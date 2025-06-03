package pt.ipbeja.estig.po2.snowman.app.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ScoreLoader is responsible for loading all saved Score files
 * from the specified directory. Each file that begins with "Snowman"
 * and ends with ".txt" is parsed to extract the player's name, map name,
 * and move count, producing a List<Score>.
 */
public class ScoreLoader {

    private static final String DIRECTORY = ".";

    /**
     * Scans the DIRECTORY for files whose names start with "Snowman" and end with ".txt".
     * For each matching file, calls extractScoreFromFile to parse its contents.
     *
     * @return a List of Score objects extracted from all matching files;
     *         returns an empty list if no files are found or if all extractions fail.
     */
    public static List<Score> loadScoresForMap(String mapNameToFilter) {
        File folder = new File(DIRECTORY);
        // Filter files: name starts with "Snowman" and ends with ".txt"
        File[] files = folder.listFiles((dir, name) -> name.startsWith("Snowman") && name.endsWith(".txt"));

        List<Score> scores = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                //each file into a Score object
                Score score = extractScoreFromFile(file);
                if (score != null && score.getLevelName().equals(mapNameToFilter)) {
                    scores.add(score);
                }
            }
        }

        return scores;
    }


    /**
     * Opens the given file and reads its contents to extract:
     *  - map name from the first line ("Mapa: [nome]")
     *  - total moves from a line starting with "Total de jogadas:"
     *  - player name from a line starting with "Jogador:"
     *
     * If parsing succeeds, returns a new Score(playerName, mapName, moves).
     * In case of any I/O or format error, logs an error and returns null.
     *
     * @param file the File object pointing to a "Snowman*.txt" file
     * @return a Score object with the extracted data, or null if an error occurred
     */
    private static Score extractScoreFromFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Read the first line
            String mapLine = reader.readLine(); // Mapa: [nome]
            String mapName = mapLine.split(":")[1].trim();

            String line;
            int moves = 0;
            String playerName = "???";

            // Read remaining lines to find "Total de jogadas:" and "Jogador:"
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Total de jogadas:")) {
                    // Extract move count after colon
                    moves = Integer.parseInt(line.split(":")[1].trim());
                }
                if (line.startsWith("Jogador:")) {
                    // Extract player name after colon
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
