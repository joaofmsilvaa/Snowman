package pt.ipbeja.estig.po2.snowman.app.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ScoreManager {
    private final List<Score> scores = new ArrayList<>();

    public void addScore(Score score) {
        scores.add(score);
        scores.sort(Comparator.naturalOrder()); // menor = melhor
        updateTop();
    }

    private void updateTop() {
        // Clean flags
        for (Score s : scores) {
            s.setTop(false);
        }
        // store top-3
        for (int i = 0; i < Math.min(3, scores.size()); i++) {
            scores.get(i).setTop(true);
        }
    }

    public List<Score> getTopScores() {
        return scores.stream()
                .limit(3)
                .toList();
    }
}
