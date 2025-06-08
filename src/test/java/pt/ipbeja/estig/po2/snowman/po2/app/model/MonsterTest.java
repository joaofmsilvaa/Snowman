package pt.ipbeja.estig.po2.snowman.po2.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.ipbeja.estig.po2.snowman.app.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Unit tests for the Monster class, verifying its movement logic on the board.
 *
 * The tests cover:
 * 1. Moving the monster left.
 * 2. Moving the monster up.
 * 3. Handling invalid moves (attempting to move into a blocked or out-of-bounds cell).
 *
 * @author João Silva
 * @author Paulo Neves
 */
public class MonsterTest {
    private Monster monster;
    private List<List<PositionContent>> content = new ArrayList<>();
    private List<Snowball> snowballs = new ArrayList<>();
    private BoardModel board;

    private final int rows = 3;
    private final int cols = 3;

    /**
     * Sets up a 3×3 board where:
     * - The monster starts at position (2,2).
     * - The top row (row 0) is covered in snow; other cells have no snow.
     * - No snowballs are placed.
     */
    @BeforeEach
    public void setUp() {
        monster = new Monster(2, 2);

        // Initialize board content: snow in row 0, no snow elsewhere
        for (int i = 0; i < rows; i++) {
            List<PositionContent> row = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                row.add(i == 0 ? PositionContent.SNOW : PositionContent.NO_SNOW);
            }
            content.add(row);
        }

        // Create the BoardModel with the monster and no snowballs
        board = new BoardModel(content, monster, snowballs);
    }

    /**
     * Tests that the monster can move left on the board.
     * After moving LEFT from (2,2), the expected position is (2,1).
     */
    @Test
    @DisplayName("Move the monster to the left")
    void testMonsterToTheLeft() {
        // Attempt to move the monster one cell to the left
        monster.move(Direction.LEFT, board);

        // Verify new coordinates
        assertEquals(2, monster.getRow());
        assertEquals(1, monster.getCol());
    }

    /**
     * Tests that the monster can move up on the board.
     * After moving UP from (2,2), the expected position is (1,2).
     */
    @Test
    @DisplayName("Move the monster up")
    void testMonsterToUp() {
        // Attempt to move the monster one cell up
        monster.move(Direction.UP, board);

        // Verify new coordinates
        assertEquals(1, monster.getRow());
        assertEquals(2, monster.getCol());
    }

    /**
     * Tests that an invalid move (moving into a boundary or blocked cell)
     * is correctly rejected and does not change the monster's position.
     */
    @Test
    @DisplayName("Test invalid monster move")
    void testMonsterInvalidMove() {
        // Attempt to move the monster right into an out-of-bounds column
        boolean validMove = board.moveMonster(Direction.RIGHT);

        // Monster should remain at its original position
        assertEquals(2, monster.getRow());
        assertEquals(2, monster.getCol());

        // The move should be reported as invalid
        assertFalse(validMove);
    }
}