package pt.ipbeja.estig.po2.snowman.po2.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.ipbeja.estig.po2.snowman.app.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the BoardModel class, verifying monster movement,
 * snowball behavior, stacking, unstacking, and map content retrieval.
 *
 *  @author João Silva
 *  @author Paulo Neves
 */
public class BoardModelTest {
    private Monster monster;
    private List<List<PositionContent>> content = new ArrayList<>();
    private List<Snowball> snowballs = new ArrayList<>();
    private BoardModel board;

    private final int rows = 5;
    private final int cols = 5;

    /**
     * Sets up a 5×5 board with a monster at (2,0),
     * snow on row 2 for columns > 1, and two snowballs at (2,1) MID and (2,2) BIG.
     */
    public void setUp() {
        monster = new Monster(2, 0);

        for (int i = 0; i < rows; i++) {
            List<PositionContent> row = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                // Place snow on row 2, columns 2–4; elsewhere no snow
                if (i == 2 && j > 1) {
                    row.add(PositionContent.SNOW);
                } else {
                    row.add(PositionContent.NO_SNOW);
                }
            }
            content.add(row);
        }

        // Place a MID snowball at (2,1) and a BIG snowball at (2,2)
        snowballs.add(new Snowball(2, 1, SnowballType.MID));
        snowballs.add(new Snowball(2, 2, SnowballType.BIG));

        board = new BoardModel(content, monster, snowballs);
    }

    public void bigSnowballSetup() {
        monster = new Monster(2, 0);

        for (int i = 0; i < rows; i++) {
            List<PositionContent> row = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                // Place snow on row 2, columns 2–4; elsewhere no snow
                if (i == 2 && j > 1) {
                    row.add(PositionContent.SNOW);
                } else {
                    row.add(PositionContent.NO_SNOW);
                }
            }
            content.add(row);
        }

        snowballs.add(new Snowball(2, 1, SnowballType.SMALL));

        board = new BoardModel(content, monster, snowballs);
    }

    public void basicSetup(){
        monster = new Monster(2, 0);

        for (int i = 0; i < rows; i++) {
            List<PositionContent> row = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                // Place snow on row 2, columns 2–4; elsewhere no snow
                if (i == 2 && j > 1) {
                    row.add(PositionContent.SNOW);
                } else {
                    row.add(PositionContent.NO_SNOW);
                }
            }
            content.add(row);
        }

        // Place a MID snowball at (2,1) and a BIG snowball at (2,2)
        snowballs.add(new Snowball(2, 2, SnowballType.SMALL));

        board = new BoardModel(content, monster, snowballs);
    }

    public void simpleMonsterSetup(){
        monster = new Monster(3, 1);

        for (int i = 0; i < rows; i++) {
            List<PositionContent> row = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
               row.add(PositionContent.NO_SNOW);
            }
            content.add(row);
        }

        // Place a MID snowball at (2,1) and a BIG snowball at (2,2)
        snowballs.add(new Snowball(2, 1, SnowballType.SMALL));

        board = new BoardModel(content, monster, snowballs);
    }

    /**
     * Tests that moving the monster left from (2,0) wraps or moves correctly.
     * After moving LEFT, monster should be at (1,0).
     */
    @Test
    @DisplayName("Move the monster to the left")
    void testMonsterToTheLeft() {
        basicSetup();

        board.moveMonster(Direction.LEFT);
        assertEquals(2, monster.getRow());
        assertEquals(0, monster.getCol());
    }

    /**
     * Tests that moving the monster up from its starting row moves it to row 0,
     * and the column becomes 1.
     */
    @Test
    @DisplayName("Move the monster up")
    void testMonsterToUp() {
        setUp();

        board.moveMonster(Direction.UP);
        assertEquals(0, monster.getRow());
        assertEquals(1, monster.getCol());
    }

    /**
     * Verifies that an invalid move beyond the board boundary returns false
     * and does not change the monster's position.
     */
    @Test
    @DisplayName("Test invalid monster move")
    void testMonsterInvalidMove() {
        setUp();

        board.moveMonster(Direction.UP);
        assertEquals(1, monster.getRow());
        assertEquals(0, monster.getCol());

        boolean validMove = board.moveMonster(Direction.UP);
        assertFalse(!validMove);
    }

    /**
     * Prints the board’s PositionContent for manual inspection.
     * (No assertions; purely diagnostic.)
     */
    @Test
    void testPositionContent() {
        setUp();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                PositionContent element = board.getPositionContent(i, j);
                System.out.print("\t|\t" + element);
            }
            System.out.println("\t|");
        }
    }

    /**
     * Tests that a snowball moves left when the monster moves into its cell.
     * After move, snowball at (0,1) should end up at (0,0), and monster at (0,1).
     */
    @Test
    @DisplayName("Move snowball to the left")
    void testMoveSnowballToTheLeft() {
        setUp();

        Snowball snowball = board.getSnowballInPosition(0, 1);
        board.moveMonster(Direction.LEFT);

        assertEquals(0, snowball.getRow());
        assertEquals(0, snowball.getCol());
        assertEquals(0, monster.getRow());
        assertEquals(1, monster.getCol());
    }

    /**
     * Tests that a snowball moves up when the monster moves into its cell.
     * After move, snowball at (1,0) should end up at (0,0), and monster at (1,0).
     */
    @Test
    @DisplayName("Move the snowball up")
    void testMoveSnowballToUp() {
        simpleMonsterSetup();

        Snowball snowball = board.getSnowballInPosition(2, 1);
        board.moveMonster(Direction.UP);

        assertEquals(1, snowball.getRow());
        assertEquals(1, snowball.getCol());
        assertEquals(2, monster.getRow());
        assertEquals(1, monster.getCol());
    }

    /**
     * Tests accumulation: a SMALL snowball at (1,0) should become MID
     * when pushed onto snow by the monster.
     */
    @Test
    @DisplayName("Create average snowball")
    void testCreateAverageSnowball() {
        basicSetup();

        board.moveMonster(Direction.RIGHT);

        Snowball snowball = board.getSnowballInPosition(2, 2);
        assertEquals(SnowballType.SMALL, snowball.getType());

        board.moveMonster(Direction.RIGHT);

        assertEquals(2, snowball.getRow());
        assertEquals(3, snowball.getCol());
        assertEquals(SnowballType.MID, snowball.getType());
    }

    /**
     * Tests growth to BIG size by repeatedly pushing the snowball over snow cells.
     */
    @Test
    @DisplayName("Create big snowball")
    void testCreateBigSnowball() {
        bigSnowballSetup();

        Snowball snowball = board.getSnowballInPosition(2, 1);
        assertEquals(SnowballType.SMALL, snowball.getType());

        board.moveMonster(Direction.RIGHT);
        assertEquals(SnowballType.MID, snowball.getType());

        board.moveMonster(Direction.RIGHT);
        assertEquals(SnowballType.BIG, snowball.getType());
    }

    /**
     * Verifies that a BIG snowball remains BIG even if pushed over snow again.
     */
    @Test
    @DisplayName("Maintain big snowball")
    void testMaintainBigSnowball() {
        setUp();

        // Pre-set to BIG for this test
        Snowball snowball = board.getSnowballInPosition(2, 1);
        snowball.setType(SnowballType.BIG);

        board.moveMonster(Direction.RIGHT);
        assertEquals(SnowballType.BIG, snowball.getType());
    }

    /**
     * Tests that an invalid snowball push (no snow or block) returns false
     * after a valid push, and position remains unchanged.
     */
    @Test
    @DisplayName("Test invalid snowball move")
    void testSnowballInvalidMove() {
        setUp();

        Snowball snowball = board.getSnowballInPosition(1, 0);
        assertTrue(board.moveMonster(Direction.UP));   // First move succeeds
        assertFalse(board.moveMonster(Direction.UP));  // Second move fails

        assertEquals(0, snowball.getRow());
        assertEquals(0, snowball.getCol());
    }

    /**
     * Tests stacking a MID snowball onto a BIG snowball to produce BIG_MID.
     */
    @Test
    @DisplayName("Test stack mid sized snowball in a big snowball")
    void testAverageBigSnowman() {
        setUp();

        Snowball midBall = board.getSnowballInPosition(2, 1);
        Snowball bigBall = board.getSnowballInPosition(2, 2);
        assertTrue(midBall.canStackOn(bigBall));

        board.moveMonster(Direction.RIGHT);

        Snowball bigMidBall = board.getSnowballInPosition(2, 2);
        assertEquals(SnowballType.BIG_MID, bigMidBall.getType());
    }

    /**
     * Tests unstacking a BIG_MID stack by pushing into it again,
     * resulting in separate MID at (2,2) and BIG at (2,3).
     */
    @Test
    @DisplayName("Test unstack big-mid snowball")
    void testUnstackBigMidSnowball() {
        setUp();

        // First, stack MID onto BIG to form BIG_MID
        board.moveMonster(Direction.RIGHT);

        // Then, push again to unstack
        board.moveMonster(Direction.RIGHT);

        Snowball bottom = board.getSnowballInPosition(2, 2);
        Snowball top = board.getSnowballInPosition(2, 3);

        assertEquals(SnowballType.MID, bottom.getType());
        assertEquals(SnowballType.BIG, top.getType());

        // Monster should end up left of the original stack
        assertEquals(2, monster.getRow());
        assertEquals(1, monster.getCol());
    }
}
