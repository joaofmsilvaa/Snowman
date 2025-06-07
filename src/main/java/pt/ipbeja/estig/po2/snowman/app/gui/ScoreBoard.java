package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import pt.ipbeja.estig.po2.snowman.app.model.Score;
import pt.ipbeja.estig.po2.snowman.app.model.ScoreLoader;
import pt.ipbeja.estig.po2.snowman.app.model.interfaces.ScoreListener;

import java.util.Collections;
import java.util.List;

/**
 * ScoreBoard is a VBox that displays the current game’s score and the top-3 high scores.
 * It implements ScoreListener to receive notifications when a new Score is earned.
 * @author João Silva
 * @author Paulo Neves
 */
public class ScoreBoard extends VBox implements ScoreListener {
    private final Label currentScoreLabel;
    private final Label highScoresLabel;
    private final List<Score> allScores;

    public ScoreBoard(String mapName) {
        setPadding(new Insets(15));
        setSpacing(15);

        currentScoreLabel = new Label();
        highScoresLabel = new Label();

        currentScoreLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        highScoresLabel.setStyle("-fx-font-size: 13px;");

        // Add both labels to the VBox layout
        this.getChildren().addAll(currentScoreLabel, highScoresLabel);
        this.allScores = ScoreLoader.loadScoresForMap(mapName);
    }

    /**
     * Updates the ScoreBoard panel with the given currentScore:
     * - Displays the current player's name, level, and move count.
     * - Inserts the new score into the allScores list, sorts it,
     * and displays the top 3 scores, marking the current score if it is in the top 3.
     *
     * @param currentScore the Score object representing the latest game result
     */
    private void updatePanel(Score currentScore) {
        // Update the current score label with player, level, and moves
        currentScoreLabel.setText(String.format("""
                        Pontuação Atual: 
                        ----------------------
                        Jogador: %s
                        Nível: %s
                        Movimentos: %d
                        """,

                currentScore.getPlayerName(),
                currentScore.getLevelName(),
                currentScore.getMoves())
        );

        // Add the new score to the list and sort in natural order
        allScores.add(currentScore);
        Collections.sort(allScores);

        // Determine how many scores to show
        int topLimit = Math.min(3, allScores.size());
        StringBuilder builder = new StringBuilder(" TOP 3 MELHORES PONTUAÇÕES\n----------------------------\n");
        for (int i = 0; i < topLimit; i++) {
            Score s = allScores.get(i);
            // Mark the current score if it matches
            s.setTop(s.equals(currentScore));
            builder.append(String.format("%d. %s\n", i + 1, s));
        }

        // Set the high scores label text
        highScoresLabel.setText(builder.toString());
    }

    /**
     * Called when a new Score has been generated. Triggers the panel update.
     *
     * @param score the newly earned Score
     */
    @Override
    public void onScore(Score score) {
        updatePanel(score);
    }
}
