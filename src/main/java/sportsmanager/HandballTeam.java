package sportsmanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HandballTeam extends AbstractTeam {
    private static final int MAX_ROSTER_SIZE = 7;
    private static final int PLAYERS_ON_FIELD = 7;

    private static final List<String> VALID_TACTICS = Arrays.asList("6-0", "5-1", "4-2", "3-2-1", "3-3");

    public HandballTeam() {
    }

    public HandballTeam(String name, String coach, String initialTactic) {
        super(name, coach, initialTactic);
        validateTactic(initialTactic);
    }

    private void validateTactic(String tactic) {
        if (tactic == null || !VALID_TACTICS.contains(tactic)) {
            throw new IllegalArgumentException(
                    "Invalid handball tactic: " + tactic + ". Allowed tactics: " + VALID_TACTICS
            );
        }
    }

    @Override
    public void addPlayerToRoster(AbstractPlayer player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }
        if (!(player instanceof HandballPlayer)) {
            throw new IllegalArgumentException("Only handball players can be added to a handball team.");
        }
        if (getAllPlayers().size() >= MAX_ROSTER_SIZE) {
            throw new IllegalStateException(
                    "Cannot add player. Handball team roster is capped at " + MAX_ROSTER_SIZE + " players."
            );
        }
        getAllPlayers().add(player);
    }

    @Override
    public void setMatchDayLineup(List<AbstractPlayer> starters, List<AbstractPlayer> bench) {
        if (starters == null || bench == null) {
            throw new IllegalArgumentException("Starters and bench cannot be null.");
        }
        if (starters.size() != PLAYERS_ON_FIELD) {
            throw new IllegalArgumentException(
                    "A handball match requires exactly " + PLAYERS_ON_FIELD + " starting players."
            );
        }
        if (starters.size() + bench.size() > MAX_ROSTER_SIZE) {
            throw new IllegalArgumentException(
                    "Match day squad exceeds the maximum allowed roster size of " + MAX_ROSTER_SIZE + "."
            );
        }

        setActivePlayers(new ArrayList<>(starters));
        setSubstitutePlayers(new ArrayList<>(bench));
    }

    @Override
    public boolean isReadyToPlay() {
        return getActivePlayers().size() == PLAYERS_ON_FIELD;
    }

    @Override
    public void changeTactic(String newTactic) {
        validateTactic(newTactic);
        super.changeTactic(newTactic);
    }

    @Override
    public String toString() {
        return String.format(
                "HandballTeam{name='%s', coach='%s', tactic='%s', rosterSize=%d}",
                getName(), getCoach(), getTactic(), getAllPlayers().size()
        );
    }
}