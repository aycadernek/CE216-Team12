package sportsmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main {
     public static void main(String[] args) throws InterruptedException {

        GameStatus gameStatus = new GameStatus();
        gameStatus.setUsername("Team12");

        ISport football = new Football();
        gameStatus.setCurrentSport(football);

        //Home and Away Team for DEMO
        FootballTeam homeTeam = new FootballTeam("Göztepe", "Coach A", "4-4-2");
        FootballTeam awayTeam = new FootballTeam("Beşiktaş", "Coach B", "4-3-3");

        //Dummy Players (11 each)
        List<AbstractPlayer> homePlayers = new ArrayList<>();
        List<AbstractPlayer> awayPlayers = new ArrayList<>();

        for (int i = 1; i <= 11; i++) {
            homePlayers.add(new FootballPlayer("HomePlayer" + i, "Position"));
            awayPlayers.add(new FootballPlayer("AwayPlayer" + i, "Position"));
        }

        homeTeam.setAllPlayers(homePlayers);
        awayTeam.setAllPlayers(awayPlayers);

        homeTeam.setActivePlayers(new ArrayList<>(homePlayers));
        awayTeam.setActivePlayers(new ArrayList<>(awayPlayers));

        FootballMatch match = new FootballMatch(homeTeam, awayTeam);

        Scanner scanner = new Scanner(System.in);

        System.out.println("=== GAME STATUS ===");
        System.out.println("User: " + gameStatus.getUsername());

        //Loading
        for (int i = 0; i < 3; i++) {
            System.out.print(".");
            Thread.sleep(600);
        }

        System.out.println("\n=== FOOTBALL RULES ===");
        football.displayRules();

        //Loading
        for (int i = 0; i < 3; i++) {
            System.out.print(".");
            Thread.sleep(600);
        }

        System.out.println("\n=== FOOTBALL DETAILS ===");
        System.out.println("Players On Field: " + football.getPlayerCount());
        System.out.println("Max Substitutions: " + football.getSubstituteChangeLimit());

        System.out.println("\n=== POINT SYSTEM ===");
        System.out.println("Win Points: " + football.getWinPoints());
        System.out.println("Draw Points: " + football.getDrawPoints());
        System.out.println("Loss Points: " + football.getLossPoints());

        //Loading
        for (int i = 0; i < 3; i++) {
            System.out.print(".");
            Thread.sleep(600);
        }

        System.out.println("\n=== MATCH STARTING ===");

        System.out.println(homeTeam.getName() + " vs " + awayTeam.getName());
        System.out.println(homeTeam.getName() + " tactic: " + homeTeam.getTactic());
        System.out.println(awayTeam.getName() + " tactic: " + awayTeam.getTactic());

        System.out.print("LOADING");
        //LOADING ANIMATION
        for (int i = 0; i < 3; i++) {
            System.out.print(".");
            Thread.sleep(750);
        }

        System.out.println("\nKickoff!!");
        Thread.sleep(1000);

        match.playPeriod(); //First Half

        Thread.sleep(1000);

        System.out.println("\n--- HALFTIME ---");
        System.out.println(homeTeam.getName() + " " + match.getTeam1Score()
                + " - " + match.getTeam2Score() + " " + awayTeam.getName());

        System.out.println("\nDo you want to change " + homeTeam.getName() + "'s tactic?");
        System.out.println("1. 4-4-2");
        System.out.println("2. 4-3-3");
        System.out.println("3. 3-5-2");
        System.out.println("0. No change");
        System.out.print("Enter your choice: ");

        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                homeTeam.changeTactic("4-4-2");
                System.out.println(homeTeam.getName() + " tactic changed to 4-4-2.");
                break;
            case 2:
                homeTeam.changeTactic("4-3-3");
                System.out.println(homeTeam.getName() + " tactic changed to 4-3-3.");
                break;
            case 3:
                homeTeam.changeTactic("3-5-2");
                System.out.println(homeTeam.getName() + " tactic changed to 3-5-2.");
                break;
            case 0:
                System.out.println("No tactic change made.");
                break;
            default:
                System.out.println("Invalid choice. No tactic change made.");
        }

        System.out.print("Second half starting");
        for (int i = 0; i < 3; i++) {
            System.out.print(".");
            Thread.sleep(600);
        }
        
        match.playPeriod(); //Second Half

        Thread.sleep(1000);

        System.out.println("\n--- FULL TIME ---");
        System.out.println(homeTeam.getName() + " " + match.getTeam1Score()
                + " - " + match.getTeam2Score() + " " + awayTeam.getName());
        System.out.println("Result: " + match.getResult());

        scanner.close();
    }
}
