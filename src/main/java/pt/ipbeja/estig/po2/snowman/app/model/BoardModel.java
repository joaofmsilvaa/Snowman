package pt.ipbeja.estig.po2.snowman.app.model;

import pt.ipbeja.estig.po2.snowman.app.model.interfaces.MoveListener;
import pt.ipbeja.estig.po2.snowman.app.model.interfaces.View;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Default constructor: initializes data structures and calls startGame()
     * to set up a 5×5 board and initial snowballs.
     */
    public BoardModel() {
        boardContent = new ArrayList<>();
        snowballs = new ArrayList<>();
        startGame(); /// Inicializa tabuleiro, monstro e bolas de neve
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

    ///  Define o objeto Game onde estão o nome do jogador e a quantidade de movimentos
    public void setGame(Game game) {
        this.game = game;

        this.game.setPlayerName(game.getPlayerName());
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

    public String getPlayerName() {
        return game.getPlayerName();
    }

    public int getMoveCount(){
        return game.getMoveCount();
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

    /**
     * Finds a snowball at the specified cell, if any.
     *
     * @param row row index
     * @param col column index
     * @return the Snowball at that position, or null if none exists
     */
    public Snowball snowballInPosition(int row, int col) {
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
            case UP -> snowballInPosition(row - 1, col);
            case DOWN -> snowballInPosition(row + 1, col);
            case LEFT -> snowballInPosition(row, col - 1);
            case RIGHT -> snowballInPosition(row, col + 1);
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

        if (moved && view != null) {

            /// Increment move count after a successful move or push
            game.incrementMoveCount();

            Position currentPosition = new Position(monster.getRow(), monster.getCol());

            /// Inform the view to clear and redraw the monster
            view.onMonsterCleared(oldPosition);
            view.onMonsterMoved(currentPosition);


            /// If a snowball was pushed, inform the view to redraw it
            if (snowball != null) {
                view.onSnowballMoved(snowball, oldSnowballPosition);
            }

            /// Notify external listener about the monster's move
            moveListener.onMove(oldPosition,currentPosition);
        }

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
        String[] moves = {"2a -> 2b"};
        snowmanFile.writeFile("map name", moves, game.getMoveCount(),snowmanPosition );
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

        if (validPosition(top.getRow(), top.getCol())) {
            /// Remove the stacked ball and add the two separate balls
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
        Snowball base = snowballInPosition(snowmanPos.getRow(), snowmanPos.getCol());
        if (base == null || base.getType() != SnowballType.BIG_MID) return;

        Snowball top = snowballInPosition(snowmanPos.getRow() - 1, snowmanPos.getCol());
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


}
