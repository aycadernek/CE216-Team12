import java.util.List;

public interface ISport {

    //Getters 
    String getSportName();
    int getPlayerCount();
    int getSubstituteCount();
    int getSubstituteChangeLimit();
    int getTeamCount();
    int getPeriodCount();
    List<String> getAvailableTactics();
    int getMatchupsPerTeam();
    
    /**
    * Provides the points system for the sport.
    * Used by other components to assign points dynamically
    * instead of hardcoding values (e.g., 3/1/0 in football).
    */
    int getWinPoint();
    int getDrawPoint();
    int getLossPoint();

    //Setters
    void setSportName(String sportName);
    void setPlayerCount(int playerCount);
    void setSubstituteCount(int substituteCount);
    void setSubstituteChangeLimit(int substituteChangeLimit);
    void setTeamCount(int teamCount);
    void setWinPoint(int winPoint);
    void setDrawPoint(int drawPoint);
    void setLossPoint(int lossPoint);
    void setPeriodCount(int periodCount);
    void setAvailableTactics(List<String> availableTactics);
    void setMatchupsPerTeam(int matchupsPerTeam);

    // Core Methods
    void generateLeague();
    void generateTeams();
}