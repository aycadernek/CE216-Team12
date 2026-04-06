package sportsmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AbstractMatchTest {

    private static class DummyMatch extends AbstractMatch {
        public DummyMatch(AbstractTeam team1, AbstractTeam team2, int totalPeriods) {
            super(team1, team2, totalPeriods);
        }
        @Override public void playPeriod() {}
        @Override public void createEvent() {}
    }

    private DummyMatch match;
    private FootballTeam team1;
    private FootballTeam team2;

    @BeforeEach
    public void setUp() {
        team1 = new FootballTeam("Home Team", "Coach Home", "4-4-2");
        team2 = new FootballTeam("Away Team", "Coach Away", "4-3-3");
        match = new DummyMatch(team1, team2, 4);
    }

    @Test
    public void testMatchInitialization() {
        // Test if variables are properly initialized when the league is created
        assertEquals(team1, match.getTeam1());
        assertEquals(team2, match.getTeam2());
        assertEquals(0, match.getTeam1Score());
        assertEquals(0, match.getTeam2Score());
        assertEquals(0, match.getCurrentPeriod());
        assertEquals(4, match.getTotalPeriods());
        assertFalse(match.isFinished());
        assertNotNull(match.getEvents());

        // Test that the same team cannot play a match against itself
        assertThrows(IllegalArgumentException.class, () -> {
            new DummyMatch(team1, team1, 4); 
        }, "A team cannot play a match against itself.");
    }

    @Test
    public void testSetters() {
        // Setter tests
        match.setTeam1Score(3);
        match.setTeam2Score(1);
        assertEquals(3, match.getTeam1Score(), "Team 1 score should match the setted value.");
        assertEquals(1, match.getTeam2Score(), "Team 2 score should match the setted value.");

        match.setCurrentPeriod(4);
        match.setFinished(true);
        assertEquals(4, match.getCurrentPeriod());
        assertTrue(match.isFinished(), "Match should be marked as finished.");

        match.setResult("Home Team Won");
        assertEquals("Home Team Won", match.getResult(), "Result string should be correctly saved.");

        // Test that a null team should not be allowed to play a match
        assertThrows(IllegalArgumentException.class, () -> {
            match.setTeam1(null);
        }, "Home team cannot be NULL.");
        
        assertThrows(IllegalArgumentException.class, () -> {
            match.setTeam2(null);
        }, "Away team cannot be NULL.");
    }
}