package sportsmanager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.List;

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

    @FXML
    public void initialize() {
        saveAndExitButton.setOnAction(e -> {
                if (this.gameStatus != null) {
                    SaveManager saveManager = new SaveManager();
                    saveManager.saveGame(this.gameStatus);
                }
                App.showMainScreen();
            });
    }

    public void setGameData(GameStatus gameStatus) {
        this.gameStatus = gameStatus; 
        updateHeader();
        loadTeamScreen();
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

    private void updateNextMatch() {
        if (gameStatus != null) {
            AbstractLeague league = gameStatus.getCurrentLeague();
            AbstractTeam team = league.getTeamByName(gameStatus.getUserTeamName());
            if (league == null || team == null) return;

            List<AbstractMatch> matches = league.getMatchesForWeek(league.getCurrentWeek());
            for (AbstractMatch match : matches) {
                if (match.getTeam1().equals(team) || match.getTeam2().equals(team)) {
                    nextMatchInfoLabel.setText("Next: " + match.getTeam1().getName() + " vs " + match.getTeam2().getName());
                    return;
                }
            }
        }
        nextMatchInfoLabel.setText("No upcoming match");
    }
}
