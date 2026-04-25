package sportsmanager;

import java.util.List;
import java.util.ArrayList;

public class FootballLeague extends AbstractLeague {

    public FootballLeague() {
    }
    
    public FootballLeague(String leagueName) {
        super(leagueName, new Football());
    }

    @Override
    public void createLeague() {
        List<AbstractTeam> teams = getTeams();
        if (teams.size() < 2) return; 
        int numTeams = teams.size();
        boolean odd = (numTeams % 2 != 0);
        int numWeeksInHalf = (odd ? numTeams : numTeams - 1);

        List<AbstractTeam> tempTeams = new ArrayList<>(teams);
        if (odd) tempTeams.add(null); 
        numTeams = tempTeams.size();
        int halfSize = numTeams / 2;

        int currentWeekCount = 1; 
        for (int week = 0; week < numWeeksInHalf; week++) {
            List<AbstractMatch> matchesForThisWeek = new ArrayList<>();
            
            for (int idx = 0; idx < halfSize; idx++) {
                int firstTeam = (week + idx) % (numTeams - 1);
                int secondTeam = (numTeams - 1 - idx + week) % (numTeams - 1);
                if (idx == 0) secondTeam = numTeams - 1;
                
                AbstractTeam home = tempTeams.get(firstTeam);
                AbstractTeam away = tempTeams.get(secondTeam);
                if (home != null && away != null) {
                    matchesForThisWeek.add(new FootballMatch((FootballTeam)home, (FootballTeam)away));
                }
            }

            addWeeklyFixture(currentWeekCount++, matchesForThisWeek);
        }
        
        for (int week = 1; week <= numWeeksInHalf; week++) {
            List<AbstractMatch> firstHalfMatches = getWeeklyFixtures().get(week);
            List<AbstractMatch> reversedMatchesForThisWeek = new ArrayList<>();
            
            for (AbstractMatch m : firstHalfMatches) {
                reversedMatchesForThisWeek.add(new FootballMatch(
                    (FootballTeam)m.getTeam2(), 
                    (FootballTeam)m.getTeam1() 
                ));
            }
            addWeeklyFixture(currentWeekCount++, reversedMatchesForThisWeek);
        }
        setTotalWeeks(currentWeekCount - 1);
    }

    @Override
    public void playWeeklyMatch() {
        if (getCurrentWeek() > getTotalWeeks()) return;
        List<AbstractMatch> currentWeekMatches = getMatchesForWeek(getCurrentWeek());
        
        if (currentWeekMatches.isEmpty()) {
            advanceWeek();
            return;
        }

        boolean weekFinalized = false;
        for (AbstractMatch match : currentWeekMatches) {
            if (!match.isFinished()) {
                while (!match.isFinished()) {
                    match.playPeriod();
                }
            }
        }
        for (AbstractMatch match : currentWeekMatches) {
            if (match.isFinished() && !getCompletedMatches().contains(match)) {
                addCompletedMatch(match);
                updateTeamStats(match);
                weekFinalized = true;
            }
        }
        if (weekFinalized) {
            decrementInjuries();
        }
        
        boolean allFinished = true;
        for (AbstractMatch match : currentWeekMatches) {
            if (!match.isFinished()) {
                allFinished = false;
                break;
            }
        }
        if (allFinished) {
            advanceWeek();
        }
    }
    
    private void updateTeamStats(AbstractMatch match) {
        FootballTeam home = (FootballTeam)match.getTeam1();
        FootballTeam away = (FootballTeam)match.getTeam2();
        if (match.getTeam1Score() > match.getTeam2Score()) {
            home.setWinCount(home.getWinCount() + 1);
            away.setLossCount(away.getLossCount() + 1);
        } else if (match.getTeam1Score() < match.getTeam2Score()) {
            away.setWinCount(away.getWinCount() + 1);
            home.setLossCount(home.getLossCount() + 1);
        } else {
            home.setDrawCount(home.getDrawCount() + 1);
            away.setDrawCount(away.getDrawCount() + 1);
        }
    }
    
    private void decrementInjuries() {
        for (AbstractTeam team : getTeams()) {
            for (AbstractPlayer player : team.getAllPlayers()) {
                if (player.isInjured() && player.getInjuryRemainingDuration() > 0) {
                    player.setInjuryRemainingDuration(player.getInjuryRemainingDuration() - 1);
                }
            }
        }
    }

    @Override
    public void calculateLeagueResult() {
        sortTeams((t1, t2) -> {
            int points1 = (t1.getWinCount() * 3) + t1.getDrawCount();
            int points2 = (t2.getWinCount() * 3) + t2.getDrawCount();
            
            if (points1 != points2) {
                return Integer.compare(points2, points1);
            }
            int t1HeadToHeadPts = 0;
            int t2HeadToHeadPts = 0;
            for (AbstractMatch match : getCompletedMatches()) {
                if ((match.getTeam1().equals(t1) && match.getTeam2().equals(t2)) || 
                    (match.getTeam1().equals(t2) && match.getTeam2().equals(t1))) {
                    
                    if (match.getTeam1Score() > match.getTeam2Score()) {
                        if (match.getTeam1().equals(t1)) t1HeadToHeadPts += 3;
                        else t2HeadToHeadPts += 3;
                    } else if (match.getTeam2Score() > match.getTeam1Score()) {
                        if (match.getTeam2().equals(t1)) t1HeadToHeadPts += 3;
                        else t2HeadToHeadPts += 3;
                    } else {
                        t1HeadToHeadPts += 1;
                        t2HeadToHeadPts += 1;
                    }
                }
            }
            if (t1HeadToHeadPts != t2HeadToHeadPts) {
                 return Integer.compare(t2HeadToHeadPts, t1HeadToHeadPts);
            }
            
            int gd1 = getGoalDifference(t1);
            int gd2 = getGoalDifference(t2);
            if (gd1 != gd2) {
                return Integer.compare(gd2, gd1);
            }
            return t1.getName().compareTo(t2.getName()); 
        });
    }

    private int getGoalDifference(AbstractTeam team) {
        int scored = 0;
        int conceded = 0;
        for (AbstractMatch match : getCompletedMatches()) {
            if (match.getTeam1().equals(team)) {
                scored += match.getTeam1Score();
                conceded += match.getTeam2Score();
            } else if (match.getTeam2().equals(team)) {
                scored += match.getTeam2Score();
                conceded += match.getTeam1Score();
            }
        }
        return scored - conceded;
    }
}