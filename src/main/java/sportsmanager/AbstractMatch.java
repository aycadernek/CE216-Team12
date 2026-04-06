package sportsmanager;

import java.util.List;
import java.util.ArrayList;

public abstract class AbstractMatch {
    private AbstractTeam team1;
    private AbstractTeam team2;
    private int team1Score;
    private int team2Score;
    private String result;
    private int totalPeriods;
    private int currentPeriod;
    private boolean isFinished;
    private List<String> events;

    public AbstractMatch(AbstractTeam team1, AbstractTeam team2, int totalPeriods) {
        this.team1 = team1;
        this.team2 = team2;
        this.totalPeriods = totalPeriods;
        this.team1Score = 0;
        this.team2Score = 0;
        this.currentPeriod = 0;
        this.isFinished = false;
        this.events = new ArrayList<>();
    }

    public AbstractTeam getTeam1() { return team1; }
    public void setTeam1(AbstractTeam team) { this.team1 = team; }
    
    public AbstractTeam getTeam2() { return team2; }
    public void setTeam2(AbstractTeam team) { this.team2 = team; }
    
    public int getTeam1Score() { return team1Score; }
    public void setTeam1Score(int score) { this.team1Score = score; }
    
    public int getTeam2Score() { return team2Score; }
    public void setTeam2Score(int score) { this.team2Score = score; }
    
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    
    public int getTotalPeriods() { return totalPeriods; }
    
    public int getCurrentPeriod() { return currentPeriod; }
    public void setCurrentPeriod(int period) { this.currentPeriod = period; }
    
    public boolean isFinished() { return isFinished; }
    public void setFinished(boolean finished) { this.isFinished = finished; }
    
    public List<String> getEvents() { return events; }

    
    public abstract void playPeriod();
    public abstract void createEvent();
}