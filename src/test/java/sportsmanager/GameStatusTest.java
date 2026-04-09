package sportsmanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class GameStatusTest {


    private static class DummyLeague extends AbstractLeague {
        public DummyLeague(String leagueName, ISport sportType) {
            super(leagueName, sportType);
        }

        @Override
        public void createLeague() {
            // not needed for this test
        }

        @Override
        public void playWeeklyMatch() {
            // not needed for this test
        }

        @Override
        public void calculateLeagueResult() {
            // not needed for this test
        }
    }

    @Test
    public void testDefaultConstructor() {
        GameStatus status = new GameStatus();

        assertEquals("", status.getUsername());
        assertNull(status.getCurrentSport());
        assertNull(status.getCurrentLeague());
        assertEquals(GameStatus.NOT_STARTED, status.getStatus());
        assertEquals(0, status.getCurrentWeek());
    }

    @Test
    public void testSettersAndGetters() {
        GameStatus status = new GameStatus();
        ISport football = new Football();
        DummyLeague league = new DummyLeague("Test League", football);

        status.setUsername("Cem");
        status.setCurrentSport(football);
        status.setCurrentLeague(league);

        assertEquals("Cem", status.getUsername());
        assertEquals(1, status.getCurrentWeek());
        assertEquals(football, status.getCurrentSport());
    }

    @Test
    public void testStartNewGame() {
        GameStatus status = new GameStatus();
        ISport football = new Football();
        DummyLeague league = new DummyLeague("Test League", football);

        status.startNewGame("Cem", football, league);

        assertEquals("Cem", status.getUsername());
        assertEquals(1, status.getCurrentWeek());
        assertEquals(football, status.getCurrentSport());
    }

        @Test
    public void testFinishGame() {
        GameStatus status = new GameStatus();
        ISport football = new Football();
        DummyLeague league = new DummyLeague("Test League", football);

        status.startNewGame("Cem", football, league);
        status.finishGame();

        assertEquals(GameStatus.FINISHED, status.getStatus());
    }

    @Test
    public void testIsLeagueOver() {
        ISport football = new Football();
        DummyLeague league = new DummyLeague("Test League", football);
        GameStatus status = new GameStatus();

        status.setCurrentLeague(league);
        league.setTotalWeeks(10);

        assertFalse(status.isLeagueOver());

                for (int i = 0; i < 10; i++) {
            league.advanceWeek();
        }

        assertTrue(status.isLeagueOver());
    }
}