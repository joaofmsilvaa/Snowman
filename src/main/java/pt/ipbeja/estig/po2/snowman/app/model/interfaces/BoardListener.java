package pt.ipbeja.estig.po2.snowman.app.model.interfaces;

import pt.ipbeja.estig.po2.snowman.app.model.PositionContent;

public interface BoardListener {

    void onTerrainChanged(int row, int col, PositionContent newContent);
    void updateBoard();
}
