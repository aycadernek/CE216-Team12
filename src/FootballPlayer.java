public class FootballPlayer extends AbstractPlayer {

    private int goals;
    private int assists;
    private int yellowCards;
    private int redCards;

    public FootballPlayer(String name, String position) {
        super(name, position);
        this.goals = 0;
        this.assists = 0;
        this.yellowCards = 0;
        this.redCards = 0;
    }

    private void validateNonNegative(int value, String statName) {
        if (value < 0) {
            throw new IllegalArgumentException(statName + " cannot be negative!");
        }
    }

    public int getGoals() {return goals; }

    public void setGoals(int goals) {
        validateNonNegative(goals, "Goals");
        this.goals = goals;
    }

    public int getAssists() {return assists; }

    public void setAssists(int assists) {
        validateNonNegative(assists, "Assists");
        this.assists = assists;
    }

    public int getYellowCards() {return yellowCards; }

    public void setYellowCards(int yellowCards) {
        validateNonNegative(yellowCards, "Yellow cards");
        this.yellowCards = yellowCards;
    }

    public int getRedCards() {return redCards; }

    public void setRedCards(int redCards) {
        validateNonNegative(redCards, "Red cards");
        this.redCards = redCards;
    }

    public void scoreGoal() {
        if (isInjured()) {
            throw new IllegalStateException("Injured player cannot score.");
        }
        this.goals++;
    }

    public void recordAssist() {
        if (isInjured()) {
            throw new IllegalStateException("Injured player cannot record assist.");
        }
        this.assists++;
    }

    public void receiveYellowCard() { this.yellowCards++; }
    public void receiveRedCard() { this.redCards++; }

    // for debugging
    @Override
    public String toString() {
        return String.format("FootballPlayer{name='%s', position='%s', goals=%d, assists=%d, injured=%b}",
                getName(), getPosition(), goals, assists, isInjured());
    }
}