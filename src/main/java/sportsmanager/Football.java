package sportsmanager;

import java.util.ArrayList;
import java.util.List;

public class Football implements ISport {
    // NOTE (Future Enhancement):
    // Substitution rules can be expanded to include advanced scenarios
    // such as substitution windows, halftime exceptions, and extra-time rules.
    // This will be discussed with the team and implemented later if required.


    private String sportName;
    private int playerCount;
    private int substituteCount;
    private int substituteChangeLimit;
    private int teamCount;
    private int periodCount;
    private List<String> availableTactics;
    private int matchupsPerTeam;


    private int winPoints;
    private int drawPoints;
    private int lossPoints;

    public Football() {
        
        this.sportName = "Football";
        this.playerCount = 11;
        this.substituteCount = 7;
        this.substituteChangeLimit = 5;
        this.teamCount = 2;
        this.periodCount = 2; // 2 halves
        this.matchupsPerTeam = 2; // home & away

        this.winPoints = 3;
        this.drawPoints = 1;
        this.lossPoints = 0;

        this.availableTactics = new ArrayList<>();
        availableTactics.add("4-4-2");
        availableTactics.add("4-3-3");
        availableTactics.add("3-5-2");
        availableTactics.add("4-2-3-1");
    }

    //  GETTERS
    @Override
    public String getSportName() {
        return sportName;
    }

    @Override
    public int getPlayerCount() {
        return playerCount;
    }

    @Override
    public int getSubstituteCount() {
        return substituteCount;
    }

    @Override
    public int getSubstituteChangeLimit() {
        return substituteChangeLimit;
    }

    @Override
    public int getTeamCount() {
        return teamCount;
    }

    @Override
    public int getPeriodCount() {
        return periodCount;
    }

    @Override
    public List<String> getAvailableTactics() {
        return availableTactics;
    }

    @Override
    public int getMatchupsPerTeam() {
        return matchupsPerTeam;
    }

    @Override
    public int getWinPoints() {
        return winPoints;
    }

    @Override
    public int getDrawPoints() {
        return drawPoints;
    }

    @Override
    public int getLossPoints() {
        return lossPoints;
    }    

    //  SETTERS

    @Override
    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    @Override
    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    @Override
    public void setSubstituteCount(int substituteCount) {
        this.substituteCount = substituteCount;
    }

    @Override
    public void setSubstituteChangeLimit(int substituteChangeLimit) {
        this.substituteChangeLimit = substituteChangeLimit;
    }

    @Override
    public void setTeamCount(int teamCount) {
        this.teamCount = teamCount;
    }

    @Override
    public void setWinPoints(int winPoints) {
        this.winPoints = winPoints;
    }

    @Override
    public void setDrawPoints(int drawPoints) {
        this.drawPoints = drawPoints;
    }

    @Override
    public void setLossPoints(int lossPoints) {
        this.lossPoints = lossPoints;
    }

    @Override
    public void setPeriodCount(int periodCount) {
        this.periodCount = periodCount;
    }

    @Override
    public void setAvailableTactics(List<String> availableTactics) {
        this.availableTactics = availableTactics;
    }

    @Override
    public void setMatchupsPerTeam(int matchupsPerTeam) {
        this.matchupsPerTeam = matchupsPerTeam;
    }

    //  METHODS

    @Override
    public void displayRules() {
        System.out.println("Football Rules:");
        System.out.println("- Sport Name: " + sportName);
        System.out.println("- Players on field: " + playerCount);
        System.out.println("- Substitutes on bench: " + substituteCount);
        System.out.println("- Maximum substitution changes: " + substituteChangeLimit);
        System.out.println("- Teams in a match: " + teamCount);
        System.out.println("- Number of periods: " + periodCount);
        System.out.println("- Matchups per team: " + matchupsPerTeam);
        System.out.println("- Available tactics: " + availableTactics);
        System.out.println("- Point system: Win = " + winPoints
                + ", Draw = " + drawPoints
                + ", Loss = " + lossPoints);
}

    public boolean makeSubstitution(int currentSubsUsed) {
        if (currentSubsUsed < substituteChangeLimit) {
            System.out.println("Substitution successful.");
            return true;
        } else {
            System.out.println("No substitutions remaining.");
            return false;
        }
    }

    @Override
    public void generateLeague() {
        System.out.println("Generating football league...");
        System.out.println("Sport: " + sportName);
        System.out.println("Teams in one match: " + teamCount);
        System.out.println("Matchups per team: " + matchupsPerTeam);
        System.out.println("Point system: " + winPoints + "/" + drawPoints + "/" + lossPoints);
   
    }

    @Override
    public void generateTeams() {System.out.println("Generating football teams...");
        System.out.println("Players on field: " + playerCount);
        System.out.println("Substitutes: " + substituteCount);
        System.out.println("Substitute change limit: " + substituteChangeLimit);
        System.out.println("Available tactics: " + availableTactics);
    }

    public boolean isValidLineupSize(int players) {
        return players == playerCount;
    }

    public boolean canSubstitute(int currentSubs) {
        return currentSubs < substituteChangeLimit;
    }

    
}
