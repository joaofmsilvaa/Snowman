package pt.ipbeja.estig.po2.snowman.app.model;

public class Score implements Comparable<Score> {
    private final String name;
    private final int points;
    private final String levelName;
    private boolean top;          // nova flag

    public Score(String name, int points, String levelName) {
        this.name = name;
        this.points = points;
        this.levelName = levelName;
        this.top = false;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public String getLevelName() {
        return levelName;
    }

    public boolean isTop() {
        return top;
    }

    public void setTop(boolean top) {
        this.top = top;
    }

    @Override
    public int compareTo(Score other) {
        return Integer.compare(this.points, other.points);
    }

    public String getDisplayString() {
        String s = name + " - " + points + " moves [" + levelName + "]";
        if (top) {
            s += " TOP";
        }
        return s;
    }
}
