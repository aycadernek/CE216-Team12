package sportsmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FootballPlayerTest {
    private FootballPlayer player;

    @BeforeEach
    public void setUp() {
        player = new FootballPlayer("Lionel Messi", "Forward");
    }

    @Test
    public void testFootballPlayerInitialization() {
        assertEquals(0, player.getGoals(), "A new player should have 0 goals");
        assertEquals(0, player.getAssists(), "A new player should have 0 assists");
        assertEquals(0, player.getYellowCards(), "A new player should have 0 yellow cards");
        assertEquals(0, player.getRedCards(), "A new player should have 0 red cards");
    }

    @Test
    public void testStatIncrements() {
        player.scoreGoal();
        player.recordAssist();
        player.receiveYellowCard();
        player.receiveRedCard();

        assertEquals(1, player.getGoals());
        assertEquals(1, player.getAssists());
        assertEquals(1, player.getYellowCards());
        assertEquals(1, player.getRedCards());
    }

    @Test
    public void testInjuryLogic() {
        player.makeInjury(3);

        // test that injured player cannot score or assist
        assertThrows(IllegalStateException.class, () -> player.scoreGoal(), "Injured player cannot score.");
        assertThrows(IllegalStateException.class, () -> player.recordAssist(), "Injured player cannot assist.");

        player.setInjured(false);
        assertDoesNotThrow(() -> player.scoreGoal(), "Healed player should be able to score.");
    }

    @Test
    public void testNegativeStatsValidation() {
        assertThrows(IllegalArgumentException.class, () -> player.setGoals(-1), "Cannot set negative goals.");
        assertThrows(IllegalArgumentException.class, () -> player.setAssists(-1), "Cannot set negative assists.");
        assertThrows(IllegalArgumentException.class, () -> player.setYellowCards(-1), "Cannot set negative yellow cards.");
        assertThrows(IllegalArgumentException.class, () -> player.setRedCards(-1), "Cannot set negative red cards.");
    }
}
