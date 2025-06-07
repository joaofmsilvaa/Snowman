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
    // CORE GAME STATE
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

    /// GETTER METHODS
    /// These methods provide read-only access to game state information

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



    // GAME LOGIC AND COLLISION DETECTION

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
        // PRE-MOVEMENT STATE CAPTURE
        // Store original positions before any modifications for rollback capability
        Position oldPosition = new Position(monster.getRow(), monster.getCol());
        Snowball snowball = snowballInFrontOfMonster(direction);
        Position oldSnowballPosition = null;

        // If there's a snowball to push, record its current position
        if (snowball != null) {
            oldSnowballPosition = new Position(snowball.getRow(), snowball.getCol());
        }

        // MOVEMENT ATTEMPT
        // Delegate actual movement logic to Monster class, which handles:
        // Boundary checking, collision detection, snowball pushing
        boolean moved = monster.move(direction, this);

        // POST-MOVEMENT PROCESSING
        // Only proceed if movement was successful
        if (moved) {
            // Save current state to undo history AFTER successful move
            saveState();

            Position currentPosition = new Position(monster.getRow(), monster.getCol());

            // UI NOTIFICATION SYSTEM
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

            // EVENT LISTENER NOTIFICATIONS
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

    /**
     * Attempts to stack one snowball on top of another.
     *
     * This method delegates the stacking logic to the Snowball class. If the two snowballs can be stacked,
     * it removes them from the collection, creates a new stacked snowball of the resulting type,
     * updates the board content and notifies the view. If the new snowball completes a snowman,
     * it also triggers the appropriate view callbacks and stores the game details.
     *
     * @param top    the Snowball instance to be placed on top
     * @param bottom the Snowball instance that will become the base of the stack
     * @return       true if the snowballs were successfully stacked; false otherwise
     */
    public boolean tryStackSnowballs(Snowball top, Snowball bottom) {
        // Attempt to stack the two snowballs
        SnowballType newType = top.stackOn(bottom);
        if (newType == null) return false;

        // Update the snowballs collection
        snowballs.remove(top);
        snowballs.remove(bottom);

        // Create a new snowball at the bottom position
        Snowball stacked = new Snowball(bottom.getRow(), bottom.getCol(), newType);
        snowballs.add(stacked);

        //Notify the view about the stacking
        Position bottomPos = new Position(bottom.getRow(), bottom.getCol());
        if (view != null) {
            view.onSnowballStacked(bottomPos, newType);
        }

        //Check if a complete snowman was formed
        if (newType == SnowballType.COMPLETE) {
            //update the board content to show the snowman at the position
            boardContent.get(bottom.getRow()).set(bottom.getCol(), PositionContent.SNOWMAN);


            if (view != null) {
                view.onSnowmanCreated(bottomPos, newType);
                // The base is at (bottom.getRow(), bottom.getCol()), so the “head” is one row below:
                Position snowmanPos = new Position(bottom.getRow() + 1, bottom.getCol());
                storeGameDetails(snowmanPos);
            }
        }

        return true;
    }

    /**
     * Stores the details of a completed snowman game into a file and notifies the score listener.
     *
     * This method creates a new SnowmanFile with a filename based on the current date,
     * writes the game details (map name, map string, move history, move count, player name, and
     * the position of the completed snowman) into the file, and, if a score listener is
     * registered, constructs a Score object and triggers the onScore callback.
     *
     * @param snowmanPosition the Position where the snowman was completed
     */
    public void storeGameDetails(Position snowmanPosition) {
        //Initialize and configure the SnowmanFile
        SnowmanFile snowmanFile = new SnowmanFile();
        snowmanFile.setFilename("Snowman" + snowmanFile.getCurrentDate() + ".txt");
        snowmanFile.createFile();

        /*
        Map name (or "Unknown" if game is null)
        Serialized map string
        Move history array (or empty array if game is null)
        Move count (or 0 if game is null)
        Player name (or "Unknown" if game is null)
        Position of the completed snowman
         */
        snowmanFile.writeFile(
                game != null ? game.getMapName() : "Unknown",
                generateMapString(),
                game != null ? game.getMoveHistoryArray() : new String[0],
                game != null ? game.getMoveCount() : 0,
                game != null ? game.getPlayerName() : "Unknown",
                snowmanPosition
        );

        // Notify the score listener if available
        if (game != null && scoreListener != null) {
            Score score = new Score(game.getPlayerName(), game.getMapName(), game.getMoveCount());
            scoreListener.onScore(score);
        }
    }


    /**
     * Generates a string array representation of the current game map.
     *
     * This method first locates the position of the completed snowman on the board.
     * Then, for each row of the board, it constructs a string (via getStringBuilder)
     * that includes all relevant symbols, potentially highlighting the snowman's location.
     *
     * @return an array of strings, where each element represents one row of the game map
     */
    public String[] generateMapString() {
        // Create an array to hold each row of the map as a string
        String[] mapLines = new String[getRowCount()];
        Position snowmanPosition = null;

        // Iterate over the board to find the cell marked as SNOWMAN
        for (int row = 0; row < getRowCount(); row++) {
            for (int col = 0; col < getColCount(); col++) {
                if (boardContent.get(row).get(col) == PositionContent.SNOWMAN) {
                    snowmanPosition = new Position(row, col);
                    break;
                }
            }
        }

        // Build each line of the map into a string
        // For each row, use a helper to construct a StringBuilder that includes
        // all relevant characters, possibly highlighting the snowman location
        for (int row = 0; row < getRowCount(); row++) {
            StringBuilder line = getStringBuilder(row, snowmanPosition);
            mapLines[row] = line.toString();
        }

        // Return the complete array of map strings, one entry per row
        return mapLines;
    }

    /**
     * Constructs a string representation for a single row of the game board.
     *
     * This method iterates through each column in the specified row and builds
     * a StringBuilder containing symbols for the monster, the completed snowman
     * (if present), or other board content (blocks, snow, no snow, or generic snowball).
     *
     * @param row               the index of the row to render
     * @param snowmanPosition   the Position of the completed snowman, or null if none
     * @return                  a StringBuilder containing the symbols for this row
     */
    private StringBuilder getStringBuilder(int row, Position snowmanPosition) {
        StringBuilder line = new StringBuilder();

        // Iterate over each column and append the appropriate symbol
        for (int col = 0; col < getColCount(); col++) {
            Position current = new Position(row, col);

            if (monster.getRow() == row && monster.getCol() == col) {
                // Monster emoji
                line.append("\t\uD83D\uDC79\t");

            } else if (snowmanPosition != null && snowmanPosition.equals(current)) {
                // append the snowman
                line.append(" SM ");
            } else {
                // otherwise, append symbol based on board content
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

    /**
     * Attempts to separate a combined snowball back into its top and bottom components.
     *
     * This method identifies the bottom and top parts of the provided stacked snowball.
     * It then verifies whether the top part can be unstacked into the specified direction.
     * If valid, it removes the combined snowball from the collection, re-adds its separate
     * parts, and notifies the view. Returns true if the unstack operation succeeds.
     *
     * @param stacked   the combined Snowball instance to be unstacked
     * @param direction the Direction in which the top part will be placed after unstacking
     * @return          true if unstacking succeeds; false otherwise
     */
    public boolean unstackSnowballs(Snowball stacked, Direction direction) {
        // Identify bottom and top snowball parts from the stacked one
        Snowball bottom = getBottom(stacked);
        Snowball top = getTop(stacked, direction);

        // Check that both parts exist and the top can actually be unstacked
        if (bottom != null && top != null && canUnstack(top.getRow(), top.getCol())) {
            // Update the snowballs collection – remove combined, add separate parts
            snowballs.remove(stacked);
            snowballs.add(top);
            snowballs.add(bottom);

            // Notify the view with the top and the bottom balls
            if (view != null) {
                view.onSnowballUnstacked(top, bottom);
            }

            return true;
        }

        return false;
    }

    /**
     * Checks if the given Snowball instance represents a stacked snowball.
     *
     * This method delegates the check to the Snowball class, maintaining a consistent
     * interface for querying snowball state.
     *
     * @param snowball the Snowball instance to check
     * @return         true if the snowball is a stacked snowball; false otherwise
     */
    public boolean isSnowballStack(Snowball snowball) {
        // Delegate the check to the Snowball object
        return snowball.isSnowballStack();
    }

    /**
     * Verifies if the snowballs at and above the given position form a complete snowman.
     *
     * This method checks whether there is a “BIG_MID” snowball at the specified position
     * and a “SMALL” snowball directly above it. If both exist, it removes them and replaces
     * them with a single “COMPLETE” snowman snowball. It also updates the board content
     * and notifies the view if applicable.
     *
     * @param snowmanPos the Position where the base (“BIG_MID”) of the potential snowman is located
     */
    void checkCompleteSnowman(Position snowmanPos) {
        //Locate the base snowball and verify its type
        Snowball base = getSnowballInPosition(snowmanPos.getRow(), snowmanPos.getCol());
        if (base == null || base.getType() != SnowballType.BIG_MID) return;

        //Locate the top snowball directly above the base
        Snowball top = getSnowballInPosition(snowmanPos.getRow() - 1, snowmanPos.getCol());
        if (top != null && top.getType() == SnowballType.SMALL) {
            snowballs.remove(base);
            snowballs.remove(top);

            // Create and add the COMPLETE snowman snowball
            Snowball snowman = new Snowball(snowmanPos.getRow(), snowmanPos.getCol(), SnowballType.COMPLETE);
            snowballs.add(snowman);

            // update the board content to reflect the new snowman
            boardContent.get(snowmanPos.getRow()).set(snowmanPos.getCol(), PositionContent.SNOWMAN);

            if (view != null) {
                view.onSnowmanCreated(snowmanPos, SnowballType.COMPLETE);
            }
        }
    }

    /**
     * Returns the bottom component of a stacked snowball.
     *
     * Given a combined snowball instance, this method determines the appropriate
     * “bottom” snowball based on the stack’s type. For example, a MID_SMALL stack
     * has a MID bottom, while BIG_MID and BIG_SMALL stacks share the same bottom size.
     *
     * @param stack the combined Snowball instance to decompose
     * @return      a new Snowball representing the bottom part, or null if not applicable
     */
    public Snowball getBottom(Snowball stack) {
        // Determine bottom based on stack type
        return switch (stack.getType()) {

            // a MID_SMALL stack has a MID snowball at its bottom position
            case MID_SMALL ->
                    new Snowball(stack.getRow(), stack.getCol(), SnowballType.MID);

            // BIG_MID and BIG_SMALL stacks both have a BIG snowball as the bottom
            case BIG_MID, BIG_SMALL ->
                    new Snowball(stack.getRow(), stack.getCol(), SnowballType.BIG);
            default -> null;
        };
    }

    /**
     * Returns the top component of a stacked snowball, positioned in the specified direction.
     *
     * This method calculates the position of the “top” part by moving one cell from the
     * base of the stacked snowball in the given direction. It then determines the appropriate
     * SnowballType for the top based on the stack’s type (e.g., MID_SMALL or BIG_SMALL have a SMALL top,
     * BIG_MID has a MID top). If the stack type does not correspond to a valid top part, it returns null.
     *
     * @param stack     the combined Snowball instance to decompose
     * @param direction the Direction in which the top part should be placed
     * @return          a new Snowball representing the top part at its correct position, or null if not applicable
     */
    public Snowball getTop(Snowball stack, Direction direction) {
        //Calculate the target position for the top component
        // Start from the base’s row and column, then move one cell in the specified direction
        Position position = new Position(stack.getRow(),
                stack.getCol()).changePosition(direction);

        // Determine the SnowballType of the top component based on the stack type
        SnowballType type = switch (stack.getType()) {
            case MID_SMALL, BIG_SMALL -> SnowballType.SMALL;
            case BIG_MID -> SnowballType.MID;
            default -> null;
        };

        // Create and return the new Snowball if type is valid, otherwise return null
        return type == null
                ? null
                : new Snowball(position.getRow(), position.getCol(), type);
    }

    /**
     * Saves the current game state to the history stack, maintaining a maximum history size.
     *
     * This method performs the following steps:
     * 1. Removes any “future” states if the user has undone moves and then makes a new move.
     * 2. If the history exceeds MAX_HISTORY_SIZE, drops the oldest state to make room.
     * 3. Clones the current game state and appends it to the history list.
     * 4. Advances the currentStateIndex to point to the newly saved snapshot.
     */
    public void saveState() {
        // Discard any states “ahead” of the currentStateIndex
        while (history.size() > currentStateIndex + 1) {
            history.remove(history.size() - 1);
        }

        // Enforce maximum history size
        // If we’ve reached MAX_HISTORY_SIZE, remove the oldest state (index 0)
        // and adjust currentStateIndex accordingly.
        if (history.size() >= MAX_HISTORY_SIZE) {
            history.remove(0);
            currentStateIndex--;
        }

        // Clone the current game state and append to history
        GameState snapshot = cloneCurrentState();
        history.add(snapshot);
        currentStateIndex++; // Point to the newly added snapshot
    }

    /**
     * Creates and returns a deep copy of the current game state.
     *
     * This method performs the following steps:
     * 1. Deep-copies the board content into a new list of lists.
     * 2. Deep-copies each Snowball into a new list.
     * 3. Creates a new Monster instance at the same position.
     * 4. Constructs a new GameState using the cloned monster, snowballs list, and board content.
     *
     * @return a new GameState object that is an exact duplicate of the current state
     */
    private GameState cloneCurrentState() {
        // Copy the board content
        List<List<PositionContent>> boardCopy = new ArrayList<>();
        for (List<PositionContent> row : boardContent) {

            // Create a new list for each row to avoid shared references
            boardCopy.add(new ArrayList<>(row));
        }

        // Copy the snowballs list
        List<Snowball> snowballCopy = new ArrayList<>();

        // Create a new Snowball instance with the same row, column, and type
        for (Snowball snowball : snowballs) {
            snowballCopy.add(new Snowball(snowball.getRow(), snowball.getCol(), snowball.getType()));
        }

        // Clone the monster
        Monster clonedMonster = new Monster(monster.getRow(), monster.getCol());

        // Construct and return the new GameState
        return new GameState(clonedMonster, snowballCopy, boardCopy);
    }

    /**
     * Undoes the last action by reverting to the previous game state in the history.
     *
     * This method checks if an undo operation is possible. If so, it decrements the
     * currentStateIndex to point to the previous snapshot, restores that state, and
     * notifies the view to refresh the board. Returns true if the undo succeeds.
     *
     * @return true if the game state was successfully reverted; false otherwise
     */
    public boolean undo() {
        //Check if undo is possible
        if (canUndo()) {
            // Move index back to the previous state
            currentStateIndex--;
            // Restore game to the selected snapshot
            restoreState(history.get(currentStateIndex));

            // Notify the view to update the board display
            if (view != null) {
                view.updateBoard();
                boardListener.updateBoard();
            }

            // Undo made
            return true;
        }
        // Undo no
        return false;
    }

    /**
     * Reapplies an undone action by advancing to the next game state in the history.
     *
     * This method checks if a redo operation is possible. If so, it increments the
     * currentStateIndex to point to the next snapshot, restores that state, and
     * notifies the view to refresh the board. Returns true if the redo succeeds.
     *
     * @return true if the game state was successfully reapplied; false otherwise
     */
    public boolean redo() {
        // Check if redo is possible
        if (canRedo()) {
            // Advance index to the next state
            currentStateIndex++;
            //Restore game to the selected snapshot
            restoreState(history.get(currentStateIndex));

            // Notify the view to update the board display
            if (view != null) {
                view.updateBoard();
            }

            // redo made
            return true;
        }
        // redo no
        return false;
    }

    /**
     * Determines whether an undo operation can be performed.
     *
     * An undo is possible if there is at least one previous state in the history
     * (the currentStateIndex is greater than zero).
     *
     * @return true if there is a prior state to revert to; false otherwise
     */
    public boolean canUndo() {
        // Check if there is a previous state in the history
        return currentStateIndex > 0;
    }

    /**
     * Determines whether a redo operation can be performed.
     *
     * A redo is possible if there is at least one subsequent state in the history
     * (The currentStateIndex is less than the index of the last stored state).
     *
     * @return true if there is a future state to advance to; false otherwise
     */
    public boolean canRedo() {
        // Check if there is a next state in the history
        return currentStateIndex < history.size() - 1;
    }

    /**
     * Restores the game to a previously saved state.
     *
     * This method clears the current board content, snowballs list, and monster,
     * then repopulates them using the data from the provided GameState instance.
     *
     * @param state the GameState snapshot to restore
     */
    private void restoreState(GameState state) {
        // Restore the board content
        boardContent.clear();
        // Deep-copy each row of the saved board content into the current board
        for (List<PositionContent> row : state.boardContent()) {
            boardContent.add(new ArrayList<>(row));
        }
        // Restore the snowballs list
        // Remove any existing snowballs
        snowballs.clear();
        // For each saved Snowball, create a new instance with the same position and type
        for (Snowball s : state.snowballs()) {
            snowballs.add(new Snowball(s.getRow(), s.getCol(), s.getType()));
        }
        // Restore the monster’s position
        monster = new Monster(state.monster().getRow(), state.monster().getCol());
    }

    /**
     * Sets the content of a specific board cell and notifies the listener of any terrain change.
     *
     * This method first checks that the provided row and column indices are within the valid board range.
     * If valid, it updates the boardContent at the specified location to the new PositionContent,
     * then informs the boardListener (if registered) about the terrain change.
     *
     * @param row     the row index of the cell to update
     * @param col     the column index of the cell to update
     * @param content the new PositionContent to place at the specified cell
     */
    public void setPositionContent(int row, int col, PositionContent content) {
        // Validate row and column bounds
        if (row >= 0 && row < getRowCount() && col >= 0 && col < getColCount()) {
            // Update the board content
            boardContent.get(row).set(col, content);

            // Notify the listener about the terrain change
            if (boardListener != null) {
                boardListener.onTerrainChanged(row, col, content);
            }
        }
    }
}