package sportsmanager;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.util.List;
import java.util.Map;

public class LeagueScreenController {

    @FXML private Label winnerLabel;
    @FXML private Button seasonResultsBtn;

    @FXML private TableView<LeagueMatchItem> leagueTable;
    @FXML private TableColumn<LeagueMatchItem, String> homeTeamCol;
    @FXML private TableColumn<LeagueMatchItem, String> awayTeamCol;
    @FXML private TableColumn<LeagueMatchItem, Integer> weekCol;
    @FXML private TableColumn<LeagueMatchItem, String> homeScoreCol;
    @FXML private TableColumn<LeagueMatchItem, String> awayScoreCol;
    @FXML private TableColumn<LeagueMatchItem, Void> eventsCol;

    private GameStatus currentGameStatus;

    @FXML
    public void initialize() {
        homeTeamCol.setCellValueFactory(new PropertyValueFactory<>("homeTeam"));
        awayTeamCol.setCellValueFactory(new PropertyValueFactory<>("awayTeam"));
        weekCol.setCellValueFactory(new PropertyValueFactory<>("week"));
        homeScoreCol.setCellValueFactory(new PropertyValueFactory<>("homeScore"));
        awayScoreCol.setCellValueFactory(new PropertyValueFactory<>("awayScore"));

        setupEventsColumn();
    }

    public void updateUI(GameStatus gameStatus) {
        this.currentGameStatus = gameStatus;
        refreshTable();
        checkWinner();
    }

    @FXML
    private void handleSeasonResults() {
        if (currentGameStatus == null || currentGameStatus.getCurrentLeague() == null) {
            return;
        }

        if (!currentGameStatus.isLeagueOver()) {
            showInfoPopup("Season Results", "Season results are only available after the league is finished.");
            return;
        }

        AbstractLeague league = currentGameStatus.getCurrentLeague();
        league.calculateLeagueResult();
        showSeasonResultsPopup();
    }

    private void refreshTable() {
        if (currentGameStatus == null || currentGameStatus.getCurrentLeague() == null) {
            return;
        }

        AbstractLeague league = currentGameStatus.getCurrentLeague();
        Map<Integer, List<AbstractMatch>> fixtures = league.getWeeklyFixtures();

        ObservableList<LeagueMatchItem> matchData = FXCollections.observableArrayList();

        for (Map.Entry<Integer, List<AbstractMatch>> entry : fixtures.entrySet()) {
            int weekNumber = entry.getKey();
            for (AbstractMatch match : entry.getValue()) {
                matchData.add(new LeagueMatchItem(weekNumber, match));
            }
        }

        leagueTable.setItems(matchData);
    }

    private void checkWinner() {
        if (currentGameStatus == null || currentGameStatus.getCurrentLeague() == null) {
            winnerLabel.setText("WINNER : PENDING");
            seasonResultsBtn.setDisable(true);
            return;
        }

        AbstractLeague league = currentGameStatus.getCurrentLeague();
        if (currentGameStatus.isLeagueOver()) {

            league.calculateLeagueResult();
            if (!league.getTeams().isEmpty()) {
                AbstractTeam winner = league.getTeams().get(0);
                winnerLabel.setText("WINNER : " + winner.getName().toUpperCase());
                seasonResultsBtn.setDisable(false);
            } else {
                winnerLabel.setText("WINNER : PENDING");
                seasonResultsBtn.setDisable(true);
            }
        } else {
            winnerLabel.setText("WINNER : PENDING");
            seasonResultsBtn.setDisable(true);
        }
    }

    private void showSeasonResultsPopup() {
        if (currentGameStatus == null || currentGameStatus.getCurrentLeague() == null) return;

        AbstractLeague league = currentGameStatus.getCurrentLeague();
        ISport sport = currentGameStatus.getCurrentSport();

        Alert popUp = new Alert(Alert.AlertType.NONE);
        popUp.setTitle("Season Results");
        popUp.getDialogPane().setId("seasonResultsPopup");

        try {
            java.net.URL cssUrl = getClass().getResource("/layouts/styles.css");
            if (cssUrl != null) {
                popUp.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
            }
        } catch (Exception e) {}

        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        popUp.getButtonTypes().add(closeButton);

        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(10);
        content.setStyle("-fx-background-color: #0c9aa6; -fx-padding: 20;");

        content.setAlignment(javafx.geometry.Pos.CENTER);

        Label titleLabel = new Label("🏆 Final Standings - " + league.getLeagueName());
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        content.getChildren().add(titleLabel);

        TableView<AbstractTeam> table = new TableView<>();
        table.getStyleClass().add("season-results-table");
        table.setStyle("-fx-background-color: #0c9aa6; -fx-control-inner-background: #0c9aa6; -fx-border-color: #139bb9; -fx-border-width: 2;");

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<AbstractTeam, Integer> rankCol = new TableColumn<>("#");
        rankCol.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(table.getItems().indexOf(column.getValue()) + 1));
        rankCol.setMinWidth(40);
        rankCol.setMaxWidth(40);
        rankCol.setResizable(false);

        TableColumn<AbstractTeam, String> teamCol = new TableColumn<>("Team");
        teamCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        teamCol.setMinWidth(200);
        teamCol.setPrefWidth(200);
        teamCol.setResizable(false);
        teamCol.getStyleClass().add("team-name-column");

        TableColumn<AbstractTeam, Integer> playedCol = new TableColumn<>("PL");
        playedCol.setCellValueFactory(data -> new SimpleIntegerProperty(
                data.getValue().getWinCount() + data.getValue().getDrawCount() + data.getValue().getLossCount()
        ).asObject());
        playedCol.setMinWidth(45);
        playedCol.setMaxWidth(45);
        playedCol.setResizable(false);

        TableColumn<AbstractTeam, Integer> winCol = new TableColumn<>("W");
        winCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getWinCount()).asObject());
        winCol.setMinWidth(45);
        winCol.setMaxWidth(45);
        winCol.setResizable(false);

        TableColumn<AbstractTeam, Integer> drawCol = new TableColumn<>("D");
        drawCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getDrawCount()).asObject());
        drawCol.setMinWidth(45);
        drawCol.setMaxWidth(45);
        drawCol.setResizable(false);

        TableColumn<AbstractTeam, Integer> lossCol = new TableColumn<>("L");
        lossCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getLossCount()).asObject());
        lossCol.setMinWidth(45);
        lossCol.setMaxWidth(45);
        lossCol.setResizable(false);

        TableColumn<AbstractTeam, Integer> ptsCol = new TableColumn<>("Pts");
        ptsCol.setCellValueFactory(data -> {
            int points = (data.getValue().getWinCount() * sport.getWinPoints()) +
                    (data.getValue().getDrawCount() * sport.getDrawPoints()) +
                    (data.getValue().getLossCount() * sport.getLossPoints());
            return new SimpleIntegerProperty(points).asObject();
        });
        ptsCol.setMinWidth(55);
        ptsCol.setMaxWidth(55);
        ptsCol.setResizable(false);

        table.setRowFactory(tv -> new TableRow<AbstractTeam>() {
            @Override
            protected void updateItem(AbstractTeam team, boolean empty) {
                super.updateItem(team, empty);
                if (team == null || empty) {
                    setStyle("-fx-background-color: #0c9aa6; -fx-border-color: transparent;");
                } else {
                    setStyle("-fx-background-color: #0c9aa6; -fx-border-color: #139bb9; -fx-border-width: 0 0 1 0;");
                }
            }
        });

        table.getColumns().addAll(rankCol, teamCol, playedCol, winCol, drawCol, lossCol, ptsCol);
        table.getItems().addAll(league.getTeams());

        int rowHeight = 30;
        int headerHeight = 32;
        table.setFixedCellSize(rowHeight);

        table.setPrefWidth(477);
        table.setMinWidth(477);
        table.setMaxWidth(477);

        double totalTableHeight = (league.getTeams().size() * rowHeight) + headerHeight + 5;
        table.setPrefHeight(totalTableHeight);
        table.setMinHeight(totalTableHeight);

        content.getChildren().add(table);

        popUp.getDialogPane().setContent(content);
        popUp.getDialogPane().setPrefWidth(517);
        popUp.getDialogPane().setMinWidth(517);
        popUp.getDialogPane().setMaxWidth(517);
        popUp.getDialogPane().setStyle("-fx-background-color: #0c9aa6; -fx-border-color: #139bb9; -fx-border-width: 3;");

        Button btn = (Button) popUp.getDialogPane().lookupButton(closeButton);
        if (btn != null) {
            btn.setStyle("-fx-background-color: #234d20; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;");
        }

        popUp.showAndWait();
    }

    private void showInfoPopup(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setMinHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    private void setupEventsColumn() {
        Callback<TableColumn<LeagueMatchItem, Void>, TableCell<LeagueMatchItem, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<LeagueMatchItem, Void> call(final TableColumn<LeagueMatchItem, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Show");

                    {
                        btn.setStyle("-fx-background-color: #174016; -fx-text-fill: white; -fx-background-radius: 15;");
                        btn.setOnAction((event) -> {
                            LeagueMatchItem data = getTableView().getItems().get(getIndex());
                            showEventsPopUp(data.getMatch(), data.getWeek());
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            LeagueMatchItem data = getTableView().getItems().get(getIndex());
                            btn.setDisable(!data.getMatch().isFinished());
                            setGraphic(btn);
                        }
                    }
                };
            }
        };
        eventsCol.setCellFactory(cellFactory);
    }

    private void showEventsPopUp(AbstractMatch match, int week) {
        Alert popUp = new Alert(Alert.AlertType.NONE);
        popUp.setTitle("Match Events");

        String css = getClass().getResource("/layouts/styles.css").toExternalForm();
        popUp.getDialogPane().getStylesheets().add(css);

        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        popUp.getButtonTypes().add(closeButton);

        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(10);
        content.setStyle("-fx-background-color: #0c9aa6; -fx-padding: 20;");

        Label titleLabel = new Label("Week " + week + ": " + match.getTeam1().getName() + " vs " + match.getTeam2().getName());
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        content.getChildren().add(titleLabel);

        javafx.scene.layout.VBox eventsBox = new javafx.scene.layout.VBox(5);
        eventsBox.setStyle("-fx-background-color: #0c9aa6;");

        if (match.getEvents().isEmpty()) {
            Label emptyLabel = new Label("No events recorded for this match.");
            emptyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-style: italic;");
            eventsBox.getChildren().add(emptyLabel);
        } else {
            for (String event : match.getEvents()) {
                Label lbl = new Label(event);
                lbl.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                lbl.setWrapText(true);
                eventsBox.getChildren().add(lbl);
            }
        }

        ScrollPane scrollPane = new ScrollPane(eventsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(500, 350);
        scrollPane.setStyle("-fx-background: #0c9aa6; -fx-control-inner-background: #0c9aa6; -fx-border-color: #139bb9; -fx-border-width: 2;");

        content.getChildren().add(scrollPane);

        popUp.getDialogPane().setContent(content);
        popUp.getDialogPane().setStyle("-fx-background-color: #0c9aa6; -fx-border-color: #139bb9; -fx-border-width: 3;");

        Button btn = (Button) popUp.getDialogPane().lookupButton(closeButton);
        if (btn != null) {
            btn.setStyle("-fx-background-color: #234d20; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;");
        }

        popUp.showAndWait();
    }

    public static class LeagueMatchItem {
        private final int week;
        private final String homeTeam;
        private final String awayTeam;
        private final String homeScore;
        private final String awayScore;
        private final AbstractMatch match;

        public LeagueMatchItem(int week, AbstractMatch match) {
            this.week = week;
            this.match = match;
            this.homeTeam = match.getTeam1().getName();
            this.awayTeam = match.getTeam2().getName();

            if (match.isFinished()) {
                this.homeScore = String.valueOf(match.getTeam1Score());
                this.awayScore = String.valueOf(match.getTeam2Score());
            } else {
                this.homeScore = "-";
                this.awayScore = "-";
            }
        }

        public int getWeek() { return week; }
        public String getHomeTeam() { return homeTeam; }
        public String getAwayTeam() { return awayTeam; }
        public String getHomeScore() { return homeScore; }
        public String getAwayScore() { return awayScore; }
        public AbstractMatch getMatch() { return match; }
    }
}