package sportsmanager;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameStatusTest {

    @Test
    public void testDefaultConstructor() {
        GameStatus status = new GameStatus();
        assertEquals("", status.getUsername());
        assertNull(status.getCurrentSport());
        assertEquals(0, status.getCurrentWeek());
    }

    @Test
    public void testSettersAndGetters() {
        GameStatus status = new GameStatus();
        ISport football = new Football();

        status.setUsername("Cem");
        status.setCurrentWeek(3);
        status.setCurrentSport(football);

        assertEquals("Cem", status.getUsername());
        assertEquals(3, status.getCurrentWeek());
        assertEquals(football, status.getCurrentSport());
    }

    @Test
    public void testStartNewGame() {
        GameStatus status = new GameStatus();
        ISport football = new Football();

        status.startNewGame("Cem", football);

        assertEquals("Cem", status.getUsername());
        assertEquals(1, status.getCurrentWeek());
        assertEquals(football, status.getCurrentSport());
    }

    @Test
    public void testIsLeagueOver() {
        GameStatus status = new GameStatus();

        status.setCurrentWeek(11);
        assertTrue(status.isLeagueOver());

        status.setCurrentWeek(5);
        assertFalse(status.isLeagueOver());
    }
}