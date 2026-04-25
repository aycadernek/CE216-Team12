package sportsmanager;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;

public class SaveManager {

    private static final String SAVE_FILE = "game_status.json";
    private ObjectMapper mapper;

    public SaveManager() {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setVisibility(com.fasterxml.jackson.annotation.PropertyAccessor.ALL, com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(com.fasterxml.jackson.annotation.PropertyAccessor.FIELD, com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void saveGame(GameStatus game_status) {
        try {
            mapper.writeValue(new File(SAVE_FILE), game_status);
            System.out.println("Game saved successfully to: " + SAVE_FILE);
        } catch (IOException e) {
            System.out.println("Error occured while saving the game: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public GameStatus loadGame() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            System.out.println("No file found for loading.");
            return null;
        }
        try {
            GameStatus game_status = mapper.readValue(file, GameStatus.class);
            System.out.println("Game load success.");
            return game_status;
        } catch (IOException e) {
            System.out.println("Error occured while loading the game: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}