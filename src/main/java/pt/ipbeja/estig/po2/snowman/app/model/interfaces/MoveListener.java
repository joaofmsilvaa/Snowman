package pt.ipbeja.estig.po2.snowman.app.model.interfaces;

import pt.ipbeja.estig.po2.snowman.app.model.Position;

public interface MoveListener {
    void onMove(Position from, Position to);
}
