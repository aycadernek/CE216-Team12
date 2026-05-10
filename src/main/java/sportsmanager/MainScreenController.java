package sportsmanager;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class MainScreenController {

    @FXML private TextField usernameField;
    @FXML private ComboBox<String> sportComboBox;
    @FXML private Button startButton;
    @FXML private Button loadButton;
    @FXML private Button helpButton;
    @FXML private Button exitButton;
    @FXML private ImageView appLogoImageView;

    private java.io.File tempPdfFile = null;

    @FXML
    public void initialize() {
        sportComboBox.setItems(FXCollections.observableArrayList("Football", "Handball"));
        sportComboBox.setValue("Football");
        
        Image logo = new Image(getClass().getResourceAsStream("/images/app/app_logo.png"));
        appLogoImageView.setImage(logo);
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
    void handleHelp(ActionEvent event) {
        try {
            Stage helpStage = new Stage();
            helpStage.setTitle("Sports Manager - Help Manual");
            
            WebView webView = new WebView();
            String url = getClass().getResource("/help_manual.html").toExternalForm();
            
            webView.getEngine().locationProperty().addListener((obs, oldLoc, newLoc) -> {
                if (newLoc != null && newLoc.toUpperCase().endsWith(".PDF")) {
                    javafx.application.Platform.runLater(() -> webView.getEngine().load(url));
                    openHelpManualPdf();
                }
            });
            
            webView.getEngine().load(url);
            
            Scene scene = new Scene(webView, 800, 600);
            helpStage.setScene(scene);
            helpStage.show();
        } catch (Exception e) {
            showAlert("Help Error", "Could not load the help manual: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleExit(ActionEvent event) {
        System.exit(0);
    }

    private GameStatus createFootballGame(String username) {
        Football sport = new Football();
        FootballLeague league = new FootballLeague("Football League");

        int teamCount = 3 + new java.util.Random().nextInt(4); 
        java.util.List<FootballTeam> teams = DataGenerator.generateFootballTeams(teamCount);
        for (FootballTeam team : teams) {
            league.addTeam(team);
        }
        league.createLeague();

        GameStatus gameStatus = new GameStatus();
        gameStatus.startNewGame(username, sport, league);
        gameStatus.setUserTeamName(teams.get(0).getName());

        return gameStatus;
    }

    private GameStatus createHandballGame(String username) {
        Handball sport = new Handball();
        HandballLeague league = new HandballLeague("Handball League");

        int teamCount = 3 + new java.util.Random().nextInt(4); 
        java.util.List<HandballTeam> teams = DataGenerator.generateHandballTeams(teamCount);
        for (HandballTeam team : teams) {
            league.addTeam(team);
        }
        league.createLeague();

        GameStatus gameStatus = new GameStatus();
        gameStatus.startNewGame(username, sport, league);
        gameStatus.setUserTeamName(teams.get(0).getName());

        return gameStatus;
    }

    private void openHelpManualPdf() {
        try {
            if (tempPdfFile == null || !tempPdfFile.exists()) {
                java.io.InputStream in = getClass().getResourceAsStream("/USER MANUAL.pdf");
                if (in == null) {
                    showAlert("File Not Found", "The user manual PDF could not be found.");
                    return;
                }
                java.nio.file.Path tempPath = java.nio.file.Files.createTempFile("USER_MANUAL", ".pdf");
                tempPdfFile = tempPath.toFile();
                tempPdfFile.deleteOnExit();
                java.nio.file.Files.copy(in, tempPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
            
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(tempPdfFile);
            } else {
                showAlert("Not Supported", "Desktop operations are not supported on this system.");
            }
        } catch (Exception e) {
            showAlert("Error", "Could not open the PDF: " + e.getMessage());
        }
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