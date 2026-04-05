package sportsmanager;

public class Main {
     public static void main(String[] args) {

        GameStatus gameStatus = new GameStatus();
        gameStatus.setUsername("Cem");
        gameStatus.setCurrentWeek(1);

        ISport football = new Football();
        gameStatus.setCurrentSport(football);

        System.out.println("=== GAME STATUS ===");
        System.out.println("User: " + gameStatus.getUsername());
        System.out.println("Week: " + gameStatus.getCurrentWeek());

        System.out.println("\n=== FOOTBALL RULES ===");
        football.displayRules();

        System.out.println("\n=== FOOTBALL DETAILS ===");
        System.out.println("Players On Field: " + football.getPlayerCount());
        System.out.println("Max Substitutions: " + football.getSubstituteChangeLimit());

        System.out.println("\n=== POINT SYSTEM ===");
        System.out.println("Win Points: " + football.getWinPoints());
        System.out.println("Draw Points: " + football.getDrawPoints());
        System.out.println("Loss Points: " + football.getLossPoints());

    }
}
