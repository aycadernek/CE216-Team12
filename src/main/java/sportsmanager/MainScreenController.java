package sportsmanager;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class MainScreenController {

    @FXML private TextField usernameField;
    @FXML private ComboBox<String> sportComboBox;
    @FXML private Button startButton;
    @FXML private Button loadButton;
    @FXML private Button exitButton;

    @FXML
    public void initialize() {
        sportComboBox.setItems(FXCollections.observableArrayList("Football", "Handball"));
        sportComboBox.setValue("Football");
    }

    @FXML
    void handleStartGame(ActionEvent event) {
        String username = usernameField.getText();
        String selectedSport = sportComboBox.getValue();

        if (username == null || username.trim().isEmpty()) {
            showAlert("Missing Username", "Please enter a manager name before starting the game.");
            return;
        }

        if (selectedSport == null || selectedSport.trim().isEmpty()) {
            showAlert("Missing Sport", "Please choose a sport before starting the game.");
            return;
        }

        GameStatus gameStatus;

        if (selectedSport.equals("Football")) {
            gameStatus = createFootballGame(username.trim());
        } else if (selectedSport.equals("Handball")) {
            gameStatus = createHandballGame(username.trim());
        } else {
            showAlert("Invalid Sport", "Selected sport is not supported.");
            return;
        }

        App.showMainTabs(gameStatus);
    }

    @FXML
    void handleLoadGame(ActionEvent event) {
        SaveManager saveManager = new SaveManager();
        GameStatus loadedGame = saveManager.loadGame();

        if (loadedGame == null) {
            showAlert("Load Failed", "No saved game could be loaded.");
            return;
        }

        App.showMainTabs(loadedGame);
    }

    @FXML
    void handleExit(ActionEvent event) {
        System.exit(0);
    }

    private GameStatus createFootballGame(String username) {
        Football sport = new Football();
        FootballLeague league = new FootballLeague("Football League");

        FootballTeam team1 = DataGenerator.generateFootballTeam();
        FootballTeam team2 = DataGenerator.generateFootballTeam();

        league.addTeam(team1);
        league.addTeam(team2);
        league.createLeague();

        GameStatus gameStatus = new GameStatus();
        gameStatus.startNewGame(username, sport, league);
        gameStatus.setUserTeamName(team1.getName());

        return gameStatus;
    }

    private GameStatus createHandballGame(String username) {
        Handball sport = new Handball();
        HandballLeague league = new HandballLeague("Handball League");

        HandballTeam team1 = DataGenerator.generateHandballTeam();
        HandballTeam team2 = DataGenerator.generateHandballTeam();

        league.addTeam(team1);
        league.addTeam(team2);
        league.createLeague();

        GameStatus gameStatus = new GameStatus();
        gameStatus.startNewGame(username, sport, league);
        gameStatus.setUserTeamName(team1.getName());

        return gameStatus;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}