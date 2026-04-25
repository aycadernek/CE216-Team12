package sportsmanager;

public class GameStatus {
    public static final String NOT_STARTED = "NOT_STARTED";
    public static final String IN_PROGRESS = "IN_PROGRESS";
    public static final String FINISHED = "FINISHED";

    private String username;
    private ISport currentSport;
    private AbstractLeague currentLeague;
    private String status;
    private String userTeamName;

    public GameStatus() {
            this.username = "";
            this.currentSport = null;
            this.currentLeague = null;
            this.status = NOT_STARTED;
            this.userTeamName = null;
        }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ISport getCurrentSport() {
        return currentSport;
    }

    public void setCurrentSport(ISport currentSport) {
        this.currentSport = currentSport;
    }

    public AbstractLeague getCurrentLeague() {
        return currentLeague;
    } 
    
    public void setCurrentLeague(AbstractLeague currentLeague) {
        this.currentLeague = currentLeague;
    }

    public String getStatus() {
        return status;
    }

    public String getUserTeamName() {
        return userTeamName;
    }

    public void setUserTeamName(String userTeamName) {
        this.userTeamName = userTeamName;
    }

    public int getCurrentWeek() {
        if (currentLeague != null) {
            return currentLeague.getCurrentWeek();
        }
        return 0;
    }

    public void startNewGame(String username, ISport sport, AbstractLeague league) {
        this.username = username;
        this.currentSport = sport;
        this.currentLeague = league;
        this.status = IN_PROGRESS;
    }

    public void finishGame() {
        this.status = FINISHED;
    }
            
    public boolean isLeagueOver() {
        if (currentLeague == null) {
            return false;
        }
        return currentLeague.getCurrentWeek() > currentLeague.getTotalWeeks();
    }  
}