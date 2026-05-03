package sportsmanager;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class GameScreenController {

    @FXML private Label matchTitleLabel;
    @FXML private Label scoreLabel;
    @FXML private Label periodLabel;
    @FXML private Label resultLabel;
    @FXML private TextArea eventsTextArea;
    @FXML private Button playPeriodButton;
    @FXML private Button backButton;

    private GameStatus gameStatus;
    private AbstractMatch currentMatch;

    @FXML
    public void initialize() {
        playPeriodButton.setOnAction(event -> handlePlayPeriod());
        backButton.setOnAction(event -> App.showMainTabs());
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
            showInfoPopup("No Match", "There is no match available to play.");
            return;
        }

        if (currentMatch.isFinished()) {
            showInfoPopup("Match Finished", "This match has already finished.");
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
            playPeriodButton.setDisable(true);
        } else {
            resultLabel.setText("");
            playPeriodButton.setDisable(false);
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