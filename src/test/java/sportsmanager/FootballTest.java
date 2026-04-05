package sportsmanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class FootballTest {

    @Test
    public void testDefaultSportName() {
        Football football = new Football();
        assertEquals("Football", football.getSportName());
    }

    @Test
    public void testDisplayRulesDoesNotCrash() {
        Football football = new Football();
        football.displayRules(); // just ensures no exception
}

    @Test
    public void testDefaultPlayerCount() {
        Football football = new Football();
        assertEquals(11, football.getPlayerCount());
    }

    @Test
    public void testDefaultSubstituteCount() {
        Football football = new Football();
        assertEquals(7, football.getSubstituteCount());
    }

    @Test
    public void testDefaultSubstituteChangeLimit() {
        Football football = new Football();
        assertEquals(5, football.getSubstituteChangeLimit());
    }

    @Test
    public void testDefaultPoints() {
        Football football = new Football();
        assertEquals(3, football.getWinPoints());
        assertEquals(1, football.getDrawPoints());
        assertEquals(0, football.getLossPoints());
    }

    @Test
    public void testValidLineupSize() {
        Football football = new Football();
        assertTrue(football.isValidLineupSize(11));
        assertFalse(football.isValidLineupSize(10));
    }

    @Test
    public void testCanSubstitute() {
        Football football = new Football();
        assertTrue(football.canSubstitute(2));
        assertFalse(football.canSubstitute(5));
    }
}