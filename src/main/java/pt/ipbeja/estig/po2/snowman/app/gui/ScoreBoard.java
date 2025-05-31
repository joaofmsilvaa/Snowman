package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import pt.ipbeja.estig.po2.snowman.app.model.Score;
import pt.ipbeja.estig.po2.snowman.app.model.ScoreLoader;
import pt.ipbeja.estig.po2.snowman.app.model.interfaces.ScoreListener;

import java.util.Collections;
import java.util.List;

public class ScoreBoard extends VBox implements ScoreListener {
    private final Label currentScoreLabel;
    private final Label highScoresLabel;
    private final List<Score> allScores;

    public ScoreBoard() {
        setPadding(new Insets(15));
        setSpacing(15);

        currentScoreLabel = new Label();
        highScoresLabel = new Label();

        currentScoreLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        highScoresLabel.setStyle("-fx-font-size: 13px;");

        this.getChildren().addAll(currentScoreLabel, highScoresLabel);
        this.allScores = ScoreLoader.loadAllScores(); // ← carrega scores guardados
    }

    private void updatePanel(Score currentScore) {
        // Atualizar pontuação atual com destaque
        currentScoreLabel.setText(String.format("""
                Pontuação Atual 🎯
                ----------------------
                Jogador: %s
                Nível: %s
                Movimentos: %d
                """,
                currentScore.getPlayerName(),
                currentScore.getLevelName(),
                currentScore.getMoves())
        );

        // Atualizar top 3
        allScores.add(currentScore);
        Collections.sort(allScores);

        int topLimit = Math.min(3, allScores.size());
        StringBuilder builder = new StringBuilder("🏆 TOP 3 MELHORES PONTUAÇÕES\n----------------------------\n");
        for (int i = 0; i < topLimit; i++) {
            Score s = allScores.get(i);
            s.setTop(s.equals(currentScore));
            builder.append(String.format("%d. %s\n", i + 1, s));
        }

        highScoresLabel.setText(builder.toString());
    }

    @Override
    public void onScore(Score score) {
        updatePanel(score);
    }
}
