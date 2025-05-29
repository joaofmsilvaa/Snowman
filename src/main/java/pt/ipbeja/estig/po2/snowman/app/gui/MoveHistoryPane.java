package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.scene.control.TextArea;
import pt.ipbeja.estig.po2.snowman.app.model.Position;
import pt.ipbeja.estig.po2.snowman.app.model.interfaces.MoveListener;

public class MoveHistoryPane extends TextArea implements MoveListener {

    public MoveHistoryPane() {
        this.setEditable(false);
        this.setPrefRowCount(5);
    }

    @Override
    public void onMove(Position from, Position to) {
        int adjustedFromRow = from.getRow() +1, adjustedToRow = to.getRow() + 1;
        String log = from.formatDetails(adjustedFromRow, from.getCol(), adjustedToRow, to.getCol());
        this.setText(this.getText() + log);
    }
}
