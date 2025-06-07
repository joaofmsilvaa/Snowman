package pt.ipbeja.estig.po2.snowman.app.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Game keeps track of the current player's name, the map name,
 * and the history of moves performed (as formatted strings).
 *
 * @author João Silva
 * @author Paulo Neves
 */
public class Game {
    private String playerName;
    private final List<String> moveHistory = new ArrayList<>();
    private String mapName;

    /// Constructs a new Game with the given player name and map name.
    public Game(String playerName, String mapName) {
        this.playerName = playerName;
        this.mapName = mapName;
    }

    /**
     * Called whenever the monster moves from one position to another.
     * Formats the move details and adds it to moveHistory.
     *
     * @param from the previous Position of the monster
     * @param to   the new Position of the monster
     */
    public void onMove(Position from, Position to) {
        moveHistory.add(
                to.formatDetails(
                        from.getRow() + 1,
                        from.getCol(),
                        to.getRow() + 1,
                        to.getCol()
                )
        );
    }

    /// Returns the total number of moves made so far,
    /// which corresponds to the size of moveHistory.
    public int getMoveCount() {
        return moveHistory.size();
    }

    /// Returns the name of the current player.
    public String getPlayerName() {
        return playerName;
    }

    /// converts the move history list into an array of strings.
    public String[] getMoveHistoryArray() {
        return moveHistory.toArray(new String[0]);
    }

    /// Returns the name of the current map or level.
    public String getMapName() {
        return mapName;
    }

    /// Sets or updates the player's name.
    public void setPlayerName(String name) {
        this.playerName = name;
    }
}

