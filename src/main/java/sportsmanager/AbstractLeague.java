package sportsmanager;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collections;
import java.util.LinkedHashMap;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonSubTypes({ @JsonSubTypes.Type(value = FootballLeague.class, name = "FootballLeague") })
@JsonSubTypes.Type(value = HandballLeague.class, name = "HandballLeague")
public abstract class AbstractLeague {
    private String leagueName;
    private ISport sportType;
    private List<AbstractTeam> teams;
    
    private Map<Integer, List<AbstractMatch>> weeklyFixtures;
    private List<AbstractMatch> completedMatches;
    
    private int currentWeek;
    private int totalWeeks;

    protected AbstractLeague() {
    }

    public AbstractLeague(String leagueName, ISport sportType) {
        this.leagueName = leagueName;
        this.sportType = sportType;
        this.teams = new ArrayList<>();
        this.weeklyFixtures = new LinkedHashMap<>(); 
        this.completedMatches = new ArrayList<>();
        this.currentWeek = 1; 
        this.totalWeeks = 0;
    }

    public String getLeagueName() { return leagueName; }
    public ISport getSportType() { return sportType; }
    
    public List<AbstractTeam> getTeams() { 
        return Collections.unmodifiableList(teams); 
    }
    
    public AbstractTeam getTeamByName(String name) {
        if (name == null) return null;
        for (AbstractTeam team : teams) {
            if (team.getName().equals(name)) {
                return team;
            }
        }
        return null;
    }
    
    public void addTeam(AbstractTeam team) {
        if (team == null || this.currentWeek > 1) {
            return;
        }
        if (!this.teams.contains(team)) {
            this.teams.add(team);
        }
    }

    public Map<Integer, List<AbstractMatch>> getWeeklyFixtures() { 
        return Collections.unmodifiableMap(weeklyFixtures); 
    }

    public void addWeeklyFixture(int week, List<AbstractMatch> matches) {
        this.weeklyFixtures.put(week, matches);
    }
    
    public void sortTeams(java.util.Comparator<AbstractTeam> comparator) {
        this.teams.sort(comparator);
    }
    

    public List<AbstractMatch> getMatchesForWeek(int week) {
        return weeklyFixtures.getOrDefault(week, new ArrayList<>());
    }
    
    public List<AbstractMatch> getAllMatches() {
        List<AbstractMatch> all = new ArrayList<>();
        for (List<AbstractMatch> weekMatches : weeklyFixtures.values()) {
            all.addAll(weekMatches);
        }
        return all;
    }

    public List<AbstractMatch> getCompletedMatches() { 
        return Collections.unmodifiableList(completedMatches); 
    }

    public void addCompletedMatch(AbstractMatch match) {
        if (!this.completedMatches.contains(match)) {
            this.completedMatches.add(match);
        }
    }
    
    public int getCurrentWeek() { return currentWeek; }
    public int getTotalWeeks() { return totalWeeks; }
    public void setTotalWeeks(int totalWeeks) { 
        if (totalWeeks >= 0) {
            this.totalWeeks = totalWeeks; 
        }
    }

    public void advanceWeek() {
        if (currentWeek <= totalWeeks) {
            currentWeek++;
        }
    }

    public abstract void createLeague();
    public abstract void playWeeklyMatch();
    public abstract void calculateLeagueResult();
}