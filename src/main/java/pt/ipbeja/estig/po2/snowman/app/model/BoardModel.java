package pt.ipbeja.estig.po2.snowman.app.model;

import pt.ipbeja.estig.po2.snowman.app.model.interfaces.MoveListener;
import pt.ipbeja.estig.po2.snowman.app.model.interfaces.ScoreListener;
import pt.ipbeja.estig.po2.snowman.app.model.interfaces.View;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * BoardModel manages all game logic:
 * - The board content (boardContent)
 * - The positions of the monster and snowballs
 * - Move count and player name
 * - Score registration via ScoreManager
 *
 * It is also responsible for notifying the View and MoveListener when
 * relevant events occur (monster movement, pushing snowballs, stacking,
 * and creating a complete snowman).
 */
public class BoardModel {
    private List<List<PositionContent>> boardContent;
    private Monster monster;
    private List<Snowball> snowballs;
    private View view;
    private MoveListener moveListener;
    private Game game;
    private ScoreListener scoreListener;
    private String mapFileName;
    private int currentStateIndex = -1;
    private List<GameState> history = new ArrayList<>();

    /**
     * Default constructor: initializes data structures and calls startGame()
     * to set up a 5×5 board and initial snowballs.
     */
    public BoardModel() {
        boardContent = new ArrayList<>();
        snowballs = new ArrayList<>();
        startGame();
    }


    /**
     * Constructor for creating a BoardModel from existing content.
     *
     * @param content   matrix of PositionContent representing the board layout
     * @param monster   Monster instance located at a specified cell
     * @param snowballs list of Snowball instances on the board
     */
    public BoardModel(List<List<PositionContent>> content, Monster monster, List<Snowball> snowballs) {
        this.monster = monster;
        this.snowballs = snowballs;
        this.boardContent = content;

    }

    /// Define the View that should be notified of graphical updates.
    public void setView(View view) {

        this.view = view;
    }

    /// Registers the MoveListener that receives notifications of every monster move
    public void setMoveListener(MoveListener moveListener) {
        this.moveListener = moveListener;
    }

    public void setMapFileName(String mapFileName) {
        this.mapFileName = mapFileName;
    }

    /// Registers the ScoreListener that will be notified of new scores.
    public void setScoreListener(ScoreListener listener) {
        this.scoreListener = listener;
    }

    public void setPlayerName(String playerName) {
        game.setPlayerName(playerName);
    }

    /**
     * Sets up the initial game state:
     * - A 5×5 board with the first row filled with SNOW and the rest with NO_SNOW
     * - Monster placed at (2,0)
     * - Three small snowballs at positions (2,1), (2,2), and (2,3)
     */
    public void startGame() {
        monster = new Monster(2, 0);

        for (int i = 0; i < 5; i++) {
            List<PositionContent> row = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                if (i == 0) {
                    row.add(PositionContent.SNOW);
                } else {
                    row.add(PositionContent.NO_SNOW);
                }
            }
            boardContent.add(row);
        }

        /// Place three small snowballs on row 2, columns 1-3
        snowballs.add(new Snowball(2, 1, SnowballType.SMALL));
        snowballs.add(new Snowball(2, 2, SnowballType.SMALL));
        snowballs.add(new Snowball(2, 3, SnowballType.SMALL));
    }

    ///  Defines the Game object where the player name and move count are stored.
    public void setGame(Game game) {
        this.game = game;

        this.game.setPlayerName(game.getPlayerName());
    }


    /// Getter to obtain the name of the map
    public String getMapFileName() {
        return this.mapFileName;
    }

    /// Number of rows
    public int getRowCount() {
        return boardContent.size();
    }

    /// Mumber of columns
    public int getColCount() {
        return boardContent.isEmpty() ? 0 : boardContent.get(0).size();
    }

    /**
     * Returns the content of a specific cell.
     *
     * @param row row index
     * @param col column index
     * @return PositionContent at the specified cell
     */
    public PositionContent getPositionContent(int row, int col) {
        return boardContent.get(row).get(col);
    }

    ///Returns the Monster instance
    public Monster getMonster() {
        return monster;
    }

    /// Returns player name
    public String getPlayerName() {
        return game.getPlayerName();
    }

    /// Returns the current move count
    public int getMoveCount(){
        return game.getMoveCount();
    }

    /// Get the status game for the restartLevel()
    public Game getGame() {
        return this.game;
    }

    /// set to save the name of the file name
    public void setMapName(String mapName) {
        game.setMapName(mapName);
    }

    /**
     * Checks if a given position is valid: within bounds and not a BLOCK.
     *
     * @param newRow row index to check
     * @param newCol column index to check
     * @return true if the cell is not BLOCK and is inside the board; false otherwise
     */
    public boolean validPosition(int newRow, int newCol) {
        try {
            return getPositionContent(newRow, newCol) != PositionContent.BLOCK;
        } catch (Exception e) {
            return false;
        }
    }



    public boolean canUnstack(int newRow, int newCol){
        return validPosition(newRow, newCol) && getSnowballInPosition(newRow, newCol) == null;
    }

    /**
     * Finds a snowball at the specified cell, if any.
     *
     * @param row row index
     * @param col column index
     * @return the Snowball at that position, or null if none exists
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
     * Checks if there is a snowball directly in front of the monster
     * in the given direction.
     *
     * @param direction direction in which the monster is facing
     * @return the Snowball in that cell, or null if no snowball is present
     */
    public Snowball snowballInFrontOfMonster(Direction direction) {
        int row = monster.getRow();
        int col = monster.getCol();

        return switch (direction) {
            case UP -> getSnowballInPosition(row - 1, col);
            case DOWN -> getSnowballInPosition(row + 1, col);
            case LEFT -> getSnowballInPosition(row, col - 1);
            case RIGHT -> getSnowballInPosition(row, col + 1);
        };
    }

    /**
     * Moves the monster in the given direction. If there is a snowball in front,
     * attempts to push it. Notifies the View and MoveListener of relevant updates.
     *
     * @param direction direction to move the monster
     * @return true if the monster (or snowball) moved successfully; false otherwise
     */
    public boolean moveMonster(Direction direction) {
        Position oldPosition = new Position(monster.getRow(), monster.getCol());

        /// Check if there is a snowball in front of the monster
        Snowball snowball = snowballInFrontOfMonster(direction);
        Position oldSnowballPosition = new Position(0,0);

        if (snowball != null) {
            oldSnowballPosition = new Position(snowball.getRow(), snowball.getCol());
        }

        /// Attempt to move the monster and also pushes a snowball if present
        boolean moved = monster.move(direction, this);

        /// If a View is registered, notify it to clear and redraw the monster and snowball
        if (moved) {
            Position currentPosition = new Position(monster.getRow(), monster.getCol());

            if (view != null) {
                /// Tell the view to clear the monster's old cell
                view.onMonsterCleared(oldPosition);
                /// Tell the view to draw the monster in its new cell
                view.onMonsterMoved(currentPosition);

                if (snowball != null) {
                    /// Tell the view to update the pushed snowball
                    view.onSnowballMoved(snowball, oldSnowballPosition);
                }
            }

            if (moveListener != null) {
                /// Notify the view about the monster’s move
                moveListener.onMove(oldPosition, currentPosition);
            }
            if (game != null) {
                /// Inform the Game object about the move (for logging or state)
                game.onMove(oldPosition, currentPosition);
            }
        }

        saveState();
        return moved;
    }


    /**
     * Moves a snowball in a given direction. Returns false if the move fails.
     *
     * @param direction direction to move the snowball
     * @param snowball  instance of Snowball to move
     * @return true if the snowball moved or stacked; false otherwise
     */
    public boolean moveSnowball(Direction direction, Snowball snowball) {
        return snowball.move(direction, this);
    }

    /**
     * Attempts to stack two snowballs (top onto bottom), updates the snowball list,
     * and notifies the View. If stacking results in a COMPLETE snowman, updates the board
     * and writes game details to a file.
     *
     * @param top    snowball that will be pushed on top
     * @param bottom snowball that will be at the base
     * @return true if the stack was successful; false otherwise
     */
    public boolean tryStackSnowballs(Snowball top, Snowball bottom) {
        SnowballType newType = top.stackOn(bottom);
        if (newType == null) return false;

        /// Remove the original balls and add the new stacked ball
        snowballs.remove(top);
        snowballs.remove(bottom);
        Snowball stacked = new Snowball(bottom.getRow(), bottom.getCol(), newType);
        snowballs.add(stacked);

        Position bottomPos = new Position(bottom.getRow(), bottom.getCol());
        if (view != null) {
            /// Notify the view that a snowball stack was created
            view.onSnowballStacked(bottomPos, newType);
        }

        /// If the new type is COMPLETE, a full snowman is formed
        if (newType == SnowballType.COMPLETE) {
            boardContent.get(bottom.getRow()).set(bottom.getCol(), PositionContent.SNOWMAN);

            if (view != null) {
                /// Notify the view that a complete snowman is created
                view.onSnowmanCreated(bottomPos, newType);

                /// Determine position below for snowman details
                Position snowmanPos = new Position(bottom.getRow() + 1, bottom.getCol()); // Offset da coluna de coordenadas

                storeGameDetails(snowmanPos);
            }
        }

        return true;
    }

    /**
     * Writes game information to a file when a snowman is completed:
     * - Level name ("map name")
     * - Move list (placeholder example, should use actual moves)
     * - Total move count
     * - Final snowman position
     *
     * @param snowmanPosition final position of the snowman (row and column)
     */
    public void storeGameDetails(Position snowmanPosition) {
        SnowmanFile snowmanFile = new SnowmanFile();
        snowmanFile.setFilename("Snowman" + snowmanFile.getCurrentDate() + ".txt");
        snowmanFile.createFile();

        snowmanFile.writeFile(game.getMapName(), generateMapString(), game.getMoveHistoryArray(), game.getMoveCount(), game.getPlayerName(),snowmanPosition);

        // Criar e notificar pontuação
        Score score = new Score(game.getPlayerName(), game.getMapName(), game.getMoveCount());
        if (scoreListener != null) {
            scoreListener.onScore(score);
        }
    }

    /**
     * Generates a textual representation of the current map,
     * using simple symbols for BLOCK, SNOW, NO_SNOW, MONSTER, and SNOWMAN.
     *
     * @return array of strings, each representing one row of the map
     */
    public String[] generateMapString() {
        String[] mapLines = new String[getRowCount()];
        Position snowmanPosition = null;

        /// Find the position of the complete snowman, if it exists
        for (int row = 0; row < getRowCount(); row++) {
            for (int col = 0; col < getColCount(); col++) {
                if (boardContent.get(row).get(col) == PositionContent.SNOWMAN) {
                    snowmanPosition = new Position(row, col);
                    break;
                }
            }
        }

        /// Build each row’s string using getStringBuilder
        for (int row = 0; row < getRowCount(); row++) {
            StringBuilder line = getStringBuilder(row, snowmanPosition);

            mapLines[row] = line.toString();
        }

        return mapLines;
    }

    /**
     * Builds one row of the map string, marking the monster, snowman, or cell content.
     *
     * @param row             the row index to build
     * @param snowmanPosition position of the complete snowman (may be null)
     * @return a StringBuilder containing that row’s textual representation
     */
    private StringBuilder getStringBuilder(int row, Position snowmanPosition) {
        StringBuilder line = new StringBuilder();

        for (int col = 0; col < getColCount(); col++) {
            Position current = new Position(row, col);

            if (monster.getRow() == row && monster.getCol() == col) {
                line.append("\t\uD83D\uDC79\t"); // Monster symbol
            } else if (snowmanPosition.equals(current)) {
                line.append(" SM "); // Complete snowman marker
            } else {
                PositionContent content = boardContent.get(row).get(col);
                switch (content) {
                    case BLOCK -> line.append("\tB\t");
                    case SNOW -> line.append("\tS\t");
                    case NO_SNOW -> line.append("\tX\t");
                    default -> line.append("\t☃️\t"); // Partial stack or other
                }
            }
        }
        return line;
    }

    /**
     * Undoes a snowball stack. Validates the position for the top ball, separates
     * the two balls, and notifies the View.
     *
     * @param stacked   snowball currently in a stack (type MID_SMALL, BIG_MID, or BIG_SMALL)
     * @param direction direction in which the top ball will move
     * @return true if unstack was successful; false otherwise
     */
    public boolean unstackSnowballs(Snowball stacked, Direction direction) {
        Snowball bottom = getBottom(stacked);
        Snowball top = getTop(stacked, direction);

        if (canUnstack(top.getRow(), top.getCol())) {
            snowballs.remove(stacked);
            snowballs.add(top);
            snowballs.add(bottom);

            if (view != null) {
                /// Notify the view that the snowball was unstacked
                view.onSnowballUnstacked(top, bottom);
            }

            return true;
        }

        return false;
    }

    /**
     * Checks if the given snowball is a partial stack (MID_SMALL, BIG_MID, or BIG_SMALL).
     *
     * @param snowball snowball to check
     * @return true if it is a partial stack; false otherwise
     */
    public boolean isSnowballStack(Snowball snowball) {

        return snowball.isSnowballStack();
    }

    /**
     * Verifies if a complete snowman has formed at the specified position.
     * If a BIG_MID base and SMALL top are found, replaces them with a COMPLETE
     * snowball, updates the board, and notifies the View.
     *
     * @param snowmanPos position where the base (BIG_MID) is located
     */
    void checkCompleteSnowman(Position snowmanPos) {
        Snowball base = getSnowballInPosition(snowmanPos.getRow(), snowmanPos.getCol());
        if (base == null || base.getType() != SnowballType.BIG_MID) return;

        Snowball top = getSnowballInPosition(snowmanPos.getRow() - 1, snowmanPos.getCol());
        if (top != null && top.getType() == SnowballType.SMALL) {
            /// Remove base and top to create a full snowman
            snowballs.remove(base);
            snowballs.remove(top);

            Snowball snowman = new Snowball(snowmanPos.getRow(), snowmanPos.getCol(), SnowballType.COMPLETE);
            snowballs.add(snowman);

            boardContent.get(snowmanPos.getRow()).set(snowmanPos.getCol(), PositionContent.SNOWMAN);

            if (view != null) {
                /// Notify the view of a newly created complete snowman
                view.onSnowmanCreated(snowmanPos, SnowballType.COMPLETE);
            }
        }
    }

    /**
     * Given a partial stack (MID_SMALL, BIG_MID, or BIG_SMALL), returns the bottom ball.
     *
     * @param stack snowball of a stack type
     * @return new Snowball instance representing the bottom ball, or null if invalid
     */
    public Snowball getBottom(Snowball stack) {
        return switch (stack.getType()) {
            case MID_SMALL -> new Snowball(stack.getRow(), stack.getCol(), SnowballType.MID);
            case BIG_MID, BIG_SMALL -> new Snowball(stack.getRow(), stack.getCol(), SnowballType.BIG);
            default -> null;
        };
    }

    /**
     * Given a partial stack and a direction, returns the top ball that would
     * result from unstacking in that direction.
     *
     * @param stack     snowball of a stack type
     * @param direction direction in which the top ball will be placed
     * @return new Snowball instance representing the top ball, or null if invalid
     */
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

    public void undo() {
        if(currentStateIndex > 0) {
            currentStateIndex--;
            setState(history.get(currentStateIndex));
            if (view != null) {
                view.updateBoard();
            }
        } else if (currentStateIndex == 0) {
            setState(history.get(currentStateIndex));
            if (view != null) {
                view.updateBoard();
            }
        }
    }

    public void redo() {
        if (currentStateIndex < history.size() - 1) {
            currentStateIndex++;
            setState(history.get(currentStateIndex));
            if (view != null) {
                view.updateBoard();
            }
        }
    }

    private void setState(GameState state) {
        boardContent.clear();
        for (List<PositionContent> row : state.boardContent()) {
            boardContent.add(new ArrayList<>(row));
        }

        snowballs.clear();
        snowballs.addAll(state.snowballs());

        monster = new Monster(state.monster().getRow(), state.monster().getCol());
    }
}
