import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HighScoreManager {
    private Map<String, Integer> highScores;
    private static final String HIGH_SCORES_FILE = "highscores.txt";

    public HighScoreManager() {
        highScores = loadHighScoresFromFile();
    }

    public void addHighScore(String playerName, int score) {
        highScores.put(playerName, score);
        saveHighScoresToFile();
    }

    public void deleteHighScore(String playerName) {
        highScores.remove(playerName);
        saveHighScoresToFile();
    }

    public void displayHighScores() {
        System.out.println("High Scores:");
        for (String playerName : highScores.keySet()) {
            System.out.println(playerName + ": " + highScores.get(playerName));
        }
    }

    private Map<String, Integer> loadHighScoresFromFile() {
        Map<String, Integer> loadedHighScores = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGH_SCORES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String playerName = parts[0].trim();
                    int score = Integer.parseInt(parts[1].trim());
                    loadedHighScores.put(playerName, score);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loadedHighScores;
    }

    private void saveHighScoresToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGH_SCORES_FILE))) {
            for (Map.Entry<String, Integer> entry : highScores.entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}