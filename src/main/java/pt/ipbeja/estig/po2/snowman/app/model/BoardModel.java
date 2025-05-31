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
 * <p>
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
    private int moveCount = 0;
    private Consumer<Score> scoreConsumer;

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

    public Snowball getSnowballInPosition(int row, int col) {
        for (Snowball snowball : snowballs) {
            if (snowball.getRow() == row && snowball.getCol() == col) {
                return snowball;
            }
        }
        return null;
    }

    /// Returns the Monster instance
    public Monster getMonster() {
        return monster;
    }

    public String getPlayerName() {
        return game.getPlayerName();
    }

    public int getMoveCount() {
        return game.getMoveCount();
    }

    /**
     * Checks if there is a snowball directly in front of the monster
     * in the given direction.
     *
     * @param direction direction in which the monster is facing
     * @return the Snowball in that cell, or null if no snowball is present
     */
    public Snowball getSnowballInFrontOfMonster(Direction direction) {
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
     * Returns the content of a specific cell.
     *
     * @param row row index
     * @param col column index
     * @return PositionContent at the specified cell
     */
    public PositionContent getPositionContent(int row, int col) {
        return boardContent.get(row).get(col);
    }

    /// Mumber of columns
    public int getColCount() {
        return boardContent.isEmpty() ? 0 : boardContent.get(0).size();
    }

    /// Number of rows
    public int getRowCount() {
        return boardContent.size();
    }

    public void setScoreListener(ScoreListener listener) {
        this.scoreListener = listener;
    }

    ///  Define o objeto Game onde estão o nome do jogador e a quantidade de movimentos
    public void setGame(Game game) {
        this.game = game;

        this.game.setPlayerName(game.getPlayerName());
        this.game.setMapName(game.getMapName());
    }

    /// Define the View that should be notified of graphical updates.
    public void setView(View view) {

        this.view = view;
    }

    /// Registers the MoveListener that receives notifications of every monster move
    public void setMoveListener(MoveListener moveListener) {
        this.moveListener = moveListener;
    }

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

    public boolean validPosition(int newRow, int newCol) {
        try {
            return getPositionContent(newRow, newCol) != PositionContent.BLOCK;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean canUnstack(int newRow, int newCol) {
        return validPosition(newRow, newCol) && getSnowballInPosition(newRow, newCol) == null;
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
        Snowball snowball = getSnowballInFrontOfMonster(direction);
        Position oldSnowballPosition = new Position(0, 0);

        if (snowball != null) {
            oldSnowballPosition = new Position(snowball.getRow(), snowball.getCol());
        }

        /// Attempt to move the monster and also pushes a snowball if present
        boolean moved = monster.move(direction, this);

        if (moved && view != null) {
            Position currentPosition = new Position(monster.getRow(), monster.getCol());

            view.onMonsterCleared(oldPosition);
            view.onMonsterMoved(currentPosition);

            if (snowball != null) {
                view.onSnowballMoved(snowball, oldSnowballPosition);
            }


            if (moveListener != null) {
                moveListener.onMove(oldPosition, currentPosition);
            }
            if (game != null) {
                game.onMove(oldPosition, currentPosition);
            }
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

            if (scoreConsumer != null) {
                Score score = new Score(game.getPlayerName(), game.getMapName(), moveCount);
                scoreConsumer.accept(score);
            }
        }

        return true;
    }

    /**
     * Undoes a snowball stack. Validates the position for the top ball, separates
     * the two balls, and notifies the View.
     *
     * @param stacked   snowball currently in a stack (type MID_SMALL, BIG_MID, or BIG_SMALL)
     * @param direction direction in which the top ball will move
     */
    public void unstackSnowballs(Snowball stacked, Direction direction) {
        Snowball bottom = stacked.getBottom();
        Snowball top = stacked.getTop(stacked, direction);

        if (canUnstack(top.getRow(), top.getCol())) {
            snowballs.remove(stacked);
            snowballs.add(top);
            snowballs.add(bottom);

            if (view != null) {
                /// Notify the view that the snowball was unstacked
                view.onSnowballUnstacked(top, bottom);
            }

        }

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
        game.storeGameDetails(snowmanPosition,scoreListener);
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
    public void checkCompleteSnowman(Position snowmanPos) {
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

            if (scoreConsumer != null) {
                Score score = new Score(game.getPlayerName(), game.getMapName(), moveCount);
                scoreConsumer.accept(score);
            }
        }
    }
}
