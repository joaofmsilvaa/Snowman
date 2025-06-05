package pt.ipbeja.estig.po2.snowman.app.model;

import pt.ipbeja.estig.po2.snowman.app.model.interfaces.BoardListener;
import pt.ipbeja.estig.po2.snowman.app.model.interfaces.MoveListener;
import pt.ipbeja.estig.po2.snowman.app.model.interfaces.ScoreListener;
import pt.ipbeja.estig.po2.snowman.app.model.interfaces.View;

import java.util.ArrayList;
import java.util.List;

/**
 * BoardModel manages all game logic with improved undo/redo functionality
 */
/**
 * BoardModel serves as the central game logic controller for the Snowman puzzle game.
 *
 * This class manages:
 * - Game board state and content (snow, blocks, empty spaces)
 * - Monster position and movement mechanics
 * - Snowball positions, stacking, and physics
 * - Undo/Redo functionality with complete state preservation
 * - Game progression tracking (moves, scores, completion)
 * - Event notification system for UI updates
 *
 * The class implements a robust undo/redo system that captures complete game states
 * including monster position, all snowball positions and types, and board content.
 * This allows players to experiment with different strategies without penalty.
 *
 * Key Design Patterns:
 * - Observer Pattern: Notifies multiple listeners of game state changes
 * - Command Pattern: Encapsulates game actions for undo/redo functionality
 * - State Pattern: Manages different game states and transitions
 *
 * @author João Silva
 * @author Paulo Neves
 */
public class BoardModel {
    // === CORE GAME STATE ===
    /** 2D matrix representing the game board content (snow, blocks, empty spaces) */
    private List<List<PositionContent>> boardContent;

    /** The player-controlled monster that pushes snowballs */
    private Monster monster;

    /** Collection of all snowballs currently on the board with their positions and types */
    private List<Snowball> snowballs;

    // === EVENT NOTIFICATION SYSTEM ===
    /** Primary view interface for rendering game state changes */
    private View view;

    /** Listener for monster movement events */
    private MoveListener moveListener;

    /** Listener for board terrain changes */
    private BoardListener boardListener;

    /** Listener for score-related events */
    private ScoreListener scoreListener;

    // === GAME MANAGEMENT ===
    /** Game instance containing player info, move count, and session data */
    private Game game;

    /** Name of the current map file being played */
    private String mapFileName;

    // === UNDO/REDO SYSTEM ===
    /**
     * Current position in the history timeline.
     * -1 means no history, 0+ indicates current state index
     */
    private int currentStateIndex = -1;

    /**
     * Complete history of game states for undo/redo functionality.
     * Each GameState contains a full snapshot of monster, snowballs, and board
     */
    private List<GameState> history = new ArrayList<>();

    /**
     * Maximum number of states to keep in memory to prevent excessive RAM usage.
     * Older states are automatically purged when this limit is exceeded.
     */
    private static final int MAX_HISTORY_SIZE = 50;

    /**
     * Default constructor that initializes a standard 5x5 game board.
     *
     * Creates the initial game setup:
     * - 5x5 grid with snow on top row, empty spaces elsewhere
     * - Monster positioned at (2,0) - middle-left of the board
     * - Three small snowballs placed horizontally in row 2
     *
     * This constructor automatically saves the initial state for undo/redo functionality.
     */
    public BoardModel() {
        boardContent = new ArrayList<>();
        snowballs = new ArrayList<>();
        startGame();
        // Save initial state AFTER setting up the game to ensure first undo works correctly
        saveInitialState();
    }

    /**
     * Advanced constructor for creating a BoardModel from existing game data.
     * Used for loading saved games or creating custom board configurations.
     *
     * This constructor performs deep copying to ensure data integrity and prevent
     * unintended modifications to the original data structures.
     *
     * @param content   2D matrix of PositionContent representing the board layout
     * @param monster   Monster instance with its current position
     * @param snowballs List of Snowball instances with their positions and types
     */
    public BoardModel(List<List<PositionContent>> content, Monster monster, List<Snowball> snowballs) {
        this.monster = monster;
        this.snowballs = new ArrayList<>(snowballs); // Defensive copy to prevent external modifications
        this.boardContent = new ArrayList<>();

        // Create deep copy of board content to ensure complete independence
        for (List<PositionContent> row : content) {
            this.boardContent.add(new ArrayList<>(row));
        }

        saveInitialState();
    }

    // === DEPENDENCY INJECTION METHODS ===
    // These setters implement the Dependency Injection pattern, allowing the game
    // to be configured with different implementations of interfaces

    /**
     * Registers the primary view component responsible for rendering the game.
     * The view will receive notifications about all visual changes requiring updates.
     *
     * @param view Implementation of the View interface for game rendering
     */
    public void setView(View view) {
        this.view = view;
    }

    /**
     * Registers a listener for monster movement events.
     * This enables tracking of monster positions for logging, analytics, or additional UI updates.
     *
     * @param moveListener Implementation that will receive movement notifications
     */
    public void setMoveListener(MoveListener moveListener) {
        this.moveListener = moveListener;
    }

    /**
     * Registers a listener for board terrain changes.
     * Useful for tracking environmental modifications like snow melting or terrain updates.
     *
     * @param boardListener Implementation that will receive board change notifications
     */
    public void setBoardListener(BoardListener boardListener) {
        this.boardListener = boardListener;
    }

    /**
     * Sets the filename of the current map being played.
     * Used for save/load functionality and score tracking.
     *
     * @param mapFileName Path or name of the map file
     */
    public void setMapFileName(String mapFileName) {
        this.mapFileName = mapFileName;
    }

    /**
     * Registers a listener for score-related events.
     * Notified when games are completed and scores need to be recorded.
     *
     * @param listener Implementation that will handle score events
     */
    public void setScoreListener(ScoreListener listener) {
        this.scoreListener = listener;
    }

    /**
     * Sets the current player's name.
     * Safely handles null game instances to prevent crashes during initialization.
     *
     * @param playerName Name of the current player
     */
    public void setPlayerName(String playerName) {
        if (game != null) {
            game.setPlayerName(playerName);
        }
    }

    /**
     * Initializes the game board to its default starting configuration.
     *
     * This method creates a standard 5x5 Snowman puzzle setup:
     * - Top row (row 0): Filled with SNOW for snowball creation
     * - Remaining rows: Empty NO_SNOW spaces for movement
     * - Monster: Positioned at (2,0) - middle-left edge
     * - Snowballs: Three SMALL snowballs placed at (2,1), (2,2), (2,3)
     *
     * The setup is designed to provide an optimal starting puzzle where players
     * can learn the basic mechanics of pushing and stacking snowballs.
     *
     * Also resets the undo/redo history to provide a clean starting state.
     */
    public void startGame() {
        // Clear any existing game state to ensure clean initialization
        boardContent.clear();
        snowballs.clear();

        // Position monster at strategic starting location
        monster = new Monster(2, 0);

        // Create 5x5 board with snow on top row for snowball material
        for (int i = 0; i < 5; i++) {
            List<PositionContent> row = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                if (i == 0) {
                    row.add(PositionContent.SNOW);  // Snow source row
                } else {
                    row.add(PositionContent.NO_SNOW);  // Movement space
                }
            }
            boardContent.add(row);
        }

        // Place initial snowballs in a line for easy access
        // This configuration allows players to practice basic pushing mechanics
        snowballs.add(new Snowball(2, 1, SnowballType.SMALL));
        snowballs.add(new Snowball(2, 2, SnowballType.SMALL));
        snowballs.add(new Snowball(2, 3, SnowballType.SMALL));

        // Reset undo/redo system for new game
        history.clear();
        currentStateIndex = -1;
    }

    /**
     * Saves the initial game state immediately after board setup.
     *
     * This method ensures that players can undo back to the very beginning
     * of the game, which is crucial for puzzle games where experimentation
     * is encouraged.
     *
     * Called automatically after startGame() completes to capture the
     * pristine starting configuration.
     */
    private void saveInitialState() {
        history.clear();
        currentStateIndex = -1;
        saveState();
    }

    /**
     * Associates a Game instance with this BoardModel.
     *
     * The Game object tracks session-specific data like player name,
     * move count, and game progression. This method ensures proper
     * initialization of player data if it exists.
     *
     * @param game Game instance containing session data
     */
    public void setGame(Game game) {
        this.game = game;
        if (game != null && game.getPlayerName() != null) {
            this.game.setPlayerName(game.getPlayerName());
        }
    }

    // === GETTER METHODS ===
    // These methods provide read-only access to game state information

    /** @return The filename of the currently loaded map */
    public String getMapFileName() {
        return this.mapFileName;
    }

    /** @return Number of rows in the game board */
    public int getRowCount() {
        return boardContent.size();
    }

    /** @return Number of columns in the game board */
    public int getColCount() {
        return boardContent.isEmpty() ? 0 : boardContent.get(0).size();
    }

    /**
     * Retrieves the content type of a specific board cell.
     *
     * Includes bounds checking to prevent index out of bounds exceptions.
     * Returns BLOCK for out-of-bounds coordinates to simulate walls.
     *
     * @param row Row coordinate (0-based)
     * @param col Column coordinate (0-based)
     * @return PositionContent at the specified location, or BLOCK if out of bounds
     */
    public PositionContent getPositionContent(int row, int col) {
        if (row < 0 || row >= getRowCount() || col < 0 || col >= getColCount()) {
            return PositionContent.BLOCK; // Treat out-of-bounds as impassable walls
        }
        return boardContent.get(row).get(col);
    }

    /** @return Current Monster instance */
    public Monster getMonster() {
        return monster;
    }

    /** @return Current player name, or empty string if no game is set */
    public String getPlayerName() {
        return game != null ? game.getPlayerName() : "";
    }

    /** @return Current move count, or 0 if no game is set */
    public int getMoveCount() {
        return game != null ? game.getMoveCount() : 0;
    }

    /** @return Current Game instance */
    public Game getGame() {
        return this.game;
    }

    /**
     * Sets the name of the current map for scoring and save purposes.
     * Safely handles null game instances.
     *
     * @param mapName Name identifier for the current map
     */
    public void setMapName(String mapName) {
        if (game != null) {
            game.setMapName(mapName);
        }
    }

    // === GAME LOGIC AND COLLISION DETECTION ===

    /**
     * Validates whether a given position is accessible for movement.
     *
     * A position is considered valid if:
     * 1. It's within the board boundaries
     * 2. It doesn't contain a BLOCK (impassable terrain)
     *
     * This method is critical for preventing illegal moves and ensuring
     * game physics consistency.
     *
     * @param newRow Target row coordinate
     * @param newCol Target column coordinate
     * @return true if the position allows movement, false otherwise
     */
    public boolean validPosition(int newRow, int newCol) {
        // First check boundaries to prevent array access errors
        if (newRow < 0 || newRow >= getRowCount() || newCol < 0 || newCol >= getColCount()) {
            return false;
        }
        // Then check if the position contains impassable terrain
        return getPositionContent(newRow, newCol) != PositionContent.BLOCK;
    }

    /**
     * Determines if a snowball can be unstacked at the specified position.
     *
     * For unstacking to be possible:
     * 1. The target position must be valid for movement
     * 2. No other snowball can occupy that position
     *
     * This prevents snowballs from being unstacked into occupied spaces
     * or illegal terrain.
     *
     * @param newRow Target row for the unstacked snowball
     * @param newCol Target column for the unstacked snowball
     * @return true if unstacking is possible, false otherwise
     */
    public boolean canUnstack(int newRow, int newCol) {
        return validPosition(newRow, newCol) && getSnowballInPosition(newRow, newCol) == null;
    }

    /**
     * Searches for a snowball at the specified coordinates.
     *
     * Uses linear search through the snowballs collection. While O(n) complexity,
     * this is acceptable given the small number of snowballs typically in play.
     *
     * @param row Target row coordinate
     * @param col Target column coordinate
     * @return Snowball instance at that position, or null if none exists
     */
    public Snowball getSnowballInPosition(int row, int col) {
        for (Snowball snowball : snowballs) {
            if (snowball.getRow() == row && snowball.getCol() == col) {
                return snowball;
            }
        }
        return null;
    }

    /**
     * Detects if there's a snowball directly adjacent to the monster in the given direction.
     *
     * This is essential for push mechanics - the monster can only interact with
     * snowballs that are immediately in front of it.
     *
     * Uses switch expression for clean directional logic without complex if-else chains.
     *
     + @param direction Direction the monster is facing/moving
     * @return Snowball directly in front of monster, or null if none exists
     */
    public Snowball snowballInFrontOfMonster(Direction direction) {
        int row = monster.getRow();
        int col = monster.getCol();

        // Calculate adjacent position based on direction using modern switch syntax
        return switch (direction) {
            case UP -> getSnowballInPosition(row - 1, col);
            case DOWN -> getSnowballInPosition(row + 1, col);
            case LEFT -> getSnowballInPosition(row, col - 1);
            case RIGHT -> getSnowballInPosition(row, col + 1);
        };
    }

    /**
     * Executes monster movement with comprehensive state management and event notification.
     *
     * This is one of the most complex methods in the class, handling:
     * 1. Pre-movement state capture for undo functionality
     * 2. Snowball collision detection and pushing mechanics
     * 3. Movement validation and execution
     * 4. Post-movement state saving and event notifications
     * 5. UI synchronization through multiple listener interfaces
     *
     * The method follows a careful sequence to ensure data consistency:
     * - Capture old positions BEFORE any changes
     * - Attempt movement (which may fail)
     * - Only save state and notify listeners if movement succeeds
     *
     * @param direction Direction for monster movement
     * @return true if movement was successful, false if blocked or invalid
     */
    public boolean moveMonster(Direction direction) {
        // === PHASE 1: PRE-MOVEMENT STATE CAPTURE ===
        // Store original positions before any modifications for rollback capability
        Position oldPosition = new Position(monster.getRow(), monster.getCol());
        Snowball snowball = snowballInFrontOfMonster(direction);
        Position oldSnowballPosition = null;

        // If there's a snowball to push, record its current position
        if (snowball != null) {
            oldSnowballPosition = new Position(snowball.getRow(), snowball.getCol());
        }

        // === PHASE 2: MOVEMENT ATTEMPT ===
        // Delegate actual movement logic to Monster class, which handles:
        // - Boundary checking, collision detection, snowball pushing
        boolean moved = monster.move(direction, this);

        // === PHASE 3: POST-MOVEMENT PROCESSING ===
        // Only proceed if movement was successful
        if (moved) {
            // Save current state to undo history AFTER successful move
            saveState();

            Position currentPosition = new Position(monster.getRow(), monster.getCol());

            // === PHASE 4: UI NOTIFICATION SYSTEM ===
            // Notify view of visual changes requiring screen updates
            if (view != null) {
                // Clear monster sprite from old position
                view.onMonsterCleared(oldPosition);
                // Draw monster sprite at new position
                view.onMonsterMoved(currentPosition);

                // If a snowball was pushed, update its visual representation
                if (snowball != null) {
                    view.onSnowballMoved(snowball, oldSnowballPosition);
                }
            }

            // === PHASE 5: EVENT LISTENER NOTIFICATIONS ===
            // Notify movement listener for game logic tracking
            if (moveListener != null) {
                moveListener.onMove(oldPosition, currentPosition);
            }

            // Update game session data (move counting, logging)
            if (game != null) {
                game.onMove(oldPosition, currentPosition);
            }
        }

        return moved;
    }

    /**
     * Delegates snowball movement to the Snowball class.
     *
     * This method maintains consistent interface while allowing
     * Snowball objects to handle their own movement logic.
     *
     * @param direction Direction to move the snowball
     * @param snowball  Snowball instance to move
     * @return true if movement succeeded, false otherwise
     */
    public boolean moveSnowball(Direction direction, Snowball snowball) {
        return snowball.move(direction, this);
    }

    public boolean tryStackSnowballs(Snowball top, Snowball bottom) {
        SnowballType newType = top.stackOn(bottom);
        if (newType == null) return false;

        snowballs.remove(top);
        snowballs.remove(bottom);
        Snowball stacked = new Snowball(bottom.getRow(), bottom.getCol(), newType);
        snowballs.add(stacked);

        Position bottomPos = new Position(bottom.getRow(), bottom.getCol());
        if (view != null) {
            view.onSnowballStacked(bottomPos, newType);
        }

        if (newType == SnowballType.COMPLETE) {
            boardContent.get(bottom.getRow()).set(bottom.getCol(), PositionContent.SNOWMAN);

            if (view != null) {
                view.onSnowmanCreated(bottomPos, newType);
                Position snowmanPos = new Position(bottom.getRow() + 1, bottom.getCol());
                storeGameDetails(snowmanPos);
            }
        }

        return true;
    }

    public void storeGameDetails(Position snowmanPosition) {
        SnowmanFile snowmanFile = new SnowmanFile();
        snowmanFile.setFilename("Snowman" + snowmanFile.getCurrentDate() + ".txt");
        snowmanFile.createFile();

        snowmanFile.writeFile(
                game != null ? game.getMapName() : "Unknown",
                generateMapString(),
                game != null ? game.getMoveHistoryArray() : new String[0],
                game != null ? game.getMoveCount() : 0,
                game != null ? game.getPlayerName() : "Unknown",
                snowmanPosition
        );

        if (game != null && scoreListener != null) {
            Score score = new Score(game.getPlayerName(), game.getMapName(), game.getMoveCount());
            scoreListener.onScore(score);
        }
    }

    public String[] generateMapString() {
        String[] mapLines = new String[getRowCount()];
        Position snowmanPosition = null;

        for (int row = 0; row < getRowCount(); row++) {
            for (int col = 0; col < getColCount(); col++) {
                if (boardContent.get(row).get(col) == PositionContent.SNOWMAN) {
                    snowmanPosition = new Position(row, col);
                    break;
                }
            }
        }

        for (int row = 0; row < getRowCount(); row++) {
            StringBuilder line = getStringBuilder(row, snowmanPosition);
            mapLines[row] = line.toString();
        }

        return mapLines;
    }

    private StringBuilder getStringBuilder(int row, Position snowmanPosition) {
        StringBuilder line = new StringBuilder();

        for (int col = 0; col < getColCount(); col++) {
            Position current = new Position(row, col);

            if (monster.getRow() == row && monster.getCol() == col) {
                line.append("\t\uD83D\uDC79\t");
            } else if (snowmanPosition != null && snowmanPosition.equals(current)) {
                line.append(" SM ");
            } else {
                PositionContent content = boardContent.get(row).get(col);
                switch (content) {
                    case BLOCK -> line.append("\tB\t");
                    case SNOW -> line.append("\tS\t");
                    case NO_SNOW -> line.append("\tX\t");
                    default -> line.append("\t☃️\t");
                }
            }
        }
        return line;
    }

    public boolean unstackSnowballs(Snowball stacked, Direction direction) {
        Snowball bottom = getBottom(stacked);
        Snowball top = getTop(stacked, direction);

        if (bottom != null && top != null && canUnstack(top.getRow(), top.getCol())) {
            snowballs.remove(stacked);
            snowballs.add(top);
            snowballs.add(bottom);

            if (view != null) {
                view.onSnowballUnstacked(top, bottom);
            }

            return true;
        }

        return false;
    }

    public boolean isSnowballStack(Snowball snowball) {
        return snowball.isSnowballStack();
    }

    void checkCompleteSnowman(Position snowmanPos) {
        Snowball base = getSnowballInPosition(snowmanPos.getRow(), snowmanPos.getCol());
        if (base == null || base.getType() != SnowballType.BIG_MID) return;

        Snowball top = getSnowballInPosition(snowmanPos.getRow() - 1, snowmanPos.getCol());
        if (top != null && top.getType() == SnowballType.SMALL) {
            snowballs.remove(base);
            snowballs.remove(top);

            Snowball snowman = new Snowball(snowmanPos.getRow(), snowmanPos.getCol(), SnowballType.COMPLETE);
            snowballs.add(snowman);

            boardContent.get(snowmanPos.getRow()).set(snowmanPos.getCol(), PositionContent.SNOWMAN);

            if (view != null) {
                view.onSnowmanCreated(snowmanPos, SnowballType.COMPLETE);
            }
        }
    }

    public Snowball getBottom(Snowball stack) {
        return switch (stack.getType()) {
            case MID_SMALL -> new Snowball(stack.getRow(), stack.getCol(), SnowballType.MID);
            case BIG_MID, BIG_SMALL -> new Snowball(stack.getRow(), stack.getCol(), SnowballType.BIG);
            default -> null;
        };
    }

    public Snowball getTop(Snowball stack, Direction direction) {
        Position position = new Position(stack.getRow(), stack.getCol()).changePosition(direction);
        SnowballType type = switch (stack.getType()) {
            case MID_SMALL, BIG_SMALL -> SnowballType.SMALL;
            case BIG_MID -> SnowballType.MID;
            default -> null;
        };
        return type == null ? null : new Snowball(position.getRow(), position.getCol(), type);
    }

    public void saveState() {
        while (history.size() > currentStateIndex + 1) {
            history.remove(history.size() - 1);
        }

        if (history.size() >= MAX_HISTORY_SIZE) {
            history.remove(0);
            currentStateIndex--;
        }

        GameState snapshot = cloneCurrentState();
        history.add(snapshot);
        currentStateIndex++;
    }

    private GameState cloneCurrentState() {
        List<List<PositionContent>> boardCopy = new ArrayList<>();
        for (List<PositionContent> row : boardContent) {
            boardCopy.add(new ArrayList<>(row));
        }

        List<Snowball> snowballCopy = new ArrayList<>();
        for (Snowball s : snowballs) {
            snowballCopy.add(new Snowball(s.getRow(), s.getCol(), s.getType()));
        }

        Monster clonedMonster = new Monster(monster.getRow(), monster.getCol());

        return new GameState(clonedMonster, snowballCopy, boardCopy);
    }

    public boolean undo() {
        if (canUndo()) {
            currentStateIndex--;
            restoreState(history.get(currentStateIndex));

            if (view != null) {
                view.updateBoard();
            }

            return true;
        }
        return false;
    }

    public boolean redo() {
        if (canRedo()) {
            currentStateIndex++;
            restoreState(history.get(currentStateIndex));

            if (view != null) {
                view.updateBoard();
            }

            return true;
        }
        return false;
    }

    public boolean canUndo() {
        return currentStateIndex > 0;
    }

    public boolean canRedo() {
        return currentStateIndex < history.size() - 1;
    }

    private void restoreState(GameState state) {
        boardContent.clear();
        for (List<PositionContent> row : state.boardContent()) {
            boardContent.add(new ArrayList<>(row));
        }

        snowballs.clear();
        for (Snowball s : state.snowballs()) {
            snowballs.add(new Snowball(s.getRow(), s.getCol(), s.getType()));
        }

        monster = new Monster(state.monster().getRow(), state.monster().getCol());
    }

    public void setPositionContent(int row, int col, PositionContent content) {
        if (row >= 0 && row < getRowCount() && col >= 0 && col < getColCount()) {
            boardContent.get(row).set(col, content);

            if (boardListener != null) {
                boardListener.onTerrainChanged(row, col, content);
            }
        }
    }
}