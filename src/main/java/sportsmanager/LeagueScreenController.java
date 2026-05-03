package sportsmanager;

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
        AbstractLeague league = currentGameStatus.getCurrentLeague();
        if (currentGameStatus.isLeagueOver()) {

            league.calculateLeagueResult();
            AbstractTeam winner = league.getTeams().get(0);

            winnerLabel.setText("WINNER : " + winner.getName().toUpperCase());
            seasonResultsBtn.setDisable(false);
        } else {
            winnerLabel.setText("WINNER : PENDING");
            seasonResultsBtn.setDisable(true);
        }
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
        Alert popUp = new Alert(Alert.AlertType.INFORMATION);
        popUp.setTitle("Match Events");
        popUp.setHeaderText("Week " + week + ": " + match.getTeam1().getName() + " vs " + match.getTeam2().getName());

        StringBuilder eventsList = new StringBuilder();
        if (match.getEvents().isEmpty()) {
            eventsList.append("No events recorded for this match.");
        } else {
            for (String event : match.getEvents()) {
                eventsList.append(event).append("\n");
            }
        }

        popUp.setContentText(eventsList.toString());
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