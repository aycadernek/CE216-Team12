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
    public void testTeamInitialization() {
        assertEquals("Galatasaray", team.getName());
        assertEquals("Okan Buruk", team.getCoach());
        assertEquals("4-2-3-1", team.getTactic());
        assertTrue(team.getAllPlayers().isEmpty(), "New team roster should be empty.");
    }

    @Test
    public void testInvalidTactic() {
        // try creating a team with a fake tactic
        assertThrows(IllegalArgumentException.class, () -> new FootballTeam("Team", "Coach", "9-0-1"));

        // try changing to an empty tactic
        assertThrows(IllegalArgumentException.class, () -> team.changeTactic("   "));
    }

    @Test
    public void testAddPlayerToRosterLimit() {
        // Add exactly 18 players
        for (int i = 0; i < 18; i++) {
            team.addPlayerToRoster(new FootballPlayer("Player " + i, "Midfielder"));
        }
        assertEquals(18, team.getAllPlayers().size());

        // The 19th player should throw your IllegalStateException
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

    @Test
    public void testSubstitutionLogic() {
        FootballPlayer starter = new FootballPlayer("Starter", "Midfielder");
        FootballPlayer sub = new FootballPlayer("Sub", "Midfielder");

        // manually place players on pitch and bench
        team.getActivePlayers().add(starter);
        team.getSubstitutePlayers().add(sub);

        // perform the substitution
        team.substitutePlayer(starter, sub);

        // verify they swapped lists
        assertTrue(team.getActivePlayers().contains(sub), "Sub should now be active.");
        assertFalse(team.getActivePlayers().contains(starter), "Starter should no longer be active.");
        assertTrue(team.getSubstitutePlayers().contains(starter), "Starter should now be on the bench.");

        // trying to take out a player who isn't playing
        FootballPlayer randomPlayer = new FootballPlayer("Random", "Forward");
        assertThrows(IllegalArgumentException.class, () -> team.substitutePlayer(randomPlayer, sub));
    }
}