package sportsmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Random;
import java.lang.reflect.Field;

public class FootballMatchTest {

    private FootballMatch match;
    private FootballTeam homeTeam;
    private FootballTeam awayTeam;
    private FakeRandom controlledRandom;

    private static class FakeRandom extends Random {
        public int selectedEvent = 20;

        @Override
        public int nextInt(int bound) {
            if (bound == 30) return selectedEvent; 
            if (bound == 6) return 0; 
            return 0; 
        }
    }

    @BeforeEach
    public void setUp() throws Exception {
        homeTeam = new FootballTeam("Team 1", "Coach", "4-2-3-1");
        awayTeam = new FootballTeam("Team 2", "Coach", "4-2-3-1");
        match = new FootballMatch(homeTeam, awayTeam);
        controlledRandom = new FakeRandom();
        Field randomField = FootballMatch.class.getDeclaredField("random");
        randomField.setAccessible(true);
        randomField.set(match, controlledRandom);
    }

    @Test
    
    public void testInitializationAndSetters() {
        // Test that a match should not be created with null teams
        assertThrows(IllegalArgumentException.class, () -> {
            new FootballMatch(null, awayTeam);
        }, "Home team cannot be NULL.");
        
        assertThrows(IllegalArgumentException.class, () -> {
            new FootballMatch(homeTeam, null);
        }, "Away team cannot be NULL.");

        // Test that negative scores should not be assigned
        match.setTeam1Score(-5);
        match.setTeam2Score(-2);
        assertEquals(0, match.getTeam1Score(), "Score cannot be negative.");
        assertEquals(0, match.getTeam2Score(), "Score cannot be negative.");
    }

    @Test
    public void testUpdateResultString() throws Exception {
        // Test if the winning team is determined correctly for home team
        match.setTeam1Score(2);
        match.setTeam2Score(0);
        match.setFinished(true); 
        match.updateResultString(); 
        assertEquals("Team 1 Won", match.getResult());

        // Test if the draw status is updated correctly
        setUp();
        match.setTeam1Score(1);
        match.setTeam2Score(1);
        match.setFinished(true);
        match.updateResultString();
        assertEquals("Draw", match.getResult(), "Result should be Draw when scores are equal.");

        // Test if the winning team is determined correctly for away team
        setUp();
        match.setTeam1Score(0);
        match.setTeam2Score(3);
        match.setFinished(true);
        match.updateResultString();
        assertEquals("Team 2 Won", match.getResult(), "Result string should accurately reflect the away team win.");

        // Test that the result should not be updated before the match finishes
        setUp();
        match.setTeam1Score(3);
        match.setTeam2Score(0);
        match.updateResultString(); 
        assertNotEquals("Team 1 Won", match.getResult(), "There should be no winner until the match is pver.");
        assertTrue(match.getResult() == null || match.getResult().isEmpty() || match.getResult().equals("Match not finished"), 
                   "There should be no result until the match is over.");
    }

    @Test
    public void testPlayPeriod() throws Exception {
        controlledRandom.selectedEvent = 20;

        // Test that events should be created and period number should increase when a period is played
        // By using mock random class the event selected as 20 and event count per period set to 10.
        assertEquals(0, match.getCurrentPeriod());
        assertTrue(match.getEvents().isEmpty());
        match.playPeriod();
        assertEquals(12, match.getEvents().size(), "There should be 10 events plus 2 info lines.");
        assertEquals(1, match.getCurrentPeriod(), "Period should increment by 1.");
        assertFalse(match.getEvents().isEmpty(), "Playing a period should generate match events.");
        assertFalse(match.isFinished(), "Match should not be finished after 1 period.");

        // Test that the match should finish when the 2nd period ends
        match.playPeriod(); 
        assertTrue(match.isFinished(), "Match should be marked finished after 2 periods.");
        assertEquals(2, match.getCurrentPeriod());

        // Test that another period should not be played after the match finishes
        int eventSizeAfterMatch = match.getEvents().size();
        match.playPeriod();
        assertEquals(2, match.getCurrentPeriod(), "Current period should not exceed total periods.");
        assertTrue(match.isFinished(), "Match must remain finished.");
        assertEquals(eventSizeAfterMatch, match.getEvents().size(), "No new events should be created after the match is finished.");

        // Test that events should not be added after the match finishes
        setUp();
        match.setFinished(true);
        match.playPeriod();
        match.createEvent();
        assertTrue(match.getEvents().isEmpty(), "No events should be created if match is already finished.");
    }

    @Test
    public void testCreateEvent_DirectRedCard() {
        // Red card test
        FootballPlayer p1 = new FootballPlayer("Player", "Forward");
        homeTeam.getActivePlayers().add(p1);

        controlledRandom.selectedEvent = 4;
        match.createEvent();
        
        assertTrue(homeTeam.getActivePlayers().isEmpty(), "Player that get Red Card should not continue to play.");
        assertTrue(match.getEvents().stream().anyMatch(e -> e.contains("DIRECT RED CARD")), "Red Card event should be added to events list");
    }

    @Test
    public void testCreateEvent_EmptyTeam() {
        // Test that there should be no goal when there are no people in the squad
        controlledRandom.selectedEvent = 0; 
        match.createEvent(); 
        
        assertEquals(0, match.getTeam1Score(), "Goal event should be passed if there is no player on the field");
    }

    @Test
    public void testCreateEvent_InjuryWithSubstitute() {
        FootballPlayer p1 = new FootballPlayer("Player1", "Forward");
        p1.setInjured(false);
        homeTeam.getActivePlayers().add(p1);
        
        FootballPlayer subPlayer = new FootballPlayer("Player2", "Forward");
        homeTeam.getSubstitutePlayers().add(subPlayer); 
        
        controlledRandom.selectedEvent = 6; 
        match.createEvent();
        
        assertFalse(homeTeam.getActivePlayers().contains(p1), "Injured player should be out.");
        assertTrue(homeTeam.getActivePlayers().contains(subPlayer), "Substitute should enter the field.");
        assertTrue(homeTeam.getSubstitutePlayers().isEmpty(), "Substitue should be removed from substitute list.");
    }

    @Test
    public void testCreateEvent_InjuryWithoutSubstitute() {
        FootballPlayer p1 = new FootballPlayer("Player1", "Forward");
        p1.setInjured(false);
        homeTeam.getActivePlayers().add(p1);
        
        controlledRandom.selectedEvent = 6; 
        match.createEvent();
        
        assertTrue(homeTeam.getActivePlayers().isEmpty(), "When there is no substitute player should be removed from field.");
        assertTrue(match.getEvents().stream().anyMatch(e -> e.contains("has no subs")), "User should be notified by event that there is no substitute left.");
    }

    @Test
    public void testCreateEvent_SecondYellowCard() {
        FootballPlayer p1 = new FootballPlayer("Player1", "Forward");
        p1.setYellowCards(1); 
        homeTeam.getActivePlayers().add(p1);
        
        controlledRandom.selectedEvent = 2;
        match.createEvent();
        
        assertTrue(homeTeam.getActivePlayers().isEmpty(), "Player with second yellow card should be removed from field.");
        assertTrue(match.getEvents().stream().anyMatch(e -> e.contains("SECOND YELLOW")), "There should be second yellow card event.");
    }
}