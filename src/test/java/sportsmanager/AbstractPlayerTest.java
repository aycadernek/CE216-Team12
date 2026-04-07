package sportsmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AbstractPlayerTest {

    // create a dummy player
    private static class DummyPlayer extends AbstractPlayer {
        public DummyPlayer(String name, String position) {
            super(name, position);
        }
    }

    private DummyPlayer player;

    @BeforeEach
    public void setUp() {
        player = new DummyPlayer("Player1", "Defender");
    }

    @Test
    public void testPlayerInitialization() {
        assertEquals("Player1", player.getName());
        assertEquals("Defender", player.getPosition());
        assertFalse(player.isInjured());
        assertEquals(0, player.getInjuryRemainingDuration());
    }

    @Test
    public void testInvalidNameOrPosition() {
        assertThrows(IllegalArgumentException.class, () -> new DummyPlayer("", "Defender"));
        assertThrows(IllegalArgumentException.class, () -> new DummyPlayer("Player", null));
    }

    @Test
    public void testInjuryLogic() {
        player.makeInjury(3);
        assertTrue(player.isInjured(), "Player should be marked as injured.");
        assertEquals(3, player.getInjuryRemainingDuration());

        player.setInjured(false);
        assertFalse(player.isInjured(), "Player should be healed.");
        assertEquals(0, player.getInjuryRemainingDuration(), "Duration should reset to 0 when healed.");
    }
}