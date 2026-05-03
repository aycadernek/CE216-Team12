package sportsmanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.util.List;
import java.util.Map;

public class ScheduleScreenController {

    @FXML private TableView<ScheduleMatchItem> scheduleTable;
    @FXML private TableColumn<ScheduleMatchItem, String> homeTeamCol;
    @FXML private TableColumn<ScheduleMatchItem, String> awayTeamCol;
    @FXML private TableColumn<ScheduleMatchItem, Integer> weekCol;
    @FXML private TableColumn<ScheduleMatchItem, String> homeScoreCol;
    @FXML private TableColumn<ScheduleMatchItem, String> awayScoreCol;
    @FXML private TableColumn<ScheduleMatchItem, Void> eventsCol;

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
    }

    private void refreshTable() {
        if (currentGameStatus == null || currentGameStatus.getCurrentLeague() == null) {
            return;
        }

        AbstractLeague league = currentGameStatus.getCurrentLeague();
        AbstractTeam userTeam = league.getTeamByName(currentGameStatus.getUserTeamName());

        if (userTeam == null) return;

        Map<Integer, List<AbstractMatch>> fixtures = league.getWeeklyFixtures();
        ObservableList<ScheduleMatchItem> matchData = FXCollections.observableArrayList();

        for (Map.Entry<Integer, List<AbstractMatch>> entry : fixtures.entrySet()) {
            int weekNumber = entry.getKey();
            for (AbstractMatch match : entry.getValue()) {
                if (match.getTeam1().equals(userTeam) || match.getTeam2().equals(userTeam)) {
                    matchData.add(new ScheduleMatchItem(weekNumber, match));
                }
            }
        }

        scheduleTable.setItems(matchData);
    }

    private void setupEventsColumn() {
        Callback<TableColumn<ScheduleMatchItem, Void>, TableCell<ScheduleMatchItem, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<ScheduleMatchItem, Void> call(final TableColumn<ScheduleMatchItem, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Show");

                    {
                        btn.setStyle("-fx-background-color: #174016; -fx-text-fill: white; -fx-background-radius: 15;");
                        btn.setOnAction((event) -> {
                            ScheduleMatchItem data = getTableView().getItems().get(getIndex());
                            showEventsPopUp(data.getMatch(), data.getWeek());
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            ScheduleMatchItem data = getTableView().getItems().get(getIndex());
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

    public static class ScheduleMatchItem {
        private final int week;
        private final String homeTeam;
        private final String awayTeam;
        private final String homeScore;
        private final String awayScore;
        private final AbstractMatch match;

        public ScheduleMatchItem(int week, AbstractMatch match) {
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