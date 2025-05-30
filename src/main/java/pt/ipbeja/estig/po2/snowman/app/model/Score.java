package pt.ipbeja.estig.po2.snowman.app.model;

import java.util.ArrayList;
import java.util.List;

public class Score implements Comparable<Score>{
    private final String name;
    private final int points;
    private final List<Score> scores = new ArrayList<>();


    public Score(String name, int points){
        this.name = name;
        this.points = points;
    }

    public String getName(){
        return name;
    }

    public int getPoints() {
        return points;
    }
    @Override
    public String toString(){
        return name + " - " + points + " Movimentos";
    }

    @Override
    public int compareTo(Score o) {
        return this.points - o.points;
    }

}
