package sportsmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class FootballLeagueTest {

    private FootballLeague league;
    private FootballTeam team1;
    private FootballTeam team2;
    private FootballTeam team3;
    private FootballTeam team4;

    @BeforeEach
    public void setUp() {
        league = new FootballLeague("Premier League");
        
        team1 = new FootballTeam("TeamA", "Coach1", "4-3-3");
        team2 = new FootballTeam("TeamB", "Coach2", "4-2-3-1");
        team3 = new FootballTeam("Team3", "Coach3", "4-3-3");
        team4 = new FootballTeam("Team4", "Coach4", "4-3-3");
    }

    @Test
    public void testCreateLeagueAllCases() {
        // Test creating a league with an even number of teams
        league.addTeam(team1);
        league.addTeam(team2);
        league.addTeam(team3);
        league.addTeam(team4);
        league.createLeague();
        assertEquals(6, league.getTotalWeeks());
        assertFalse(league.getWeeklyFixtures().isEmpty());
        assertEquals(2, league.getWeeklyFixtures().get(1).size(), "There should be 2 matches in week 1.");

        // Test creating a league with an odd number of teams
        setUp(); 
        league.addTeam(team1);
        league.addTeam(team2);
        league.addTeam(team3);
        league.createLeague();
        assertEquals(6, league.getTotalWeeks());
        assertTrue(league.getWeeklyFixtures().get(1).size() >= 0, "Fixtures should handle odd number of teams without crashing.");
        assertEquals(1, league.getMatchesForWeek(1).size(), "In league with 3 teams there should be 1 match per weak.");

        // Test attempting to create a league with 1 team
        setUp();
        league.addTeam(team1);
        league.createLeague();
        assertEquals(0, league.getTotalWeeks(), "League should not be created if teams are less than 2.");
        assertTrue(league.getWeeklyFixtures().isEmpty());

        // Test attempting to create a league when there are no teams
        setUp();
        league.createLeague();
        assertEquals(0, league.getTotalWeeks(), "The league cannot be created when there are no teams and total weeks should be 0.");
    }

    @Test
    public void testCalculateLeagueResult_SortByPoints() {
        // Sorting by points test
        league.addTeam(team1);
        league.addTeam(team2);
        league.addTeam(team3);
        
        team1.setWinCount(2); 
        team2.setWinCount(1); 
        team2.setLossCount(1);
        team3.setLossCount(2);
        
        league.calculateLeagueResult();
        
        assertEquals(team1, league.getTeams().get(0), "Team 1 should be 1st due to highest points.");
        assertEquals(team2, league.getTeams().get(1), "Team 2 should be 2nd.");
        assertEquals(team3, league.getTeams().get(2), "Team 3 should be 3rd.");
    }

    @Test
    public void testCalculateLeagueResult_TieBreakerGoalDifference() {
        // Test sorting by match scores against other teams when points are equal and there is no other match between teams
        league.addTeam(team1);
        league.addTeam(team2);
        
        team1.setDrawCount(1);
        team2.setDrawCount(1);
        
        FootballMatch fakeMatchForAveraj = new FootballMatch(team1, team3); 
        fakeMatchForAveraj.setTeam1Score(4); 
        fakeMatchForAveraj.setTeam2Score(0);
        
        FootballMatch fakeMatchForAveraj2 = new FootballMatch(team2, team4);
        fakeMatchForAveraj2.setTeam1Score(1); 
        fakeMatchForAveraj2.setTeam2Score(0);
        
        league.addCompletedMatch(fakeMatchForAveraj);
        league.addCompletedMatch(fakeMatchForAveraj2);
        
        league.calculateLeagueResult();
        
        assertEquals(team1, league.getTeams().get(0), "Team 1 should win the tie-breaker based on Goal Difference.");
    }

    @Test
    public void testCalculateLeagueResult_AlphabeticalTieBreaker() {
        // Test that it does alphabetical sorting if everything is equal
        league.addTeam(team1);
        league.addTeam(team2);
        
        team1.setWinCount(1); 
        team1.setLossCount(1);
        team2.setWinCount(1); 
        team2.setLossCount(1);
        
        FootballMatch fakeMatch1 = new FootballMatch(team1, team3); 
        fakeMatch1.setTeam1Score(1); 
        fakeMatch1.setTeam2Score(1); 
        
        FootballMatch fakeMatch2 = new FootballMatch(team2, team4);
        fakeMatch2.setTeam1Score(2); 
        fakeMatch2.setTeam2Score(2); 
        
        assertEquals(team1, league.getTeams().get(0), "If everything is equal, TeamA should come first.");
        assertEquals(team2, league.getTeams().get(1), "If everything is equal, TeamB should come second.");
    }
    @Test
    public void testCalculateLeagueResult_TieBreakerHeadToHead() {
        // Test that it should check the match between teams first if points are equal
        league.addTeam(team1);
        league.addTeam(team2);
        
        team1.setWinCount(1);
        team2.setWinCount(1);
        
        FootballMatch headToHeadMatch = new FootballMatch(team1, team2);
        headToHeadMatch.setTeam1Score(2);
        headToHeadMatch.setTeam2Score(1);
        headToHeadMatch.setFinished(true); 
        
        league.addCompletedMatch(headToHeadMatch);
        league.calculateLeagueResult(); 
        
        assertEquals(team1, league.getTeams().get(0), "Team 1 should be 1st due to Head-to-Head win.");
        assertEquals(team2, league.getTeams().get(1), "Team 2 should be 2nd.");
    }

    @Test
    public void testCalculateLeagueResult_ComplexTieBreakers() {
        // LEAGUE RESULT TEST WITH ALL POSSIBILITIES
        FootballTeam tA = new FootballTeam("A", "C", "4-4-2"); 
        FootballTeam tB = new FootballTeam("Z", "C", "4-4-2"); 
        FootballTeam tC = new FootballTeam("C", "C", "4-4-2");
        FootballTeam tD = new FootballTeam("D", "C", "4-4-2");
        league.addTeam(tA); 
        league.addTeam(tB);
        league.addTeam(tC); 
        league.addTeam(tD);
        
        tA.setWinCount(3); 
        tB.setWinCount(2); 
        tC.setWinCount(1); 
        tD.setWinCount(1); 
        
        FootballMatch headToHeadMatch = new FootballMatch(tC, tD);
        headToHeadMatch.setTeam1Score(2); 
        headToHeadMatch.setTeam2Score(0);
        headToHeadMatch.setFinished(true);
        league.addCompletedMatch(headToHeadMatch);
        
        FootballTeam tE = new FootballTeam("E", "C", "4-4-2");
        FootballTeam tF = new FootballTeam("F", "C", "4-4-2");
        league.addTeam(tE); 
        league.addTeam(tF);
        
        tE.setWinCount(1); 
        tF.setWinCount(1); 
        FootballMatch drawMatch = new FootballMatch(tE, tF);
        drawMatch.setTeam1Score(1); 
        drawMatch.setTeam2Score(1); 
        drawMatch.setFinished(true);
        league.addCompletedMatch(drawMatch);
        
        FootballMatch gdMatchE = new FootballMatch(tE, tA);
        gdMatchE.setTeam1Score(5); 
        gdMatchE.setTeam2Score(0); 
        gdMatchE.setFinished(true);
        FootballMatch gdMatchF = new FootballMatch(tF, tB);
        gdMatchF.setTeam1Score(2); 
        gdMatchF.setTeam2Score(0); 
        gdMatchF.setFinished(true);
        league.addCompletedMatch(gdMatchE);
        league.addCompletedMatch(gdMatchF);
        
        FootballTeam tG = new FootballTeam("G", "C", "4-4-2");
        FootballTeam tH = new FootballTeam("H", "C", "4-4-2");
        league.addTeam(tG); 
        league.addTeam(tH);
        
        league.calculateLeagueResult();
        List<AbstractTeam> sortedTeams = league.getTeams();
        
        assertEquals(tA, sortedTeams.get(0), "A should be 1st due to having the highest points.");
        assertTrue(sortedTeams.indexOf(tC) < sortedTeams.indexOf(tD), "In case of a points tie, C should rank above D due to Head-to-Head advantage.");
        assertTrue(sortedTeams.indexOf(tE) < sortedTeams.indexOf(tF), "In case of a Head-to-Head tie, E should rank above F due to Goal Difference advantage.");
        assertTrue(sortedTeams.indexOf(tG) < sortedTeams.indexOf(tH), "If all statistics are equal, alphabetical sorting should apply (G comes before H).");
        List<AbstractTeam> expectedOrder = List.of(tA, tB, tE, tC, tF, tD, tG, tH);
        assertEquals(expectedOrder, sortedTeams, "The entire league standings should be perfectly ordered from first to last.");
    }

    @Test
    public void testPlayWeeklyMatchAllCases() {
        // TEST THAT A MATCH SHOULD NOT BE PLAYED WITHOUT A LEAGUE
        assertDoesNotThrow(() -> league.playWeeklyMatch(), "Playing matches without generating fixtures should be handled.");

        // Test if injury values decrease, matches are added, and the week increases after matches are completed
        setUp();
        FootballTeam homeTeam = new FootballTeam("Home", "C", "4-4-2");
        FootballTeam awayTeam = new FootballTeam("Away", "C", "4-4-2");
        league.addTeam(homeTeam);
        league.addTeam(awayTeam);
        league.setTotalWeeks(2); 
        FootballMatch mockMatch = new FootballMatch(homeTeam, awayTeam) {
            @Override
            public void playPeriod() {
                setTeam1Score(2);
                setTeam2Score(1);
                setFinished(true);
            }
        };
        league.addWeeklyFixture(1, List.of(mockMatch));
        FootballPlayer injuredPlayer = new FootballPlayer("Injured", "Forward");
        injuredPlayer.makeInjury(2);
        homeTeam.addPlayerToRoster(injuredPlayer); 

        league.playWeeklyMatch(); 
        
        assertTrue(league.getCompletedMatches().contains(mockMatch), "Finnished matches should be added to complete matches.");
        assertEquals(1, homeTeam.getWinCount(), "Home team won the match so winCount should increase by 1.");
        assertEquals(1, awayTeam.getLossCount(), "Away team lost the match so lossCount should increase by 1.");
        assertEquals(2, league.getCurrentWeek(), "After the week ends current week should increase by 1.");
        assertEquals(1, injuredPlayer.getInjuryRemainingDuration(), "Injury durations should decrease by 1 after a week ends.");

    }
    @Test
    public void testPlayWeeklyMatch_AfterLeagueEnds() {
        league.setTotalWeeks(2);
        league.advanceWeek(); 
        league.playWeeklyMatch(); 
        assertEquals(2, league.getCurrentWeek(), "If the league is at its final week, it should not increase to 3.");
    }

    @Test
    public void testLeagueStateAndEncapsulationAllCases() {
        // total weeks should not be negative
        league.setTotalWeeks(10);
        league.setTotalWeeks(-5);
        assertEquals(10, league.getTotalWeeks(), "Negative total weeks should be ignored and the old value should be kept.");

        // Test that teams cannot be deleted externally
        setUp();
        league.addTeam(team1);
        List<AbstractTeam> retrievedTeams = league.getTeams();
        try {
            retrievedTeams.clear(); 
        } catch (UnsupportedOperationException e) {
        }
        assertEquals(1, league.getTeams().size(), "The internal teams list should not be modified externally.");

        // Test that a team cannot be added after the league starts
        setUp();
        league.setTotalWeeks(38);
        league.advanceWeek(); 
        league.addTeam(team2);
        assertFalse(league.getTeams().contains(team2), "A new team cannot be added after the league has started (currentWeek > 1).");
    }
}