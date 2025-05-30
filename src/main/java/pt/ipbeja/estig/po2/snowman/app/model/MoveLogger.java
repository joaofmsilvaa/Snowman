package pt.ipbeja.estig.po2.snowman.app.model;

import pt.ipbeja.estig.po2.snowman.app.model.interfaces.MoveListener;

import java.util.ArrayList;
import java.util.List;

public class MoveLogger implements MoveListener {

    private final List<String> moveHistory = new ArrayList<>();

    @Override
    public void onMove(Position from, Position to) {
        moveHistory.add(to.formatDetails(from.getRow(), from.getCol(), to.getRow(), to.getCol()));
    }

    public String[] getMoveHistoryArray() {
        return moveHistory.toArray(new String[0]);
    }
}