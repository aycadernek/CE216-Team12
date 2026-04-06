package sportsmanager;

import java.util.List;
import java.util.Random;

public class FootballMatch extends AbstractMatch {

    private Random random;

    public FootballMatch(AbstractTeam team1, AbstractTeam team2) {
        super(team1, team2, 2); 
        this.random = new Random();
    }

    @Override
    public void playPeriod() {
        if (isFinished()) return;
        
        setCurrentPeriod(getCurrentPeriod() + 1);
        String periodStr = (getCurrentPeriod() == 1) ? "FIRST HALF" : "SECOND HALF";
        getEvents().add("--- " + periodStr + " STARTED ---");

        int eventCount = random.nextInt(6) + 10;
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
        
        int eventProbability = random.nextInt(30); 
        String timeStr = "Min " + (random.nextInt(45) + (getCurrentPeriod() == 1 ? 1 : 46)) + "'";
        
        FootballTeam home = (FootballTeam) getTeam1();
        FootballTeam away = (FootballTeam) getTeam2();
        FootballTeam activeTeam = (eventProbability % 2 == 0) ? home : away;
        switch (eventProbability) {
            case 0, 1:   
                handleGoal(activeTeam, timeStr); break;
            case 2, 3:   
                handleYellowCard(activeTeam, timeStr); break;
            case 4, 5:   
                handleRedCard(activeTeam, timeStr); break; 
            case 6, 7:
                handleInjury(activeTeam, timeStr); break;
            case 8, 9:   
                handleMissedShot(activeTeam, timeStr); break;
            case 10, 11, 20, 21: 
                handleSave(activeTeam, timeStr); break;
            case 12, 13, 22, 23: 
                handleDispossessed(activeTeam, timeStr); break;
            case 14, 15, 24, 25: 
                handleDangerousAttack(activeTeam, timeStr); break;
            case 16, 17, 26, 27: 
                handlePossession(activeTeam, timeStr); break;
            case 18, 19, 28, 29: 
                handleWoodwork(activeTeam, timeStr); break;
            default: break;
        }
    }
    private void handleGoal(FootballTeam team, String timeStr) {
        FootballPlayer scorer = getRandomPlayer(team);
        if (scorer == null) return;
        
        if (team == getTeam1()) setTeam1Score(getTeam1Score() + 1);
        else setTeam2Score(getTeam2Score() + 1);
        
        if (!scorer.isInjured()) scorer.scoreGoal();
        getEvents().add(timeStr + ": GOAL! " + team.getName() + " (" + scorer.getName() + ")");
    }

    private void handleYellowCard(FootballTeam team, String timeStr) {
       FootballPlayer carded = getRandomPlayer(team);
        if (carded == null) return;
        carded.receiveYellowCard();
        if (carded.getYellowCards() >= 2) {
            removePlayerFromPitch(team, carded, timeStr, "SECOND_YELLOW");
        } else {
            getEvents().add(timeStr + ": YELLOW CARD - " + carded.getName() + " (" + team.getName() + ")");
        }
    }

    private void handleRedCard(FootballTeam team, String timeStr) {
        FootballPlayer carded = getRandomPlayer(team);
        if (carded != null) {
            removePlayerFromPitch(team, carded, timeStr, "RED_CARD");
        }
    }

    private void handleInjury(FootballTeam team, String timeStr) {
        FootballPlayer injured = getRandomPlayer(team);
        if (injured != null && !injured.isInjured()) {
            injured.makeInjury(random.nextInt(4)+ 1); 
            removePlayerFromPitch(team, injured, timeStr, "INJURY");
        }
    }

    private void handleMissedShot(FootballTeam team, String timeStr) {
        FootballPlayer player = getRandomPlayer(team);
        getEvents().add(timeStr + ": SHOT WIDE! " + (player != null ? player.getName() : "Player") + " (" + team.getName() + ") shoots off target.");
    }

    private void handleSave(FootballTeam team, String timeStr) {
        FootballPlayer player = getRandomPlayer(team);
        getEvents().add(timeStr + ": GREAT SAVE! The keeper denies " + (player != null ? player.getName() : "Player") + " (" + team.getName() + ").");
    }

    private void handleDispossessed(FootballTeam team, String timeStr) {
        FootballPlayer player = getRandomPlayer(team);
        getEvents().add(timeStr + ": DISPOSSESSED! " + (player != null ? player.getName() : "Player") + " (" + team.getName() + ") loses the ball.");
    }

    private void handleDangerousAttack(FootballTeam team, String timeStr) {
        getEvents().add(timeStr + ": DANGEROUS ATTACK! " + team.getName() + " is pressing high up the pitch.");
    }

    private void handlePossession(FootballTeam team, String timeStr) {
        getEvents().add(timeStr + ": POSSESSION! " + team.getName() + " controls the tempo.");
    }

    private void handleWoodwork(FootballTeam team, String timeStr) {
        FootballPlayer player = getRandomPlayer(team);
        getEvents().add(timeStr + ": WOODWORK! " + (player != null ? player.getName() : "Player") + " (" + team.getName() + ") hits the post!");
    }
    private void removePlayerFromPitch(FootballTeam team, FootballPlayer outPlayer, String timeStr, String reason) {
        team.getActivePlayers().remove(outPlayer);
        
        if (reason.equals("RED_CARD") || reason.equals("SECOND_YELLOW")) {
            String cardText = reason.equals("RED_CARD") ? "DIRECT RED CARD" : "SECOND YELLOW";
            getEvents().add(timeStr + ": " + cardText + "! " + outPlayer.getName() + " is sent off! " + team.getName() + " is down to " + team.getActivePlayers().size() + " men.");
            
        } else if (reason.equals("INJURY")) {
            List<AbstractPlayer> bench = team.getSubstitutePlayers();
            if (bench != null && !bench.isEmpty()) {
                AbstractPlayer sub = bench.get(0); 
                team.getActivePlayers().add(sub); 
                bench.remove(sub);
                getEvents().add(timeStr + ": INJURY! " + outPlayer.getName() + " out, " + sub.getName() + " in (" + team.getName() + ").");
            } else {
                getEvents().add(timeStr + ": INJURY! " + outPlayer.getName() + " out! " + team.getName() + " has no subs and plays with " + team.getActivePlayers().size() + " men.");
            }
        }
    }

    private FootballPlayer getRandomPlayer(FootballTeam team) {
        List<AbstractPlayer> active = team.getActivePlayers();
        if (active == null || active.isEmpty()) {
            return null;
        }
        int idx = random.nextInt(active.size());
        return (FootballPlayer) active.get(idx);
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