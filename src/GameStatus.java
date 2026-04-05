public class GameStatus {
    public static final String NOT_STARTED = "NOT_STARTED";
    public static final String IN_PROGRESS = "IN_PROGRESS";
    public static final String FINISHED = "FINISHED";

    private String username;
    private ISport currentSport;
    private int currentWeek;
    //private AbstractTeam userTeam;
    //private AbstractLeague currentLeague;

    public GameStatus() {
            this.username = "";
            this.currentSport = null;
            this.currentWeek = 0;
            //this.userTeam = null;
            //this.currentLeague = null;
        }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /* public AbstractTeam getUserTeam() {
        return userTeam;
    }

    public void setUserTeam(AbstractTeam userTeam) {
        this.userTeam = userTeam;
    } */

    public ISport getCurrentSport() {
        return currentSport;
    }

    public void setCurrentSport(ISport currentSport) {
        this.currentSport = currentSport;
    }

    /* public AbstractLeague getCurrentLeague() {
        return currentLeague;
    } 
    
    public void setCurrentLeague(AbstractLeague currentLeague) {
        this.currentLeague = currentLeague;
    }*/

    public int getCurrentWeek() {
        return currentWeek;
    }

    public void setCurrentWeek(int currentWeek) {
        this.currentWeek = currentWeek;
    }

    public void startNewGame(String username, ISport sport) {
        this.username = username;
        this.currentSport = sport;
        this.currentWeek = 1;
        //more to be impemented with Abstractleague
    }

       
        
    public boolean isLeagueOver() {
        return currentWeek > 10; // to be implemented with Abstractleague
    }

        
}