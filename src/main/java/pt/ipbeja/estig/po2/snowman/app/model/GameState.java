package pt.ipbeja.estig.po2.snowman.app.model;

import java.util.List;

/**
 * Represents a snapshot of the game’s state at a given point in time.
 * <p>
 * This record encapsulates:
 * 1. The Monster instance and its position.
 * 2. The list of Snowball objects currently on the board.
 * 3. The 2D boardContent representing terrain and snowman placement.
 * <p>
 * Used for undo/redo functionality and history management.
 *
 * @param monster      the Monster and its current position
 * @param snowballs    the list of Snowball objects on the board
 * @param boardContent a list of rows, each being a list of PositionContent enums
 *                     /**
 * @author João Silva
 * @author Paulo Neves
 */
public record GameState(
        Monster monster,
        List<Snowball> snowballs,
        List<List<PositionContent>> boardContent) {
}

