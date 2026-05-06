package sportsmanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showMainScreen();
    }

    public static void showMainScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/layouts/MainScreen.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 800, 750);
            primaryStage.setTitle("Sports Manager");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showMainTabs() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/layouts/MainTabsLayout.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 800, 750);
            primaryStage.setTitle("Sports Manager");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showMainTabs(GameStatus gameStatus) {
    try {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/layouts/MainTabsLayout.fxml"));
        Parent root = loader.load();

        MainTabsLayoutController controller = loader.getController();
        controller.setGameData(gameStatus);

        Scene scene = new Scene(root, 800, 750);
        primaryStage.setTitle("Sports Manager");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

    } catch (Exception e) {
        e.printStackTrace();
    }
}

    public static void showGameScreen(GameStatus gameStatus) {
    try {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/layouts/GameScreen.fxml"));
        Parent root = loader.load();

        GameScreenController controller = loader.getController();
        controller.setGameData(gameStatus);

        Scene scene = new Scene(root, 800, 750);
        primaryStage.setTitle("Sports Manager - Match");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

    } catch (Exception e) {
        e.printStackTrace();
    }
}

    public static void main(String[] args) {
        launch(args);
    }
}
