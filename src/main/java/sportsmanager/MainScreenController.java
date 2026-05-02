package sportsmanager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainScreenController {

    @FXML
    private Button startButton;

    @FXML
    void handleStartGame(ActionEvent event) {
        App.showMainTabs();
    }
}
