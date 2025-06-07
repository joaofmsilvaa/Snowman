package pt.ipbeja.estig.po2.snowman.app.model.interfaces;

import pt.ipbeja.estig.po2.snowman.app.model.PositionContent;

/**
 * @author João Silva
 * @author Paulo Neves
 */
public interface BoardListener {

    void onTerrainChanged(int row, int col, PositionContent newContent);

    void updateBoard();
}
