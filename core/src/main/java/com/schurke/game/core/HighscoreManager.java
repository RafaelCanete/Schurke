package com.schurke.game.core;

import java.io.*;

public class HighscoreManager {
    private static final String HIGHSCORE_FILE = System.getProperty("user.home") + "/schurke_highscore.txt";

    private static int highscore = 0;
    private static int highscoreLevel = 0;

    public static int getHighscore() {
        return highscore;
    }

    public static int getHighscoreLevel() {
        return highscoreLevel;
    }

    public static void load() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGHSCORE_FILE))) {
            String line = reader.readLine();
            if (line != null && line.contains(",")) {
                String[] parts = line.trim().split(",");
                highscore = Integer.parseInt(parts[0]);
                highscoreLevel = Integer.parseInt(parts[1]);
            } else if (line != null) {
                highscore = Integer.parseInt(line.trim());
                highscoreLevel = 0;
            }
        } catch (Exception e) {
            highscore = 0;
            highscoreLevel = 0;
        }
    }

    public static void save(int score, int level) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGHSCORE_FILE))) {
            writer.write(score + "," + level);
        } catch (Exception e) {
            // Ignore
        }
    }
}
