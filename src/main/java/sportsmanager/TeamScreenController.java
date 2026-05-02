package sportsmanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;


public class TeamScreenController {

    @FXML private TableView<AbstractPlayer> playersTable;
    @FXML private TableColumn<AbstractPlayer, Void> infoColumn;
    @FXML private TableColumn<AbstractPlayer, String> nameColumn;
    @FXML private TableColumn<AbstractPlayer, String> statusColumn;
    @FXML private TableColumn<AbstractPlayer, String> injuryColumn;
    @FXML private TableColumn<AbstractPlayer, String> returnInColumn;

    @FXML private TableView<String> tacticsTable;
    @FXML private TableColumn<String, String> tacticNameColumn;
    @FXML private TableColumn<String, Void> tacticSelectColumn;

    @FXML private Label playedLabel;
    @FXML private Label winsLabel;
    @FXML private Label lossesLabel;
    @FXML private Label drawsLabel;

    private GameStatus gameStatus;

    public void setData(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
        updateUI();
    }

    @FXML
    public void initialize() {
        setupTable();
    }

    private void setupTable() {
        infoColumn.setCellFactory(col -> new TableCell<>() {
            private final Button infoButton = new Button("i");

            {
                infoButton.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 50;");
                infoButton.setOnAction(e -> {
                    AbstractPlayer player = getTableRow() != null ? getTableRow().getItem() : null;
                    if (player != null) {
                        showPlayerInfo(player);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(infoButton);
                }
            }
        });

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        statusColumn.setCellValueFactory(cellData -> {
            AbstractPlayer player = cellData.getValue();
            String status = "Substitute";
            if (gameStatus != null) {
                AbstractLeague league = gameStatus.getCurrentLeague();
                AbstractTeam team = league.getTeamByName(gameStatus.getUserTeamName());
                if (team != null && team.getActivePlayers().contains(player)) {
                    status = "Starter";
                }
            }
            return new javafx.beans.property.SimpleStringProperty(status);
        });

        statusColumn.setCellFactory(col -> new TableCell<>() {
            private final ComboBox<String> comboBox = new ComboBox<>(FXCollections.observableArrayList("Starter", "Substitute"));
            private boolean isUpdating = false;

            {
                comboBox.setStyle("-fx-font-size: 11px;");
                comboBox.setOnAction(event -> {
                    if (!isUpdating) {
                        String newVal = comboBox.getValue();
                        AbstractPlayer player = getTableRow() != null ? getTableRow().getItem() : null;
                        if (player != null && newVal != null) {
                            handleChangeStatus(player, newVal);
                        }
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    AbstractPlayer player = getTableRow() != null ? getTableRow().getItem() : null;
                    if (player != null) {
                        isUpdating = true;
                        if (player.isInjured()) {
                            comboBox.setDisable(true);
                            comboBox.setStyle("-fx-font-size: 11px; -fx-opacity: 0.7;");
                        } else {
                            comboBox.setDisable(false);
                            comboBox.setStyle("-fx-font-size: 11px; -fx-opacity: 1.0;");
                        }
                        
                        comboBox.setValue(item);
                        isUpdating = false;
                        
                        setGraphic(comboBox);
                        setText(null);
                    }
                }
            }
        });

        injuryColumn.setCellValueFactory(cellData -> {
            AbstractPlayer player = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(player.isInjured() ? "INJURED" : "Healthy");
        });

        returnInColumn.setCellValueFactory(cellData -> {
            AbstractPlayer player = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(player.isInjured() ? player.getInjuryRemainingDuration() + " matches" : "-");
        });

        tacticNameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()));
        
        tacticSelectColumn.setCellFactory(col -> new TableCell<>() {
            private final RadioButton radioButton = new RadioButton();
            {
                radioButton.setOnAction(e -> {
                    if (radioButton.isSelected()) {
                        String selectedTactic = getTableView().getItems().get(getIndex());
                        if (gameStatus != null) {
                            AbstractTeam team = gameStatus.getCurrentLeague().getTeamByName(gameStatus.getUserTeamName());
                            if (team == null) return;
                            team.setTactic(selectedTactic);
                            System.out.println("Tactic changed to: " + selectedTactic + " for team: " + team.getName());
                            getTableView().refresh();
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    String tacticName = getTableView().getItems().get(getIndex());
                    boolean isSelected = false;
                    if (gameStatus != null) {
                        AbstractTeam team = gameStatus.getCurrentLeague().getTeamByName(gameStatus.getUserTeamName());
                        isSelected = team != null && tacticName.equalsIgnoreCase(team.getTactic());
                    }
                    radioButton.setSelected(isSelected);
                    setGraphic(radioButton);
                }
            }
        });
    }

    private void showPlayerInfo(AbstractPlayer player) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Player Information");
        
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().add(closeButton);
        
        VBox content = new VBox(10);
        content.setStyle("-fx-background-color: #0c9aa6; -fx-padding: 20;");
        java.util.Map<String, String> allInfo = player.getAllInfo();
        String statusStr = "Substitute";
        if (gameStatus != null) {
            AbstractLeague league = gameStatus.getCurrentLeague();
            AbstractTeam team = league.getTeamByName(gameStatus.getUserTeamName());
            if (team != null && team.getActivePlayers().contains(player)) {
                statusStr = "Starter";
            }
        }
        
        
        java.util.Map<String, String> displayData = new java.util.LinkedHashMap<>();
        displayData.put("Name", allInfo.get("Name")); 
        displayData.put("Position", allInfo.get("Position"));
        displayData.put("Match Status", statusStr); 
        
        for (java.util.Map.Entry<String, String> entry : allInfo.entrySet()) {
            if (!entry.getKey().equals("Name") && !entry.getKey().equals("Position")) {
                displayData.put(entry.getKey(), entry.getValue());
            }
        }

        for (java.util.Map.Entry<String, String> entry : displayData.entrySet()) {
            Label label = new Label(entry.getKey() + ": " + entry.getValue());
            label.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
            
            if (entry.getValue().equals("INJURED")) {
                label.setStyle("-fx-text-fill: #ffcccc; -fx-font-size: 16px; -fx-font-weight: bold;");
            }
            
            content.getChildren().add(label);
        }

        alert.getDialogPane().setContent(content);
        alert.getDialogPane().setStyle("-fx-background-color: #0c9aa6; -fx-border-color: #139bb9; -fx-border-width: 3;");

        Button btn = (Button) alert.getDialogPane().lookupButton(closeButton);
        if (btn != null) {
            btn.setStyle("-fx-background-color: #234d20; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;");
        }

        alert.showAndWait();
    }

    private void handleChangeStatus(AbstractPlayer player, String newStatus) {
        if (gameStatus == null) return;
        AbstractLeague league = gameStatus.getCurrentLeague();
        AbstractTeam team = league.getTeamByName(gameStatus.getUserTeamName());
        if (team == null || league == null) return;

        if (player.isInjured()) {
            System.out.println("Cannot change status for injured player: " + player.getName());
            return;
        }

        boolean isCurrentlyStarter = team.getActivePlayers().contains(player);
        boolean wantsToBeStarter = "Starter".equals(newStatus);
        
        String oldStatus = isCurrentlyStarter ? "Starter" : "Substitute";

        if (isCurrentlyStarter && !wantsToBeStarter) {
            team.getActivePlayers().remove(player);
            if (!team.getSubstitutePlayers().contains(player)) {
                team.getSubstitutePlayers().add(player);
            }
            System.out.println("Status changed for " + player.getName() + ": " + oldStatus + " -> Substitute");
        } else if (!isCurrentlyStarter && wantsToBeStarter) {
            team.getSubstitutePlayers().remove(player);
            if (!team.getActivePlayers().contains(player)) {
                team.getActivePlayers().add(player);
            }
            System.out.println("Status changed for " + player.getName() + ": " + oldStatus + " -> Starter");
        }
        playersTable.refresh();
    }

    private void updateUI() {
        if (gameStatus == null) return;
        AbstractLeague league = gameStatus.getCurrentLeague();
        AbstractTeam team = league.getTeamByName(gameStatus.getUserTeamName());
        if (team == null || league == null) return;

        
        ObservableList<AbstractPlayer> players = FXCollections.observableArrayList(team.getAllPlayers());
        playersTable.setItems(players);

        playedLabel.setText(String.valueOf(team.getWinCount() + team.getLossCount() + team.getDrawCount()));
        winsLabel.setText(String.valueOf(team.getWinCount()));
        lossesLabel.setText(String.valueOf(team.getLossCount()));
        drawsLabel.setText(String.valueOf(team.getDrawCount()));

        updateTactics();
    }

    private void updateTactics() {
        if (gameStatus == null) return;
        AbstractLeague league = gameStatus.getCurrentLeague();
        if (league == null || league.getSportType() == null) {
            tacticsTable.setItems(FXCollections.observableArrayList());
            return;
        }

        java.util.List<String> tactics = league.getSportType().getAvailableTactics();
        if (tactics != null) {
            tacticsTable.setItems(FXCollections.observableArrayList(tactics));
            tacticsTable.refresh();
        }
    }
}