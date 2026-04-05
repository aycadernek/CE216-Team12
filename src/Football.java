public class Football {
    // NOTE (Future Enhancement):
    // Substitution rules can be expanded to include advanced scenarios
    // such as substitution windows, halftime exceptions, and extra-time rules.
    // This will be discussed with the team and implemented later if required.


    private int maxPlayersPerTeam;
    private int playersOnField;
    private int maxSubstitutions;
    private int matchDuration;

    private int winPoints;
    private int drawPoints;
    private int lossPoints;

    public Football() {
        this.maxPlayersPerTeam = 18;
        this.playersOnField = 11;
        this.maxSubstitutions = 5;
        this.matchDuration = 90;

        this.winPoints = 3;
        this.drawPoints = 1;
        this.lossPoints = 0;
    }

    //  GETTERS
    public int getMaxPlayersPerTeam(){
        return maxPlayersPerTeam;
    };
    public int getPlayersOnField(){
        return playersOnField;
    };
    public int getMaxSubstitutions(){
        return maxSubstitutions;
    };
    public int getMatchDuration(){
        return matchDuration;
    };

    //  POINT SYSTEM GETTERS
    public int getWinPoint() {
        return winPoints;
    }

    public int getDrawPoint() {
        return drawPoints;
    }

    public int getLossPoint() {
        return lossPoints;
    }

    //  METHODS
    public void generateLeague() {
        // TODO: Implement after AbstractLeague and FootballLeague structures are finalized with the team.
    }

    public void generateTeams() {
        // TODO: Implement after AbstractTeam and FootballTeam structures are finalized with the team.
    }

    public boolean isValidLineupSize(int players) {
        return players == playersOnField;
    }

    public boolean canSubstitute(int currentSubs) {
        return currentSubs < maxSubstitutions;
    }
}
