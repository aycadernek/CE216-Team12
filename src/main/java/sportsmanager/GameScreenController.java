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
    @FXML private Button backButton;

    private GameStatus gameStatus;
    private AbstractMatch currentMatch;

    @FXML
    public void initialize() {
        playPeriodButton.setOnAction(event -> handlePlayPeriod());
        finishWeekButton.setOnAction(event -> handleFinishWeek());
        substitutionButton.setOnAction(event -> showSubstitutionPopup());
        backButton.setOnAction(event -> App.showMainTabs(gameStatus));
    }

    public void setGameData(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
        this.currentMatch = findUserTeamMatch();

        if (currentMatch == null) {
            matchTitleLabel.setText("No match found");
            scoreLabel.setText("- - -");
            periodLabel.setText("Period: -");
            playPeriodButton.setDisable(true);
            eventsTextArea.setText("No upcoming match was found for your team.");
            return;
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
            showInfoPopup("Full Time",
                currentMatch.getTeam1().getName() + " " + currentMatch.getTeam1Score()
                    + " - " + currentMatch.getTeam2Score() + " " + currentMatch.getTeam2().getName()
                        + "\nResult: " + currentMatch.getResult());
            return;
        }

        if (currentMatch.isFinished()) {
            showInfoPopup("Match Finished", "This match has already finished.\nResult: " + currentMatch.getResult());
            return;
        }

        currentMatch.playPeriod();
        appendEvents();
        updateUI();

        if (currentMatch.isFinished()) {
            playPeriodButton.setDisable(true);
            showInfoPopup("Full Time", "The match has finished.\nResult: " + currentMatch.getResult());
        }
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

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Live Substitution");
        dialog.setHeaderText("Choose a player to substitute");

        ButtonType confirmButton = new ButtonType("Confirm Substitution");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, cancelButton);

        ComboBox<AbstractTeam> teamComboBox = new ComboBox<>();
        teamComboBox.getItems().addAll(currentMatch.getTeam1(), currentMatch.getTeam2());
        teamComboBox.setValue(currentMatch.getTeam1());

        ComboBox<AbstractPlayer> outPlayerComboBox = new ComboBox<>();
        ComboBox<AbstractPlayer> inPlayerComboBox = new ComboBox<>();

        teamComboBox.setOnAction(event -> {
            AbstractTeam selectedTeam = teamComboBox.getValue();
            updateSubstitutionChoices(selectedTeam, outPlayerComboBox, inPlayerComboBox);
        });

    updateSubstitutionChoices(teamComboBox.getValue(), outPlayerComboBox, inPlayerComboBox);

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 15;");
        content.getChildren().addAll(
                new Label("Team:"),
                teamComboBox,
                new Label("Player OUT:"),
                outPlayerComboBox,
                new Label("Player IN:"),
                inPlayerComboBox
    );

    dialog.getDialogPane().setContent(content);

    dialog.setResultConverter(button -> {
        if (button == confirmButton) {
            AbstractTeam selectedTeam = teamComboBox.getValue();
            AbstractPlayer outPlayer = outPlayerComboBox.getValue();
            AbstractPlayer inPlayer = inPlayerComboBox.getValue();

            if (selectedTeam == null || outPlayer == null || inPlayer == null) {
                showInfoPopup("Invalid Substitution", "Please select a team, a player out, and a player in.");
                return null;
            }

            try {
                selectedTeam.substitutePlayer(outPlayer, inPlayer);

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

        teamComboBox.setConverter(new StringConverter<AbstractTeam>() {
        @Override
        public String toString(AbstractTeam team) {
            return team == null ? "" : team.getName();
        }

        @Override
        public AbstractTeam fromString(String string) {
            return null;
        }
    });

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
                    playPeriodButton.setText("MATCH FINISHED");
                    playPeriodButton.setDisable(true);

                    substitutionButton.setDisable(true);

                    finishWeekButton.setVisible(true);
                    finishWeekButton.setManaged(true);
                } else {
                    resultLabel.setText("");
                    playPeriodButton.setDisable(false);

                    finishWeekButton.setVisible(false);
                    finishWeekButton.setManaged(false);

                    if (currentMatch.getCurrentPeriod() == 0) {
                        playPeriodButton.setText("PLAY FIRST HALF");
                        substitutionButton.setDisable(true);
                    } else if (currentMatch.getCurrentPeriod() == 1) {
                        playPeriodButton.setText("PLAY SECOND HALF");
                        substitutionButton.setDisable(false);
                    } else {
                        playPeriodButton.setText("PLAY PERIOD");
                        substitutionButton.setDisable(false);
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
        alert.showAndWait();
    }
}