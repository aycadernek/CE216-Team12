package sportsmanager;

import java.util.ArrayList;
import java.util.List;

public class HandballLeague extends AbstractLeague {

    public HandballLeague() {}

    public HandballLeague(String leagueName) {
        super(leagueName, new Handball());
    }

    @Override
    public void createLeague() {
        List<AbstractTeam> teams = getTeams();
        if (teams == null || teams.size() < 2) {
            throw new IllegalStateException("Not enough teams to generate a league fixture.");
        }
        List<AbstractTeam> tempTeams = new ArrayList<>(teams);
        if (tempTeams.size() % 2 != 0) {
            tempTeams.add(null);
        }

        int numTeams = tempTeams.size();
        int totalWeeksRequired = (numTeams - 1) * 2;
        setTotalWeeks(totalWeeksRequired);

        int currentWeekIndex = 1;
        for (int half = 0; half < 2; half++) {
            for (int week = 0; week < numTeams - 1; week++) {
                List<AbstractMatch> weeklyMatches = new ArrayList<>();

                for (int i = 0; i < numTeams / 2; i++) {
                    AbstractTeam home = tempTeams.get(i);
                    AbstractTeam away = tempTeams.get(numTeams - 1 - i);

                    if (home != null && away != null) {
                        AbstractMatch match = (half == 0) ? new HandballMatch(home, away) : new HandballMatch(away, home);
                        weeklyMatches.add(match);
                    }
                }

                addWeeklyFixture(currentWeekIndex, weeklyMatches);
                currentWeekIndex++;

                AbstractTeam lastTeam = tempTeams.remove(tempTeams.size() - 1);
                tempTeams.add(1, lastTeam);
            }
        }
    }

    @Override
    public void playWeeklyMatch() {
        if (getCurrentWeek() > getTotalWeeks()) return;
        List<AbstractMatch> currentWeekMatches = getMatchesForWeek(getCurrentWeek());


        if(currentWeekMatches.isEmpty()) {
            advanceWeek();
            return;
        }

            for (AbstractMatch match : currentWeekMatches) {
                if (!match.isFinished()) {
                    for (int i = 0; i < match.getTotalPeriods(); i++) {
                        match.playPeriod();
                    }
                    match.setFinished(true);
                    updateTeamStats(match);
                    addCompletedMatch(match);
                }
            }

        decrementInjuries();
        advanceWeek();
    }

    public void updateTeamStats(AbstractMatch match) {
        HandballTeam home = (HandballTeam) match.getTeam1();
        HandballTeam away = (HandballTeam) match.getTeam2();
        int homeScore = match.getTeam1Score();
        int awayScore = match.getTeam2Score();

        if (homeScore > awayScore) {
            home.setWinCount(home.getWinCount() + 1);
            away.setLossCount(away.getLossCount() + 1);
        } else if (awayScore > homeScore) {
            away.setWinCount(away.getWinCount() + 1);
            home.setLossCount(home.getLossCount() + 1);
        } else {
            home.setDrawCount(home.getDrawCount() + 1);
            away.setDrawCount(away.getDrawCount() + 1);
        }
    }

    public void decrementInjuries() {
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
            int points1 = (t1.getWinCount() * 2) + t1.getDrawCount();
            int points2 = (t2.getWinCount() * 2) + t2.getDrawCount();

            if (points1 != points2) {
                return Integer.compare(points2, points1);
            }
            int t1HeadToHeadPts = 0;
            int t2HeadToHeadPts = 0;
            for (AbstractMatch match : getCompletedMatches()) {
                if ((match.getTeam1().equals(t1) && match.getTeam2().equals(t2)) ||
                        (match.getTeam1().equals(t2) && match.getTeam2().equals(t1))) {

                    if (match.getTeam1Score() > match.getTeam2Score()) {
                        if (match.getTeam1().equals(t1)) t1HeadToHeadPts += 2;
                        else t2HeadToHeadPts += 2;
                    } else if (match.getTeam2Score() > match.getTeam1Score()) {
                        if (match.getTeam2().equals(t1)) t1HeadToHeadPts += 2;
                        else t2HeadToHeadPts += 2;
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

    public int getGoalDifference(AbstractTeam team) {
        int goalDifference = 0;

        for (AbstractMatch match : getCompletedMatches()) {
            if (match.getTeam1().equals(team)) {
                goalDifference += (match.getTeam1Score() - match.getTeam2Score());
            } else if (match.getTeam2().equals(team)) {
                goalDifference += (match.getTeam2Score() - match.getTeam1Score());
            }
        }
        return goalDifference;
    }
}
