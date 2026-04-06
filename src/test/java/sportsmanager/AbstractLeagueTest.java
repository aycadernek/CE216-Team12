package sportsmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class AbstractLeagueTest {

    private static class DummyLeague extends AbstractLeague {
        public DummyLeague(String leagueName, ISport sport) {
            super(leagueName, sport);
        }
        @Override public void createLeague() {}
        @Override public void playWeeklyMatch() {}
        @Override public void calculateLeagueResult() {}
    }

    private DummyLeague league;
    private ISport mockSport;

    @BeforeEach
    public void setUp() {
        mockSport = new Football();
        league = new DummyLeague("Test League", mockSport);
    }

    @Test
    public void testLeagueInitialization() {
        // Test if variables are properly initialized when the league is created
        assertNotNull(league.getTeams(), "Teams list should be initialized empty.");
        assertTrue(league.getTeams().isEmpty());
        assertNotNull(league.getWeeklyFixtures(), "Fixtures map should be initialized.");
        assertTrue(league.getWeeklyFixtures().isEmpty());
        assertNotNull(league.getCompletedMatches(), "Completed matches list should be initialized.");
        assertEquals(1, league.getCurrentWeek(), "At start week should be 1.");
    }

    @Test
    public void testAddTeam() {
        // Test if two existing different teams can be added
        FootballTeam team1 = new FootballTeam("Team A", "Coach", "4-4-2");
        FootballTeam team2 = new FootballTeam("Team B", "Coach", "3-5-2");
        league.addTeam(team1);
        league.addTeam(team2);
        assertEquals(2, league.getTeams().size());
        assertTrue(league.getTeams().contains(team1));

        // Check whether the same team can be added to the league again
        league = new DummyLeague("Test League", mockSport);
        FootballTeam dupTeam1 = new FootballTeam("Team A", "Coach", "4-4-2");
        league.addTeam(dupTeam1);
        league.addTeam(dupTeam1); 
        assertEquals(1, league.getTeams().size(), "Duplicate teams should not be added to the league.");

        // Test if a null team can be added
        league = new DummyLeague("Test League", mockSport);
        league.addTeam(null);
        assertTrue(league.getTeams().isEmpty(), "Null teams should not be added to the league.");
    }

    @Test
    public void testWeekManagement() {
        // Setter test 
        league.setTotalWeeks(38);
        assertEquals(38, league.getTotalWeeks(), "Total week setter test failed.");

        // Test if the week advances properly
        league = new DummyLeague("Test League", mockSport);
        league.setTotalWeeks(5); 
        league.advanceWeek();
        assertEquals(2, league.getCurrentWeek(), "Advance week should increment current week by 1.");

        // Test if the week exceeds the total weeks
        league = new DummyLeague("Test League", mockSport);
        league.setTotalWeeks(3);
        league.advanceWeek();
        league.advanceWeek();
        league.advanceWeek(); 
        assertTrue(league.getCurrentWeek() <= league.getTotalWeeks() + 1, "Current week should not exceed total weeks + 1.");

        // Test if it doesn't advance when total weeks is zero
        league = new DummyLeague("Test League", mockSport);
        league.setTotalWeeks(0);
        league.advanceWeek();
        assertEquals(1, league.getCurrentWeek(), "Current week should remain as initialized if total weeks is 0.");
    }

    @Test
    public void testMatchRetrieval() {
        // Test if it tries to return matches of non-existent weeks
        List<AbstractMatch> futureMatches = league.getMatchesForWeek(99);
        assertNotNull(futureMatches, "Should return an empty list for non existent weeks.");
        assertTrue(futureMatches.isEmpty(), "The returned list should be empty.");

        List<AbstractMatch> negativeWeekMatches = league.getMatchesForWeek(-1);
        assertNotNull(negativeWeekMatches, "Should handle negative weeks.");
        assertTrue(negativeWeekMatches.isEmpty(), "The returned list should be empty.");

        // Test if it throws an error when there are no matches in the league
        List<AbstractMatch> allMatches = league.getAllMatches();
        assertNotNull(allMatches, "getAllMatches should not return null even if no matches exist.");
        assertTrue(allMatches.isEmpty(), "getAllMatches should be empty initially.");
    }
}