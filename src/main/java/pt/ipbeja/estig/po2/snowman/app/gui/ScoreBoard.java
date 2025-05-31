package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import pt.ipbeja.estig.po2.snowman.app.model.Score;
import pt.ipbeja.estig.po2.snowman.app.model.ScoreLoader;
import pt.ipbeja.estig.po2.snowman.app.model.interfaces.ScoreListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreBoard extends VBox implements ScoreListener {
    private final Label currentScoreLabel;
    private final Label highScoresLabel;
    private final List<Score> allScores;

    public ScoreBoard() {
        setPadding(new Insets(10));
        setSpacing(10);
        currentScoreLabel = new Label("Pontuação atual: ");
        highScoresLabel = new Label("Melhores Pontuações:\n");

        this.getChildren().addAll(currentScoreLabel, highScoresLabel);
        this.allScores = ScoreLoader.loadAllScores(); // ← carrega scores guardados
    }

    private void updatePanel(Score currentScore) {
        currentScoreLabel.setText("Pontuação: " + currentScore.getMoves() +
                "\nJogador: " + currentScore.getPlayerName() +
                "\nNível: " + currentScore.getLevelName());

        allScores.add(currentScore);
        Collections.sort(allScores);

        int topLimit = Math.min(3, allScores.size());

        StringBuilder builder = new StringBuilder("TOP 3:\n");
        for (int i = 0; i < topLimit; i++) {
            Score s = allScores.get(i);
            s.setTop(s.equals(currentScore));
            builder.append(i + 1).append(". ").append(s).append("\n");
        }

        highScoresLabel.setText(builder.toString());
    }

    @Override
    public void onScore(Score score) {
        updatePanel(score);
    }
}