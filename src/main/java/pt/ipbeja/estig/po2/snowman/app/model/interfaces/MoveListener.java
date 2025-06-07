package pt.ipbeja.estig.po2.snowman.app.model.interfaces;

import pt.ipbeja.estig.po2.snowman.app.model.Position;

/**
 * @author João Silva
 * @author Paulo Neves
 */
public interface MoveListener {

    /// Called whenever the monster move is done
    void onMove(Position from, Position to);
}
