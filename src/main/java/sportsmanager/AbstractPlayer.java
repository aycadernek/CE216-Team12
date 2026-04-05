package sportsmanager;

public abstract class AbstractPlayer {

    private String name;
    private String position;
    private boolean isInjured;
    private int injuryRemainingDuration;

    public AbstractPlayer(String name, String position) {
        setName(name);
        setPosition(position);
        this.isInjured = false;
        this.injuryRemainingDuration = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be empty.");
        }
        this.name = name.trim();
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        if (position == null || position.trim().isEmpty()) {
            throw new IllegalArgumentException("Player position cannot be empty.");
        }
        this.position = position.trim();
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
        if (duration < 0) {
            throw new IllegalArgumentException("Injury duration cannot be negative.");
        }
        this.injuryRemainingDuration = duration;
        this.isInjured = duration > 0;
    }

    public void makeInjury(int duration) {
        if (duration <= 0) {
            throw new IllegalArgumentException("Injury duration must be greater than 0");
        }
        this.isInjured = true;
        this.injuryRemainingDuration = duration;
    }
}