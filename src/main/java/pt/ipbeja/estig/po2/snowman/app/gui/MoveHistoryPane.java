package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.scene.control.TextArea;
import pt.ipbeja.estig.po2.snowman.app.model.Position;
import pt.ipbeja.estig.po2.snowman.app.model.interfaces.MoveListener;

/**
 * MoveHistoryPane is a non-editable TextArea that implements MoveListener.
 * It appends a formatted record each time the monster moves.
 * @author João Silva
 * @author Paulo Neves
 */
public class MoveHistoryPane extends TextArea implements MoveListener {

    /**
     * Constructs the pane: sets it to be non-editable and
     * limits the preferred row count to 5.
     */
    public MoveHistoryPane() {
        this.setEditable(false);
        this.setPrefRowCount(5);
    }

    /**
     * Called whenever the monster moves from one Position to another.
     * Formats the move details (using 1-based row indices and column letters)
     * and appends the resulting string to the TextArea.
     *
     * @param from previous Position of the monster
     * @param to   new Position of the monster
     */
    @Override
    public void onMove(Position from, Position to) {
        // Convert row indices to 1-based for display
        int adjustedFromRow = from.getRow() + 1, adjustedToRow = to.getRow() + 1;
        String log = from.formatDetails(adjustedFromRow, from.getCol(),
                adjustedToRow, to.getCol());
        // Append this move to the existing text
        this.setText(this.getText() + log);
    }
}
