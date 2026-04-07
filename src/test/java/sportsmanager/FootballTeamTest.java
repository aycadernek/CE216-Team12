package sportsmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

public class FootballTeamTest {

    private FootballTeam team;

    @BeforeEach
    public void setUp() {
        team = new FootballTeam("Galatasaray", "Okan Buruk", "4-2-3-1");
    }

    @Test
    public void testInvalidTactic() {
        // try creating a team with a fake tactic
        assertThrows(IllegalArgumentException.class, () -> new FootballTeam("Team", "Coach", "9-0-1"));

        // try changing to a fake football tactic
        assertThrows(IllegalArgumentException.class, () -> team.changeTactic("9-0-1"));
    }

    @Test
    public void testAddPlayerToRosterLimit() {
        // Add exactly 18 players
        for (int i = 0; i < 18; i++) {
            team.addPlayerToRoster(new FootballPlayer("Player " + i, "Midfielder"));
        }
        assertEquals(18, team.getAllPlayers().size());

        // 19th player should throw IllegalStateException
        assertThrows(IllegalStateException.class, () -> team.addPlayerToRoster(new FootballPlayer("Extra", "Forward")));
    }

    @Test
    public void testSetMatchDayLineup() {
        List<AbstractPlayer> starters = new ArrayList<>();
        List<AbstractPlayer> bench = new ArrayList<>();

        // create 11 starter and 5 bench players
        for (int i = 0; i < 11; i++) { starters.add(new FootballPlayer("Starter " + i, "Position")); }
        for (int i = 0; i < 5; i++) { bench.add(new FootballPlayer("Sub " + i, "Position")); }

        team.setMatchDayLineup(starters, bench);
        assertEquals(11, team.getActivePlayers().size());
        assertEquals(5, team.getSubstitutePlayers().size());
        assertTrue(team.isReadyToPlay(), "Team should be ready with exactly 11 active players.");

        // try to set lineup with only 10 starters
        starters.remove(0);
        assertThrows(IllegalArgumentException.class, () -> team.setMatchDayLineup(starters, bench), "Lineup must have exactly 11 starters.");
    }
}