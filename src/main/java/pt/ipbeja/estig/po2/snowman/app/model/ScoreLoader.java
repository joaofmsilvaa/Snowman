package pt.ipbeja.estig.po2.snowman.app.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ScoreLoader {

    private static final String DIRECTORY = "."; // ou onde os ficheiros são guardados

    public static List<Score> loadAllScores() {
        File folder = new File(DIRECTORY);
        File[] files = folder.listFiles((dir, name) -> name.startsWith("Snowman") && name.endsWith(".txt"));

        List<Score> scores = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                Score score = extractScoreFromFile(file);
                if (score != null) {
                    scores.add(score);
                }
            }
        }

        return scores;
    }

    private static Score extractScoreFromFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String mapLine = reader.readLine(); // Mapa: [nome]
            String mapName = mapLine.split(":")[1].trim();

            String line;
            int moves = 0;
            String playerName = "???";

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Total de jogadas:")) {
                    moves = Integer.parseInt(line.split(":")[1].trim());
                }
                if (line.startsWith("Jogador:")) {
                    playerName = line.split(":")[1].trim();
                }
            }

            return new Score(playerName, mapName, moves);

        } catch (IOException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.err.println("Erro ao ler pontuação de " + file.getName() + ": " + e.getMessage());
            return null;
        }
    }
}
