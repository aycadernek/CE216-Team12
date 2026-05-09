package sportsmanager;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonSubTypes({ 
    @JsonSubTypes.Type(value = FootballTeam.class, name = "FootballTeam"),
    @JsonSubTypes.Type(value = HandballTeam.class, name = "HandballTeam")
})
public abstract class AbstractTeam {

    private String name;
    private List<AbstractPlayer> allPlayers;
    private List<AbstractPlayer> activePlayers;
    private List<AbstractPlayer> substitutePlayers;
    private String coach;
    private String coachPhotoPath;
    private String tactic;
    private String teamLogoPath;
    private int winCount;
    private int lossCount;
    private int drawCount;

    protected AbstractTeam() {
    }

    public AbstractTeam(String name, String coach, String tactic) {
        this.name = name;
        this.coach = coach;
        this.coachPhotoPath = "";
        this.tactic = tactic;

        this.allPlayers = new ArrayList<>();
        this.activePlayers = new ArrayList<>();
        this.substitutePlayers = new ArrayList<>();

        this.winCount = 0;
        this.lossCount = 0;
        this.drawCount = 0;
    }
    public String getTeamLogoPath() {
        return teamLogoPath;
    }

    public void setTeamLogoPath(String teamLogoPath) {
        this.teamLogoPath = teamLogoPath;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<AbstractPlayer> getAllPlayers() { return allPlayers; }
    public void setAllPlayers(List<AbstractPlayer> allPlayers) { this.allPlayers = allPlayers; }

    public List<AbstractPlayer> getActivePlayers() { return activePlayers; }
    public void setActivePlayers(List<AbstractPlayer> activePlayers) { this.activePlayers = activePlayers; }

    public List<AbstractPlayer> getSubstitutePlayers() { return substitutePlayers; }
    public void setSubstitutePlayers(List<AbstractPlayer> substitutePlayers) { this.substitutePlayers = substitutePlayers; }

    public String getCoach() { return coach; }
    public void setCoach(String coach) { this.coach = coach; }

    public String getCoachPhotoPath() { return coachPhotoPath; }
    public void setCoachPhotoPath(String coachPhotoPath) { this.coachPhotoPath = coachPhotoPath; }

    public String getTactic() { return tactic; }
    public void setTactic(String tactic) { this.tactic = tactic; }

    public int getWinCount() { return winCount; }
    public void setWinCount(int winCount) { this.winCount = winCount; }

    public int getLossCount() { return lossCount; }
    public void setLossCount(int lossCount) { this.lossCount = lossCount; }

    public int getDrawCount() { return drawCount; }
    public void setDrawCount(int drawCount) { this.drawCount = drawCount; }


    public void substitutePlayer(AbstractPlayer outPlayer, AbstractPlayer inPlayer) {
        if (!activePlayers.contains(outPlayer)) {
            throw new IllegalArgumentException("Substitution failed: " + outPlayer.getName() + " is not currently on the pitch.");
        }
        if (!substitutePlayers.contains(inPlayer)) {
            throw new IllegalArgumentException("Substitution failed: " + inPlayer.getName() + " is not on the substitute bench.");
        }

        activePlayers.remove(outPlayer);
        substitutePlayers.remove(inPlayer);

        activePlayers.add(inPlayer);
        substitutePlayers.add(outPlayer);
    }

    public void fillStarters(int requiredStarters) {
        Iterator<AbstractPlayer> activeIter = activePlayers.iterator();
        while (activeIter.hasNext()) {
            AbstractPlayer p = activeIter.next();
            if (p.isInjured()) {
                substitutePlayers.add(p);
                activeIter.remove(); 
            }
        }

        Iterator<AbstractPlayer> subIter = substitutePlayers.iterator();
        while (subIter.hasNext() && activePlayers.size() < requiredStarters) {
            AbstractPlayer p = subIter.next();
            if (!p.isInjured()) {
                activePlayers.add(p);
                subIter.remove(); 
            }
        }

        if (activePlayers.size() < requiredStarters) {
            for (AbstractPlayer p : allPlayers) {
                if (activePlayers.size() >= requiredStarters) {
                    break;
                }
                if (!p.isInjured() && !activePlayers.contains(p) && !substitutePlayers.contains(p)) {
                    activePlayers.add(p);
                }
            }
        }
        
        for (AbstractPlayer p : allPlayers) {
            if (!activePlayers.contains(p) && !substitutePlayers.contains(p)) {
                substitutePlayers.add(p);
            }
        }
    }

    public boolean isReadyToPlay() {
        int requiredStarters = getRequiredStarters();
        if (getActivePlayers().size() == requiredStarters) {
            return true;
        }
        for (AbstractPlayer sub : getSubstitutePlayers()) {
            if (!sub.isInjured()) {
                return false;
            }
        }
        return !getActivePlayers().isEmpty();
    }

    public void changeTactic(String newTactic) {
        if (newTactic == null || newTactic.trim().isEmpty()) {
            throw new IllegalArgumentException("Tactic cannot be empty.");
        }
        this.tactic = newTactic.trim();
    }
    
    public void resetStats() {
        this.winCount = 0;
        this.lossCount = 0;
        this.drawCount = 0;
    }
    
    public abstract void addPlayerToRoster(AbstractPlayer player);
    public abstract void setMatchDayLineup(List<AbstractPlayer> starters, List<AbstractPlayer> bench);
    public abstract int getRequiredStarters();
}
