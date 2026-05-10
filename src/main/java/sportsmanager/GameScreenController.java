package sportsmanager;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class GameScreenController {

    @FXML private Label matchTitleLabel;
    @FXML private Label scoreLabel;
    @FXML private Label periodLabel;
    @FXML private Label resultLabel;
    @FXML private TextArea eventsTextArea;
    @FXML private Button playPeriodButton;
    @FXML private Button finishWeekButton;
    @FXML private Button substitutionButton;
    @FXML private Button tacticButton;
    @FXML private Button backButton;
    @FXML private ImageView homeTeamLogo;
    @FXML private ImageView awayTeamLogo;

    private GameStatus gameStatus;
    private AbstractMatch currentMatch;
    private int userSubsUsed;

    @FXML
    public void initialize() {
        playPeriodButton.setOnAction(event -> handlePlayPeriod());
        finishWeekButton.setOnAction(event -> handleFinishWeek());
        substitutionButton.setOnAction(event -> showSubstitutionPopup());
        tacticButton.setOnAction(event -> showTacticChangePopup());
        backButton.setOnAction(event -> App.showMainTabs(gameStatus));
    }

    public void setGameData(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
        this.currentMatch = findUserTeamMatch();
        this.userSubsUsed = 0;

        if (currentMatch == null) {
            matchTitleLabel.setText("No match found");
            scoreLabel.setText("- - -");
            periodLabel.setText("Period: -");
            playPeriodButton.setDisable(true);
            eventsTextArea.setText("No upcoming match was found for your team.");
            return;
        }
        if (currentMatch.getTeam1().getTeamLogoPath() != null && !currentMatch.getTeam1().getTeamLogoPath().isEmpty()) {
            homeTeamLogo.setImage(new Image(getClass().getResourceAsStream(currentMatch.getTeam1().getTeamLogoPath())));
        }

        if (currentMatch.getTeam2().getTeamLogoPath() != null && !currentMatch.getTeam2().getTeamLogoPath().isEmpty()) {
            awayTeamLogo.setImage(new Image(getClass().getResourceAsStream(currentMatch.getTeam2().getTeamLogoPath())));
        }

        updateUI();
        eventsTextArea.setText("Match is ready. Click PLAY FIRST HALF to begin.\n");
    }

    private AbstractMatch findUserTeamMatch() {
        if (gameStatus == null || gameStatus.getCurrentLeague() == null) {
            return null;
        }

        AbstractLeague league = gameStatus.getCurrentLeague();
        String userTeamName = gameStatus.getUserTeamName();

        if (userTeamName == null) {
            return null;
        }

        AbstractTeam userTeam = league.getTeamByName(userTeamName);

        if (userTeam == null) {
            return null;
        }

        List<AbstractMatch> matches = league.getMatchesForWeek(league.getCurrentWeek());

        for (AbstractMatch match : matches) {
            if (match.getTeam1().equals(userTeam) || match.getTeam2().equals(userTeam)) {
                return match;
            }
        }

        return null;
    }

    private void handlePlayPeriod() {
        if (currentMatch == null) {
            showInfoPopup("No Match", "There is no active match to play.");
            return;
        }

        if (currentMatch.isFinished()) {
            showInfoPopup("Match Finished", "This match has already finished.\nResult: " + currentMatch.getResult());
            return;
        }

        backButton.setDisable(true);
        playPeriodButton.setDisable(true);
        substitutionButton.setDisable(true);
        tacticButton.setDisable(true);
        finishWeekButton.setDisable(true);

        int oldSize = currentMatch.getEvents().size();
        currentMatch.playPeriod();
        
        java.util.List<String> newEvents = new java.util.ArrayList<>(currentMatch.getEvents().subList(oldSize, currentMatch.getEvents().size()));

        javafx.animation.Timeline timeline = new javafx.animation.Timeline();
        for (int i = 0; i < newEvents.size(); i++) {
            String ev = newEvents.get(i);
            timeline.getKeyFrames().add(new javafx.animation.KeyFrame(javafx.util.Duration.millis(500 * (i + 1)), e -> {
                eventsTextArea.appendText(ev + "\n");
                eventsTextArea.positionCaret(eventsTextArea.getText().length());
            }));
        }
        timeline.setOnFinished(e -> {
            updateUI();
            if (currentMatch.isFinished()) {
                javafx.application.Platform.runLater(() -> {
                    showInfoPopup(
                            "Full Time",
                            currentMatch.getTeam1().getName() + " " + currentMatch.getTeam1Score()
                                    + " - " + currentMatch.getTeam2Score() + " " + currentMatch.getTeam2().getName()
                                    + "\nResult: " + currentMatch.getResult()
                    );
                });
            }
        });
        timeline.play();
    }

    private void handleFinishWeek() {

        if (gameStatus == null || gameStatus.getCurrentLeague() == null) {

            showInfoPopup("Error", "No active league was found.");

            return;

        }

        AbstractLeague league = gameStatus.getCurrentLeague();

        league.playWeeklyMatch();

        showInfoPopup("Week Finished", "The current week has been completed.");

        App.showMainTabs(gameStatus);

    }

    private void showSubstitutionPopup() {
    if (currentMatch == null) {
        showInfoPopup("No Match", "There is no active match for substitution.");
        return;
    }

    if (currentMatch.getCurrentPeriod() == 0) {
        showInfoPopup("Match Not Started", "Substitutions can only be made after the match has started.");
        return;
    }

    if (currentMatch.isFinished()) {
        showInfoPopup("Match Finished", "Substitutions cannot be made after the match is finished.");
        return;
    }

        int subLimit = gameStatus.getCurrentSport().getSubstituteChangeLimit();
        if (userSubsUsed >= subLimit) {
            showInfoPopup("Substitution Limit Reached", "You have reached the maximum substitution limit (" + subLimit + ") for this match.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Live Substitution");
        String remainingText = (subLimit == Integer.MAX_VALUE) ? "Unlimited" : String.valueOf(subLimit - userSubsUsed);
        dialog.setHeaderText("Choose a player to substitute (Remaining: " + remainingText + ")");

        ButtonType confirmButton = new ButtonType("Confirm Substitution",ButtonBar.ButtonData.LEFT);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, cancelButton);

        AbstractTeam userTeam = currentMatch.getTeam1().getName().equals(gameStatus.getUserTeamName()) ? currentMatch.getTeam1() : currentMatch.getTeam2();

        Label teamLabel = new Label(userTeam.getName());
        teamLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #e9ff7a;");

        ComboBox<AbstractPlayer> outPlayerComboBox = new ComboBox<>();
        outPlayerComboBox.setConverter(new StringConverter<AbstractPlayer>() {
            @Override
            public String toString(AbstractPlayer player) {
                return player == null ? "" : player.getName() + " (" + player.getPosition() + ")";
            }

            @Override
            public AbstractPlayer fromString(String string) {
                return null;
            }
        });

        ComboBox<AbstractPlayer> inPlayerComboBox = new ComboBox<>();
        inPlayerComboBox.setConverter(new StringConverter<AbstractPlayer>() {
            @Override
            public String toString(AbstractPlayer player) {
                return player == null ? "" : player.getName() + " (" + player.getPosition() + ")";
            }

            @Override
            public AbstractPlayer fromString(String string) {
                return null;
            }
        });

        updateSubstitutionChoices(userTeam, outPlayerComboBox, inPlayerComboBox);

        VBox content = new VBox(10);
        content.getChildren().addAll(
                new Label("Team:"),
                teamLabel,
                new Label("Player OUT:"),
                outPlayerComboBox,
                new Label("Player IN:"),
                inPlayerComboBox
    );

        dialog.getDialogPane().setContent(content);
        

        try {
            String css = getClass().getResource("/layouts/styles.css").toExternalForm();
            dialog.getDialogPane().getStylesheets().add(css);
        } catch (Exception e) {
        }

    dialog.setResultConverter(button -> {
        if (button == confirmButton) {
            AbstractTeam selectedTeam = userTeam;
            AbstractPlayer outPlayer = outPlayerComboBox.getValue();
            AbstractPlayer inPlayer = inPlayerComboBox.getValue();

            if (selectedTeam == null || outPlayer == null || inPlayer == null) {
                showInfoPopup("Invalid Substitution", "Please select a team, a player out, and a player in.");
                return null;
            }

            try {
                selectedTeam.substitutePlayer(outPlayer, inPlayer);
                
                userSubsUsed++;

                String eventText = "SUBSTITUTION: "
                        + selectedTeam.getName()
                        + " - "
                        + outPlayer.getName()
                        + " out, "
                        + inPlayer.getName()
                        + " in.";

                currentMatch.getEvents().add(eventText);
                appendEvents();

                showInfoPopup("Substitution Complete", eventText);

            } catch (IllegalArgumentException exception) {
                showInfoPopup("Substitution Failed", exception.getMessage());
            }
        }

        return null;
    });

        dialog.showAndWait();
    }

    private void showTacticChangePopup() {
        if (currentMatch == null) {
            showInfoPopup("No Match", "There is no active match for tactic change.");
            return;
        }

        if (currentMatch.getCurrentPeriod() == 0) {
            showInfoPopup("Match Not Started", "Tactics can be changed only at half-time.");
            return;
        }

        if (currentMatch.isFinished()) {
            showInfoPopup("Match Finished", "Tactics cannot be changed after the match is finished.");
            return;
        }

        if (gameStatus == null || gameStatus.getCurrentLeague() == null
                || gameStatus.getCurrentLeague().getSportType() == null) {
            showInfoPopup("Error", "No sport tactic data was found.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Half-Time Tactic Change");
        dialog.setHeaderText("Choose a team and a new tactic");

        ButtonType confirmButton = new ButtonType("Confirm Tactic",ButtonBar.ButtonData.LEFT);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, cancelButton);

        AbstractTeam userTeam = currentMatch.getTeam1().getName().equals(gameStatus.getUserTeamName()) ? currentMatch.getTeam1() : currentMatch.getTeam2();

        Label teamLabel = new Label(userTeam.getName() + " - Current: " + userTeam.getTactic());
        teamLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #e9ff7a;");

        ComboBox<String> tacticComboBox = new ComboBox<>();
        tacticComboBox.setItems(FXCollections.observableArrayList(
                gameStatus.getCurrentLeague().getSportType().getAvailableTactics()
        ));

        if (gameStatus.getCurrentLeague().getSportType().getAvailableTactics().contains(userTeam.getTactic())) {
            tacticComboBox.setValue(userTeam.getTactic());
        } else if (!tacticComboBox.getItems().isEmpty()) {
            tacticComboBox.setValue(tacticComboBox.getItems().get(0));
        }

        VBox content = new VBox(10);
        content.getChildren().addAll(
                new Label("Team:"),
                teamLabel,
                new Label("New Tactic:"),
                tacticComboBox
        );

        dialog.getDialogPane().setContent(content);

        try {
            String css = getClass().getResource("/layouts/styles.css").toExternalForm();
            dialog.getDialogPane().getStylesheets().add(css);
        } catch (Exception e) {
        }

        dialog.setResultConverter(button -> {
            if (button == confirmButton) {
                AbstractTeam selectedTeam = userTeam;
                String selectedTactic = tacticComboBox.getValue();

                if (selectedTeam == null || selectedTactic == null || selectedTactic.trim().isEmpty()) {
                    showInfoPopup("Invalid Tactic", "Please select a team and a tactic.");
                    return null;
                }

                String oldTactic = selectedTeam.getTactic();
                
                if (oldTactic != null && oldTactic.equals(selectedTactic)) {
                    showInfoPopup("No Change", "The team is already using the '" + selectedTactic + "' tactic.");
                    return null; 
                }

                try {
                    selectedTeam.changeTactic(selectedTactic);

                    String eventText = "TACTIC CHANGE: "
                            + selectedTeam.getName()
                            + " changed tactic from "
                            + oldTactic
                            + " to "
                            + selectedTactic
                            + ".";

                    currentMatch.getEvents().add(eventText);
                    appendEvents();

                    showInfoPopup("Tactic Changed", eventText);

                } catch (IllegalArgumentException exception) {
                    showInfoPopup("Tactic Change Failed", exception.getMessage());
                }
            }

            return null;
    });

    dialog.showAndWait();
}

        private void updateSubstitutionChoices(
                AbstractTeam selectedTeam,
                ComboBox<AbstractPlayer> outPlayerComboBox,
                ComboBox<AbstractPlayer> inPlayerComboBox
        ) {
            if (selectedTeam == null) {
                outPlayerComboBox.setItems(FXCollections.observableArrayList());
                inPlayerComboBox.setItems(FXCollections.observableArrayList());
                return;
            }

            outPlayerComboBox.setItems(FXCollections.observableArrayList(selectedTeam.getActivePlayers()));
            inPlayerComboBox.setItems(FXCollections.observableArrayList(selectedTeam.getSubstitutePlayers()));

            if (!selectedTeam.getActivePlayers().isEmpty()) {
                outPlayerComboBox.setValue(selectedTeam.getActivePlayers().get(0));
            }

            if (!selectedTeam.getSubstitutePlayers().isEmpty()) {
                inPlayerComboBox.setValue(selectedTeam.getSubstitutePlayers().get(0));
            }
        }
            
        private void updateUI() {
            if (currentMatch == null) {
                return;
            }

            matchTitleLabel.setText(
                    currentMatch.getTeam1().getName() + " vs " + currentMatch.getTeam2().getName()
            );

            scoreLabel.setText(
                    currentMatch.getTeam1Score() + " - " + currentMatch.getTeam2Score()
            );

            periodLabel.setText(
                    "Period: " + currentMatch.getCurrentPeriod() + " / " + currentMatch.getTotalPeriods()
            );

            if (currentMatch.isFinished()) {
                resultLabel.setText("Result: " + currentMatch.getResult());
                playPeriodButton.setVisible(false);
                playPeriodButton.setManaged(false);

                substitutionButton.setDisable(true);
                tacticButton.setDisable(true);

                finishWeekButton.setVisible(true);
                finishWeekButton.setManaged(true);
                finishWeekButton.setDisable(false);
                
                backButton.setDisable(true);
            } else {
                resultLabel.setText("");
                playPeriodButton.setDisable(false);

                finishWeekButton.setVisible(false);
                finishWeekButton.setManaged(false);
                
                backButton.setDisable(currentMatch.getCurrentPeriod() > 0);

                if (currentMatch.getCurrentPeriod() == 0) {
                    playPeriodButton.setText("PLAY FIRST HALF");
                    substitutionButton.setDisable(true);
                    tacticButton.setDisable(true);

                } else if (currentMatch.getCurrentPeriod() == 1) {
                    playPeriodButton.setText("PLAY SECOND HALF");
                    substitutionButton.setDisable(false);
                    tacticButton.setDisable(false);

                } else {
                    playPeriodButton.setText("PLAY PERIOD");
                    substitutionButton.setDisable(false);
                    tacticButton.setDisable(true);
                }
            }
        }

    private void appendEvents() {
        if (currentMatch == null) {
            return;
        }

        StringBuilder builder = new StringBuilder();

        for (String event : currentMatch.getEvents()) {
            builder.append(event).append("\n");
        }

        eventsTextArea.setText(builder.toString());
        eventsTextArea.positionCaret(eventsTextArea.getText().length());
    }

    private void showInfoPopup(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setMinWidth(700);

        try {
            String css = getClass().getResource("/layouts/styles.css").toExternalForm();
            alert.getDialogPane().getStylesheets().add(css);
        } catch (Exception e) {
        }

        alert.showAndWait();
    }
}
