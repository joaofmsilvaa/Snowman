package pt.ipbeja.estig.po2.snowman.app.model;

/**
 * @author João Silva
 * @author Paulo Neves
 */
public class Score implements Comparable<Score> {
    private final String playerName;  // máx 3 letras
    private final String levelName;
    private final int moves;
    private boolean isTop;

    public Score(String playerName, String levelName, int moves) {
        this.playerName = playerName;
        this.levelName = levelName;
        this.moves = moves;
        this.isTop = false;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getLevelName() {
        return levelName;
    }

    public int getMoves() {
        return moves;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    @Override
    public int compareTo(Score other) {
        return Integer.compare(this.moves, other.moves); // menor é melhor
    }

    @Override
    public String toString() {
        return String.format("%s - %s - %d %s", playerName, levelName, moves, isTop ? "TOP" : "");
    }
}
