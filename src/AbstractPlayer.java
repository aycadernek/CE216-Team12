public abstract class AbstractPlayer {

    private String name;
    private String position;
    private boolean isInjured;
    private int injuryRemainingDuration;

    public AbstractPlayer(String name, String position) {
        this.name = name;
        this.position = position;
        this.isInjured = false;
        this.injuryRemainingDuration = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isInjured() {
        return isInjured;
    }

    public void setInjured(boolean injured) {
        this.isInjured = injured;

        if (!injured) {
            this.injuryRemainingDuration = 0;
        }
    }

    public int getInjuryRemainingDuration() {
        return injuryRemainingDuration;
    }

    public void setInjuryRemainingDuration(int duration) {
        this.injuryRemainingDuration = duration;

        if (duration > 0) {
            this.isInjured = true;
        } else {
            this.isInjured = false;
        }
    }

    public void makeInjury(int duration) {
        this.isInjured = true;
        this.injuryRemainingDuration = duration;
    }
}