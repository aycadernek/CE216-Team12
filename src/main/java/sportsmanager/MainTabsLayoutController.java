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

    @FXML private Label userNameLabel;
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
            
            if (this.gameStatus.isLeagueOver()) {
                league.resetLeague();
                updateHeader();
                updateNextMatch();
                loadScheduleScreen();
                loadLeagueScreen();
                loadTeamScreen();
                return;
            }

            AbstractTeam userTeam = league.getTeamByName(this.gameStatus.getUserTeamName());
            if (userTeam == null) return;

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
        mainTabPane.getSelectionModel().select(teamTab);
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
        String userName = gameStatus.getUsername();

        if (userName != null) {
            userNameLabel.setText(userName);
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
                if (!"New League".equals(startMatchButton.getText())) {
                    showLeagueFinishedPopup(league);
                }
                nextMatchInfoLabel.setText("League Finished");
                startMatchButton.setText("New League");
                startMatchButton.setDisable(false);
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
                startMatchButton.setText("Next Week");
            }
        } else {
            nextMatchInfoLabel.setText("No upcoming match");
        }
    }

    private void showLeagueFinishedPopup(AbstractLeague league) {
        league.calculateLeagueResult();
        List<AbstractTeam> teams = league.getTeams();
        ISport sport = gameStatus.getCurrentSport();
        
        AbstractTeam userTeam = league.getTeamByName(gameStatus.getUserTeamName());
        boolean userWon = !teams.isEmpty() && teams.get(0).equals(userTeam);

        javafx.scene.control.Dialog<Void> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("End of Season");
        dialog.setHeaderText(userWon ? "Congratulations! You Won the League! 🏆" : "The league has finished!");

        javafx.scene.control.ButtonType okButtonType = new javafx.scene.control.ButtonType("Finish", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(okButtonType);

        VBox content = new VBox(15);
        content.setAlignment(javafx.geometry.Pos.CENTER);
        content.setStyle("-fx-padding: 20px; -fx-background-color: #f4f4f4; -fx-border-radius: 10px; -fx-border-color: #dcdcdc; -fx-border-width: 2px;");

        Label titleLabel = new Label("Final Standings - Top 3");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-padding: 0 0 10 0;");
        content.getChildren().add(titleLabel);

        for (int i = 0; i < Math.min(3, teams.size()); i++) {
            AbstractTeam team = teams.get(i);
            
            javafx.scene.layout.HBox teamBox = new javafx.scene.layout.HBox(15);
            teamBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            teamBox.setStyle("-fx-background-color: white; -fx-padding: 10px 20px; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
            
            Label rankLabel = new Label("#" + (i + 1));
            rankLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + (i == 0 ? "#f1c40f" : (i == 1 ? "#95a5a6" : "#cd7f32")) + ";");
            rankLabel.setMinWidth(40);
            
            javafx.scene.image.ImageView logoView = new javafx.scene.image.ImageView();
            logoView.setFitHeight(40);
            logoView.setFitWidth(40);
            if (team.getTeamLogoPath() != null && !team.getTeamLogoPath().isEmpty()) {
                try {
                    logoView.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream(team.getTeamLogoPath())));
                } catch (Exception e) {}
            }
            
            Label nameLabel = new Label(team.getName());
            nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #34495e;");
            if (team.equals(userTeam)) {
                nameLabel.setText(team.getName() + " (YOU)");
                nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
            }
            
            int points = (team.getWinCount() * sport.getWinPoints())
                       + (team.getDrawCount() * sport.getDrawPoints())
                       + (team.getLossCount() * sport.getLossPoints());
            Label ptsLabel = new Label(points + " pts");
            ptsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #7f8c8d;");
            
            javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
            javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
            
            teamBox.getChildren().addAll(rankLabel, logoView, nameLabel, spacer, ptsLabel);
            content.getChildren().add(teamBox);
        }

        if (userWon) {
            Label congratsLabel = new Label("Incredible performance this season, Manager "+ gameStatus.getUsername()+ "!");
            congratsLabel.setStyle("-fx-font-size: 16px; -fx-font-style: italic; -fx-text-fill: #e67e22; -fx-padding: 10 0 0 0;");
            content.getChildren().add(congratsLabel);
        }

        dialog.getDialogPane().setContent(content);
        
        try {
            String css = getClass().getResource("/layouts/styles.css").toExternalForm();
            dialog.getDialogPane().getStylesheets().add(css);
        } catch (Exception e) {}
        
        dialog.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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
