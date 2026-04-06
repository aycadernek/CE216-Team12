package sportsmanager;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

public abstract class AbstractLeague {
    private String leagueName;
    private ISport sportType;
    private List<AbstractTeam> teams;
    
    private Map<Integer, List<AbstractMatch>> weeklyFixtures;
    private List<AbstractMatch> completedMatches;
    
    private int currentWeek;
    private int totalWeeks;

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
    
    public List<AbstractTeam> getTeams() { return teams; }
    public void addTeam(AbstractTeam team) {
        if (!this.teams.contains(team)) {
            this.teams.add(team);
        }
    }

    public Map<Integer, List<AbstractMatch>> getWeeklyFixtures() { return weeklyFixtures; }
    

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

    public List<AbstractMatch> getCompletedMatches() { return completedMatches; }
    
    public int getCurrentWeek() { return currentWeek; }
    public int getTotalWeeks() { return totalWeeks; }
    public void setTotalWeeks(int totalWeeks) { this.totalWeeks = totalWeeks; }

    public void advanceWeek() {
        if (currentWeek < totalWeeks) {
            currentWeek++;
        }
    }

    public abstract void createLeague();
    public abstract void playWeeklyMatch();
    public abstract void calculateLeagueResult();
}