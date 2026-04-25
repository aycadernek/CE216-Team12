package sportsmanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FootballTeam extends AbstractTeam {
    private static final int MAX_ROSTER_SIZE = 18;
    private static final int PLAYERS_ON_FIELD = 11;

    private static final List<String> VALID_TACTICS = Arrays.asList(
            "4-4-2", "4-3-3", "3-5-2", "5-3-2", "4-2-3-1"
    );

    public FootballTeam() {
    }

    public FootballTeam(String name, String coach, String initialTactic) {
        super(name, coach, initialTactic);
        validateTactic(initialTactic);
    }

    private void validateTactic(String tactic) {
        if (tactic == null || !VALID_TACTICS.contains(tactic)) {
            throw new IllegalArgumentException(
                    "Invalid football tactic: " + tactic + ". Allowed tactics: " + VALID_TACTICS
            );
        }
    }

    public void addPlayerToRoster(FootballPlayer player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }
        if (getAllPlayers().size() >= MAX_ROSTER_SIZE) {
            throw new IllegalStateException(
                    "Cannot add player. Football team roster is capped at " + MAX_ROSTER_SIZE + " players."
            );
        }
        getAllPlayers().add(player);
    }

    public void setMatchDayLineup(List<AbstractPlayer> starters, List<AbstractPlayer> bench) {
        if (starters == null || bench == null) {
            throw new IllegalArgumentException("Starters and bench cannot be null.");
        }
        if (starters.size() != PLAYERS_ON_FIELD) {
            throw new IllegalArgumentException(
                    "A football match requires exactly " + PLAYERS_ON_FIELD + " starting players."
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
                "FootballTeam{name='%s', coach='%s', tactic='%s', rosterSize=%d}",
                getName(), getCoach(), getTactic(), getAllPlayers().size()
        );
    }
}