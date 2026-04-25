package sportsmanager;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonSubTypes({ @JsonSubTypes.Type(value = Football.class, name = "Football") })
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
    int getWinPoints();
    int getDrawPoints();
    int getLossPoints();

    //Setters
    void setSportName(String sportName);
    void setPlayerCount(int playerCount);
    void setSubstituteCount(int substituteCount);
    void setSubstituteChangeLimit(int substituteChangeLimit);
    void setTeamCount(int teamCount);
    void setWinPoints(int winPoints);
    void setDrawPoints(int drawPoints);
    void setLossPoints(int lossPoints);
    void setPeriodCount(int periodCount);
    void setAvailableTactics(List<String> availableTactics);
    void setMatchupsPerTeam(int matchupsPerTeam);

    // Core Methods
    void generateLeague();
    void generateTeams();
    void displayRules();
}