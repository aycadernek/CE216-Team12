package sportsmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AbstractTeamTest {

    // create a dummy team
    private static class DummyTeam extends AbstractTeam {
        public DummyTeam(String name, String coach, String tactic) {
            super(name, coach, tactic);
        }

        @Override
        public void addPlayerToRoster(AbstractPlayer player) {}

        @Override
        public void setMatchDayLineup(java.util.List<AbstractPlayer> starters, java.util.List<AbstractPlayer> bench) {}

        @Override
        public boolean isReadyToPlay() {
            return false;
        }
    }

    // create a dummy player
    private static class DummyPlayer extends AbstractPlayer {
        public DummyPlayer(String name, String position) {
            super(name, position);
        }
    }

    private DummyTeam team;

    @BeforeEach
    public void setUp() {
        team = new DummyTeam("Team1", "Coach1", "Standard");
    }

    @Test
    public void testTeamInitialization() {
        assertEquals("Team1", team.getName());
        assertEquals("Coach1", team.getCoach());
        assertEquals("Standard", team.getTactic());
        assertTrue(team.getAllPlayers().isEmpty());
        assertTrue(team.getActivePlayers().isEmpty());
        assertTrue(team.getSubstitutePlayers().isEmpty());

        // check that stats start with 0
        assertEquals(0, team.getWinCount());
        assertEquals(0, team.getLossCount());
        assertEquals(0, team.getDrawCount());
    }

    @Test
    public void testSubstitutionLogic() {
        DummyPlayer starter = new DummyPlayer("Starter", "Pos");
        DummyPlayer sub = new DummyPlayer("Sub", "Pos");

        // manually place players on pitch and bench
        team.getActivePlayers().add(starter);
        team.getSubstitutePlayers().add(sub);

        // perform the substitution
        team.substitutePlayer(starter, sub);

        // verify they swapped lists
        assertTrue(team.getActivePlayers().contains(sub), "Sub should now be active.");
        assertFalse(team.getActivePlayers().contains(starter), "Starter should no longer be active.");
        assertTrue(team.getSubstitutePlayers().contains(starter), "Starter should now be on the bench.");
    }

    @Test
    public void testInvalidSubstitution() {
        DummyPlayer starter = new DummyPlayer("Starter", "Pos");
        DummyPlayer sub = new DummyPlayer("Sub", "Pos");
        DummyPlayer random = new DummyPlayer("Random", "Pos");

        team.getActivePlayers().add(starter);
        team.getSubstitutePlayers().add(sub);

        // try substituting a player who isn't on the pitch
        assertThrows(IllegalArgumentException.class, () -> team.substitutePlayer(random, sub));

        // try bringing in a player who isn't on the bench
        assertThrows(IllegalArgumentException.class, () -> team.substitutePlayer(starter, random));
    }

    @Test
    public void testChangeTactic() {
        team.changeTactic("Aggressive");
        assertEquals("Aggressive", team.getTactic());

        // test the Abstract validation for empty tactics
        assertThrows(IllegalArgumentException.class, () -> team.changeTactic("   "));
        assertThrows(IllegalArgumentException.class, () -> team.changeTactic(null));
    }
}