package sportsmanager;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTeam {

    private String name;
    private List<AbstractPlayer> allPlayers;
    private List<AbstractPlayer> activePlayers;
    private List<AbstractPlayer> substitutePlayers;
    private String coach;
    private String tactic;
    private int winCount;
    private int lossCount;
    private int drawCount;

    public AbstractTeam(String name, String coach, String tactic) {
        this.name = name;
        this.coach = coach;
        this.tactic = tactic;

        this.allPlayers = new ArrayList<>();
        this.activePlayers = new ArrayList<>();
        this.substitutePlayers = new ArrayList<>();

        this.winCount = 0;
        this.lossCount = 0;
        this.drawCount = 0;
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

    public void changeTactic(String newTactic) {
        if (newTactic == null || newTactic.trim().isEmpty()) {
            throw new IllegalArgumentException("Tactic cannot be empty.");
        }
        this.tactic = newTactic.trim();
    }
}