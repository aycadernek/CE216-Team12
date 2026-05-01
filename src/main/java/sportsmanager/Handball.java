package sportsmanager;

import java.util.ArrayList;
import java.util.List;

public class Handball implements ISport{

    private String sportName;
    private int playerCount;
    private int substituteCount;
    private int substituteChangeLimit;
    private int teamCount;
    private int periodCount;
    private List<String> availableTactics;
    private int matchupsPerTeam;
    private int winPoint;
    private int drawPoint;
    private int lossPoint;

    public Handball() {
        this.sportName = "Handball";
        this.playerCount = 7;
        this.substituteCount = 7;
        this.substituteChangeLimit = 99;
        this.teamCount = 2;
        this.periodCount = 2;
        this.matchupsPerTeam = 2;
        this.winPoint = 2;
        this.drawPoint = 1;
        this.lossPoint = 0;

        this.availableTactics = new ArrayList<>();
        availableTactics.add("6-0");
        availableTactics.add("5-1");
        availableTactics.add("3-2-1");
        availableTactics.add("4-2");
    }


    // Getters:

    @Override
    public String getSportName() {return sportName;}
    @Override
    public int getPlayerCount() {return playerCount;}
    @Override
    public int getSubstituteCount() {return substituteCount;}
    @Override
    public int getSubstituteChangeLimit() {return substituteChangeLimit;}
    @Override
    public int getTeamCount() {return teamCount;}
    @Override
    public int getPeriodCount() {return periodCount;}
    @Override
    public List<String> getAvailableTactics() {return availableTactics;}
    @Override
    public int getMatchupsPerTeam() {return matchupsPerTeam;}
    @Override
    public int getWinPoints() {return winPoint;}
    @Override
    public int getDrawPoints() {return drawPoint;}
    @Override
    public int getLossPoints() {return lossPoint;}


    // Setters:

    @Override public void setSportName(String sportName) { this.sportName = sportName; }
    @Override public void setPlayerCount(int playerCount) { this.playerCount = playerCount; }
    @Override public void setSubstituteCount(int substituteCount) { this.substituteCount = substituteCount; }
    @Override public void setSubstituteChangeLimit(int limit) { this.substituteChangeLimit = limit; }
    @Override public void setTeamCount(int teamCount) { this.teamCount = teamCount; }
    @Override public void setPeriodCount(int periodCount) { this.periodCount = periodCount; }
    @Override public void setAvailableTactics(List<String> tactics) { this.availableTactics = tactics; }
    @Override public void setMatchupsPerTeam(int matchups) { this.matchupsPerTeam = matchups; }
    @Override public void setWinPoints(int winPoint) { this.winPoint = winPoint; }
    @Override public void setDrawPoints(int drawPoint) { this.drawPoint = drawPoint; }
    @Override public void setLossPoints(int lossPoint) { this.lossPoint = lossPoint; }

    // Methods:


    public void displayRules() {
        System.out.println("=== " + sportName.toUpperCase() + " RULES ===");
        System.out.println("- Players on court: " + playerCount);
        System.out.println("- Substitutes on bench: " + substituteCount);
        System.out.println("- Maximum substitutions: Unlimited (represented by " + substituteChangeLimit + ")");
        System.out.println("- Teams in a match: " + teamCount);
        System.out.println("- Matchups per team: " + matchupsPerTeam);
        System.out.println("- Point system: Win = " + winPoint + ", Draw = " + drawPoint + ", Loss = " + lossPoint);
        System.out.println("- Available tactics: " + availableTactics);
    }

    @Override
    public void generateLeague() {
        System.out.println("Generating" + sportName + " league...");
        System.out.println("Teams in one match: " + teamCount);
        System.out.println("Matchups per team: " + matchupsPerTeam);
        System.out.println("Point system: " + winPoint + "/" + drawPoint + "/" + lossPoint);

    }

    @Override
    public void generateTeams() {System.out.println("Generating" + sportName + "teams...");
        System.out.println("Players on field: " + playerCount);
        System.out.println("Substitutes: " + substituteCount);
        System.out.println("Substitute change limit: " + substituteChangeLimit);
        System.out.println("Available tactics: " + availableTactics);
    }
}
