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
    public void testPlayerInitialization() {
        assertEquals("Lionel Messi", player.getName(), "Name should match the constructor input.");
        assertEquals("Forward", player.getPosition(), "Position should match the constructor input.");
        assertFalse(player.isInjured(), "A new player should not be injured.");
        assertEquals(0, player.getGoals(), "A new player should have 0 goals");
        assertEquals(0, player.getInjuryRemainingDuration(), "A new player should have 0 injury duration");
    }

    @Test
    public void testInvalidNameOrPosition() {
        assertThrows(IllegalArgumentException.class, () -> new FootballPlayer("", "Forward"), "Empty name should throw exception.");
        assertThrows(IllegalArgumentException.class, () -> new FootballPlayer("Messi", null), "Null position should throw exception.");
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
        assertTrue(player.isInjured(), "Player should be marked as injured.");
        assertEquals(3, player.getInjuryRemainingDuration());

        // test that injured player cannot score or assist
        assertThrows(IllegalStateException.class, () -> player.scoreGoal(), "Injured player cannot score.");
        assertThrows(IllegalStateException.class, () -> player.recordAssist(), "Injured player cannot assist.");

        // test healing
        player.setInjured(false);
        assertFalse(player.isInjured(), "Player should be healed.");
        assertEquals(0, player.getInjuryRemainingDuration(), "Duration should reset to 0 when healed.");

        // healed player should be able to score again without an error
        assertDoesNotThrow(() -> player.scoreGoal(), "Healed player should be able to score.");
    }

    @Test
    public void testNegativeStatsValidation() {
        assertThrows(IllegalArgumentException.class, () -> player.setGoals(-1), "Cannot set negative goals.");
        assertThrows(IllegalArgumentException.class, () -> player.setInjuryRemainingDuration(-5), "Cannot set negative injury duration.");
    }
}
