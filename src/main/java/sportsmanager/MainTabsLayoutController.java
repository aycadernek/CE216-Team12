package sportsmanager;
import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

public class MainTabsLayoutController {

    @FXML private Label teamNameLabel;
    @FXML private Label sportTypeLabel;
    @FXML private Label weekLabel;

    @FXML private TabPane mainTabPane;
    @FXML private Tab scheduleTab;
    @FXML private Tab teamTab;
    @FXML private Tab leagueTab;

    @FXML private VBox scheduleContainer;
    @FXML private VBox teamContainer;
    @FXML private VBox leagueContainer;

    @FXML private Label nextMatchInfoLabel;
    @FXML private Button startMatchButton;
    @FXML private Button saveAndExitButton;

    private GameStatus gameStatus;

    private TeamScreenController teamScreenController;
    private LeagueScreenController leagueScreenController;
    private ScheduleScreenController scheduleScreenController;

    @FXML
    public void initialize() {
        saveAndExitButton.setOnAction(e -> {
                if (this.gameStatus != null) {
                    SaveManager saveManager = new SaveManager();
                    saveManager.saveGame(this.gameStatus);
                }
                App.showMainScreen();
            });

        startMatchButton.setOnAction(e -> {
            if (this.gameStatus == null) return;
            AbstractLeague league = this.gameStatus.getCurrentLeague();
            if (league == null) return;
            AbstractTeam userTeam = league.getTeamByName(this.gameStatus.getUserTeamName());
            if (userTeam == null) return;

            if (this.gameStatus.isLeagueOver()) {
                return;
            }

            boolean hasMatchThisWeek = false;
            for (AbstractMatch match : league.getMatchesForWeek(league.getCurrentWeek())) {
                if (match.getTeam1().equals(userTeam) || match.getTeam2().equals(userTeam)) {
                    hasMatchThisWeek = true;
                    break;
                }
            }

            if (hasMatchThisWeek) {
                if (!userTeam.isReadyToPlay()) {
                    ISport sport = this.gameStatus.getCurrentSport();
                    int requiredStarters = sport.getPlayerCount();
                    int currentStarters = userTeam.getActivePlayers().size();

                    String message = "Your team is not ready to play! Please check your starting lineup.\n\n"
                                   + "Required Starters: " + requiredStarters + "\n"
                                   + "Current Starters: " + currentStarters;
                    showAlert("Invalid Lineup", message);
                    return;
                }
                App.showGameScreen(this.gameStatus);
            } else {
                league.playWeeklyMatch();
                updateHeader();
                updateNextMatch();
                loadScheduleScreen();
                loadLeagueScreen();
                loadTeamScreen();
            }
        });    
    }

    public void setGameData(GameStatus gameStatus) {
        this.gameStatus = gameStatus; 
        updateHeader();
        loadTeamScreen();
        loadLeagueScreen();
        loadScheduleScreen();
        updateNextMatch();
    }

    private void updateHeader() {
        if (gameStatus == null) return;

        AbstractLeague league = gameStatus.getCurrentLeague();
        String userTeamName = gameStatus.getUserTeamName();

        if (userTeamName != null) {
            teamNameLabel.setText(userTeamName);
        }
        if (league != null) {
            sportTypeLabel.setText(league.getSportType() != null ? league.getSportType().getSportName() : "Unknown");
            weekLabel.setText("Week " + league.getCurrentWeek());
        }
    }

    private void loadTeamScreen() {
        try {
            if (gameStatus == null) return;
            AbstractLeague league = gameStatus.getCurrentLeague();
            AbstractTeam team = league.getTeamByName(gameStatus.getUserTeamName());
            if (team == null || league == null) return;

            team.isReadyToPlay();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/TeamScreen.fxml"));
            Parent root = loader.load();
            teamScreenController = loader.getController();
            teamScreenController.setData(gameStatus);
            
            teamContainer.getChildren().clear();
            teamContainer.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadLeagueScreen() {
        try {
            if (gameStatus == null) return;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/LeagueScreen.fxml"));
            Parent root = loader.load();
            leagueScreenController = loader.getController();
            leagueScreenController.updateUI(gameStatus);

            leagueContainer.getChildren().clear();
            leagueContainer.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadScheduleScreen() {
        try {
            if (gameStatus == null) return;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/ScheduleScreen.fxml"));
            Parent root = loader.load();
            scheduleScreenController = loader.getController();
            scheduleScreenController.updateUI(gameStatus);

            scheduleContainer.getChildren().clear();
            scheduleContainer.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateNextMatch() {
        if (gameStatus != null) {
            AbstractLeague league = gameStatus.getCurrentLeague();
            AbstractTeam team = league.getTeamByName(gameStatus.getUserTeamName());
            if (league == null || team == null) {
                nextMatchInfoLabel.setText("No upcoming match");
                return;
            }

            if (gameStatus.isLeagueOver()) {
                nextMatchInfoLabel.setText("League Finished");
                startMatchButton.setDisable(true);
                return;
            } else {
                startMatchButton.setDisable(false);
            }

            List<AbstractMatch> matches = league.getMatchesForWeek(league.getCurrentWeek());
            boolean hasMatch = false;
            for (AbstractMatch match : matches) {
                if (match.getTeam1().equals(team) || match.getTeam2().equals(team)) {
                    nextMatchInfoLabel.setText("Next: " + match.getTeam1().getName() + " vs " + match.getTeam2().getName());
                    startMatchButton.setText("Start Match");
                    hasMatch = true;
                    return;
                }
            }
            
            if (!hasMatch) {
                nextMatchInfoLabel.setText("No match this week (BYE)");
                startMatchButton.setText("Simulate Week");
            }
        } else {
            nextMatchInfoLabel.setText("No upcoming match");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setMinHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
        try {
            String css = getClass().getResource("/layouts/styles.css").toExternalForm();
            alert.getDialogPane().getStylesheets().add(css);
        } catch (Exception e) {}
        alert.showAndWait();
    }
}
