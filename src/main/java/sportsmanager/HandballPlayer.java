package sportsmanager;


public class HandballPlayer extends AbstractPlayer {
    
    private int goals;
    private int assists;
    private int saves;
    private int suspensions;
    private int yellowCards;
    private int redCards;
    
    public HandballPlayer() {
        super();
    }

    public HandballPlayer(String name, String position) {
        super(name, position);
        this.goals = 0;
        this.assists = 0;
        this.saves = 0;
        this.suspensions = 0;
        this.yellowCards = 0;
        this.redCards = 0;  
    }

    public int getGoals() {
        return goals;
    }

    public int getAssists() {
        return assists;
    }

    public int getSaves() {
        return saves;
    }

    public int getSuspensions() {
        return suspensions;
    }

    public int getYellowCards() {
        return yellowCards;
    }

    public int getRedCards() {
        return redCards;
    }

    public int getTwoMinuteSuspensions() {
    return suspensions;
    }

     public void setGoals(int goals) {
        this.goals = goals;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public void setSaves(int saves) {
        this.saves = saves;
    }

    public void setSuspensions(int suspensions) {
        this.suspensions = suspensions;
    }

    public void setYellowCards(int yellowCards) {
        this.yellowCards = yellowCards;
    }

    public void setRedCards(int redCards) {
        this.redCards = redCards;
    }

    public void scoreGoal() {
        if (isInjured()) {
            throw new IllegalStateException("Injured player cannot score.");
        }
        goals++;
    }

    public void recordAssist() {
        if (isInjured()) {
            throw new IllegalStateException("Injured player cannot record assist.");
        }
        assists++;
    }

    public void recordSave() {
        if (isInjured()) {
            throw new IllegalStateException("Injured player cannot make a save.");
        }
        saves++;
    }

    public void receiveTwoMinuteSuspension() {
        suspensions++;
    }

    public void receiveYellowCard() {
        yellowCards++;
    }

    public void receiveRedCard() {
        redCards++;
    }

     @Override

    public String toString() {

        return String.format(

                "HandballPlayer{name='%s', position='%s', goals=%d, assists=%d, saves=%d, suspensions=%d, yellowCards=%d, redCards=%d, injured=%b}",

                getName(), getPosition(), goals, assists, saves, suspensions, yellowCards, redCards, isInjured()

        );
    }
}