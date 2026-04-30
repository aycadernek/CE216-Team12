package sportsmanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DataGenerator {

    private static final Random random = new Random();

    private static final List<String> PLAYER_NAMES =
            loadLines("/data/player_names.txt");

    private static final List<String> COACH_NAMES =
            loadLines("/data/coach_names.txt");

    private static final List<String> TEAM_NAMES =
            loadLines("/data/team_names.txt");

    private static final List<String> FOOTBALL_POSITIONS =
            loadLines("/data/football_positions.txt");

    private static final List<String> HANDBALL_POSITIONS =
            loadLines("/data/handball_positions.txt");

    private DataGenerator() {
    }

    private static List<String> loadLines(String resourcePath) {
        List<String> lines = new ArrayList<>();

        try (InputStream inputStream = DataGenerator.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Resource file not found: " + resourcePath);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    line = line.trim();

                    if (!line.isEmpty()) {
                        lines.add(line);
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not load resource file: " + resourcePath, e);
        }

        return lines;
    }

    private static String getRandomItem(List<String> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalStateException("Cannot select from an empty list.");
        }

        return list.get(random.nextInt(list.size()));
    }

    public static String generatePlayerName() {
        return getRandomItem(PLAYER_NAMES);
    }

    public static String generateCoachName() {
        return getRandomItem(COACH_NAMES);
    }

    public static String generateTeamName() {
        return getRandomItem(TEAM_NAMES);
    }

    public static String generateFootballPosition() {
        return getRandomItem(FOOTBALL_POSITIONS);
    }

    public static String generateHandballPosition() {
        return getRandomItem(HANDBALL_POSITIONS);
    }

    public static FootballPlayer generateFootballPlayer() {
        return new FootballPlayer(generatePlayerName(), generateFootballPosition());
    }

    public static HandballPlayer generateHandballPlayer() {
        return new HandballPlayer(generatePlayerName(), generateHandballPosition());
    }

    public static List<AbstractPlayer> generateFootballPlayers(int count) {
        List<AbstractPlayer> players = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            players.add(generateFootballPlayer());
        }

        return players;
    }

    public static List<AbstractPlayer> generateHandballPlayers(int count) {
        List<AbstractPlayer> players = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            players.add(generateHandballPlayer());
        }

        return players;
    }

    public static FootballTeam generateFootballTeam() {
        FootballTeam team = new FootballTeam(
                generateTeamName(),
                generateCoachName(),
                "4-4-2"
        );

        List<AbstractPlayer> players = generateFootballPlayers(18);

        team.setAllPlayers(players);
        team.setMatchDayLineup(
                new ArrayList<>(players.subList(0, 11)),
                new ArrayList<>(players.subList(11, 18))
        );

        return team;
    }

    public static HandballTeam generateHandballTeam() {
        HandballTeam team = new HandballTeam(
                generateTeamName(),
                generateCoachName(),
                "6-0"
        );

        List<AbstractPlayer> players = generateHandballPlayers(14);

        team.setAllPlayers(players);
        team.setMatchDayLineup(
                new ArrayList<>(players.subList(0, 7)),
                new ArrayList<>(players.subList(7, 14))
        );

        return team;
    }

    public static List<FootballTeam> generateFootballTeams(int teamCount) {
        List<FootballTeam> teams = new ArrayList<>();

        for (int i = 0; i < teamCount; i++) {
            teams.add(generateFootballTeam());
        }

        return teams;
    }

    public static List<HandballTeam> generateHandballTeams(int teamCount) {
        List<HandballTeam> teams = new ArrayList<>();

        for (int i = 0; i < teamCount; i++) {
            teams.add(generateHandballTeam());
        }

        return teams;
    }

    public static List<String> getAvailableTeamNames() {
        return Collections.unmodifiableList(TEAM_NAMES);
    }

    public static List<String> getAvailablePlayerNames() {
        return Collections.unmodifiableList(PLAYER_NAMES);
    }

    public static List<String> getAvailableCoachNames() {
        return Collections.unmodifiableList(COACH_NAMES);
    }
}