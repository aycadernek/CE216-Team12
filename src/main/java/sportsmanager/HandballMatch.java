package sportsmanager;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class HandballMatch extends AbstractMatch {

    @JsonIgnore
    private Random random;
    private Queue<Integer> plannedEventMinutes;

    public HandballMatch() {
        this.random = new Random();
        this.plannedEventMinutes = new LinkedList<>();
    }

    public HandballMatch(AbstractTeam team1, AbstractTeam team2) {
        super(team1, team2, 2); 
        this.random = new Random();
        this.plannedEventMinutes = new LinkedList<>();
        ((HandballTeam) team1).resetMatchStats();
        ((HandballTeam) team2).resetMatchStats();
    }

    @Override
    public void playPeriod() {
        if (isFinished()) return;

        setCurrentPeriod(getCurrentPeriod() + 1);
        String periodStr = (getCurrentPeriod() == 1) ? "FIRST HALF" : "SECOND HALF";
        getEvents().add("--- " + periodStr + " STARTED ---");

        int eventCount = random.nextInt(16) + 35; 
        List<Integer> eventMinutes = new ArrayList<>();
        
        for (int i = 0; i < eventCount; i++) {
            int min = random.nextInt(30) + (getCurrentPeriod() == 1 ? 1 : 31);
            eventMinutes.add(min);
        }
        Collections.sort(eventMinutes);
        plannedEventMinutes.addAll(eventMinutes);

        for (int i = 0; i < eventCount; i++) {
            createEvent();
        }

        getEvents().add("--- " + periodStr + " ENDED ---");
        if (getCurrentPeriod() >= getTotalPeriods()) {
            setFinished(true);
            updateResultString();
        }
    }

    @Override
    public void createEvent() {
        if (isFinished()) return;

        int eventProbability = random.nextInt(35); 

        int minute = plannedEventMinutes.isEmpty() 
            ? (random.nextInt(30) + (getCurrentPeriod() == 1 ? 1 : 31))
            : plannedEventMinutes.poll();

        String timeStr = "Min " + minute + "'";

        HandballTeam home = (HandballTeam) getTeam1();
        HandballTeam away = (HandballTeam) getTeam2();
        
        HandballTeam activeTeam = (eventProbability % 2 == 0) ? home : away;

        switch (eventProbability) {
            case 0, 1, 20, 21, 26, 27, 28, 29, 30, 31, 32, 33: 
                handleGoal(activeTeam, timeStr); break;
            case 2, 3, 22, 23, 34: 
                handleFastBreak(activeTeam, timeStr); break;
            case 4, 5, 24: 
                handle7MeterPenalty(activeTeam, timeStr); break;
            case 6, 7, 25: 
                handleSave(activeTeam, timeStr); break;
            case 8, 9: 
                handleMissedShot(activeTeam, timeStr); break;
            case 10, 11: 
                handleTurnover(activeTeam, timeStr); break;
            case 12, 13: 
                handle2MinuteSuspension(activeTeam, timeStr); break;
            case 14, 15: 
                handleYellowCard(activeTeam, timeStr); break;
            case 16, 17: 
                handleRedCard(activeTeam, timeStr); break;
            case 18, 19:
                handleInjury(activeTeam, timeStr); break;
            default: break;
        }
    }

    private void handleGoal(HandballTeam team, String timeStr) {
        HandballPlayer scorer = getRandomPlayer(team);
        if (scorer == null) return;

        incrementScore(team);
        if (!scorer.isInjured()) scorer.scoreGoal();
        getEvents().add(timeStr + ": GOAL! " + team.getName() + " (" + scorer.getName() + ")");
    }

    private void handleFastBreak(HandballTeam team, String timeStr) {
        HandballPlayer scorer = getRandomPlayer(team);
        if (scorer == null) return;

        incrementScore(team);
        if (!scorer.isInjured()) scorer.scoreGoal(); 
        getEvents().add(timeStr + ": FAST BREAK GOAL! " + team.getName() + " (" + scorer.getName() + ")");
    }

    private void handle7MeterPenalty(HandballTeam team, String timeStr) {
        HandballPlayer scorer = getRandomPlayer(team);
        if (scorer == null) return;

        if (random.nextInt(100) < 80) {
            incrementScore(team);
            if (!scorer.isInjured()) scorer.scoreGoal();
            getEvents().add(timeStr + ": 7-METER GOAL! " + team.getName() + " (" + scorer.getName() + ")");
        } else {
            getEvents().add(timeStr + ": 7-METER MISSED! " + scorer.getName() + " loses the ball.");
        }
    }

    private void handleSave(HandballTeam team, String timeStr) {
        HandballPlayer player = getRandomPlayer(team);
        getEvents().add(timeStr + ": AMAZING SAVE! The goalkeeper blocks a powerful shot from " + (player != null ? player.getName() : "Player") + " (" + team.getName() + ").");
    }

    private void handleMissedShot(HandballTeam team, String timeStr) {
        HandballPlayer player = getRandomPlayer(team);
        getEvents().add(timeStr + ": SHOT WIDE! " + (player != null ? player.getName() : "Player") + " shoots off target.");
    }

    private void handleTurnover(HandballTeam team, String timeStr) {
        getEvents().add(timeStr + ": TURNOVER! " + team.getName() + " loses the ball.");
    }

    private void handleYellowCard(HandballTeam team, String timeStr) {
        HandballPlayer player = getRandomPlayer(team);
        if (player == null) return;
        if (player.getYellowCards() >= 1 ||team.getMatchYellowCards() >= 3) { 
            getEvents().add(timeStr + ": YELLOW CARD QUOTA FULL! Direct 2-minute suspension.");   
            handle2MinuteSuspensionHandle(team, player, timeStr);
        } else {
            player.receiveYellowCard(); 
            team.addMatchYellowCard();
            getEvents().add(timeStr + ": YELLOW CARD - " + player.getName() + " receives a warning. (" + team.getName() + " Total Yellows: " + team.getMatchYellowCards() + "/3)");
        }
    }

    private void handle2MinuteSuspension(HandballTeam team, String timeStr) {
        HandballPlayer suspended = getRandomPlayer(team);
        if (suspended == null) return;
        handle2MinuteSuspensionHandle(team, suspended, timeStr);
    }
    
    private void handle2MinuteSuspensionHandle(HandballTeam team, HandballPlayer suspended, String timeStr) {
        suspended.receiveTwoMinuteSuspension(); 
        if (suspended.getTwoMinuteSuspensions() >= 3) {  
            removePlayerFromPitch(team, suspended, timeStr, "DISQUALIFICATION");
        } else {
            getEvents().add(timeStr + ": 2-MINUTE SUSPENSION! " + suspended.getName() + " is sent to the bench. " + team.getName());
        }
    }

    private void handleRedCard(HandballTeam team, String timeStr) {
        HandballPlayer carded = getRandomPlayer(team);
        if (carded != null) {
            if (random.nextInt(100) < 15) {
                removePlayerFromPitch(team, carded, timeStr, "BLUE_CARD");
            } else {
                removePlayerFromPitch(team, carded, timeStr, "DIRECT_DISQUALIFICATION");
            }
        }
    }
    
    private void handleInjury(HandballTeam team, String timeStr) {
        HandballPlayer injured = getRandomPlayer(team);
        if (injured != null && !injured.isInjured()) {
            injured.makeInjury(random.nextInt(4) + 1); 
            removePlayerFromPitch(team, injured, timeStr, "INJURY");
        }
    }

    private void removePlayerFromPitch(HandballTeam team, HandballPlayer outPlayer, String timeStr, String reason) {
        team.getActivePlayers().remove(outPlayer);

        if (reason.equals("DISQUALIFICATION")) {
            getEvents().add(timeStr + ": RED CARD (3x 2-MIN)! " + outPlayer.getName() + " is disqualified! " + team.getName());
        } else if (reason.equals("DIRECT_DISQUALIFICATION")) {
            getEvents().add(timeStr + ": DIRECT RED CARD! " + outPlayer.getName() + " is disqualified! " + team.getName());
        } else if (reason.equals("BLUE_CARD")) {
            getEvents().add(timeStr + ": BLUE CARD! " + outPlayer.getName() + team.getName());
        } else if (reason.equals("INJURY")) {
            List<AbstractPlayer> bench = team.getSubstitutePlayers();
            if (bench != null && !bench.isEmpty()) {
                AbstractPlayer sub = bench.get(0);
                team.getActivePlayers().add(sub);
                bench.remove(sub);
                getEvents().add(timeStr + ": INJURY! " + outPlayer.getName() + " out, " + sub.getName() + " in (" + team.getName() + ").");
            } else {
                getEvents().add(timeStr + ": INJURY! " + outPlayer.getName() + " is out! " + team.getName() + " has no substitutes left.");
            }
            return;
        }
        List<AbstractPlayer> bench = team.getSubstitutePlayers();
        if (bench != null && !bench.isEmpty()) {
            AbstractPlayer sub = bench.remove(0); 
            team.getActivePlayers().add(sub); 
        }
    }

    private void incrementScore(HandballTeam team) {
        if (team == getTeam1()) {
            setTeam1Score(getTeam1Score() + 1);
        } else {
            setTeam2Score(getTeam2Score() + 1);
        }
    }

    private HandballPlayer getRandomPlayer(HandballTeam team) {
        List<AbstractPlayer> active = team.getActivePlayers();
        if (active == null || active.isEmpty()) {
            return null;
        }
        int idx = random.nextInt(active.size());
        return (HandballPlayer) active.get(idx);
    }

    protected void updateResultString() {
        if (!isFinished()) return;
        if (getTeam1Score() > getTeam2Score()) {
            setResult(getTeam1().getName() + " Won");
        } else if (getTeam2Score() > getTeam1Score()) {
            setResult(getTeam2().getName() + " Won");
        } else {
            setResult("Draw");
        }
    }
}