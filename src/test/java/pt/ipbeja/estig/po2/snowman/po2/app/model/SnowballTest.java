package pt.ipbeja.estig.po2.snowman.po2.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.ipbeja.estig.po2.snowman.app.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Snowball class, verifying movement, growth, and stacking logic.
 *
 * The tests cover:
 * 1. Moving a snowball horizontally and vertically.
 * 2. Growing from SMALL to MID when pushed over snow.
 * 3. Rejecting invalid moves beyond board bounds or into blocks.
 * 4. Increasing snowball size explicitly via increaseSnowballType().
 * 5. Checking stacking eligibility and resulting stack types.
 *
 * @author João Silva
 * @author Paulo Neves
 */
public class SnowballTest {
    private Monster monster;
    private List<List<PositionContent>> content = new ArrayList<>();
    private List<Snowball> snowballs = new ArrayList<>();
    private BoardModel board;

    private final int rows = 3;
    private final int cols = 3;

    /**
     * Sets up a 3×3 board before each test:
     * - Monster starts at (2,0).
     * - Only the cell at (0,1) contains snow; all others are NO_SNOW.
     * - A SMALL snowball is placed at (1,1).
     */
    @BeforeEach
    public void setUp() {
        monster = new Monster(2, 0);

        // Initialize board terrain
        for (int i = 0; i < rows; i++) {
            List<PositionContent> row = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                // Only (0,1) has snow
                row.add(i == 0 && j == 1 ? PositionContent.SNOW : PositionContent.NO_SNOW);
            }
            content.add(row);
        }

        // Place a SMALL snowball at (1,1)
        snowballs.add(new Snowball(1, 1, SnowballType.SMALL));

        board = new BoardModel(content, monster, snowballs);
    }

    /**
     * Tests that a snowball can move left into an empty NO_SNOW cell.
     * After moving LEFT from (1,1), the snowball should be at (1,0).
     */
    @Test
    @DisplayName("Move snowball to the left")
    void testMoveSnowballToTheLeft() {
        Snowball snowball = board.getSnowballInPosition(1, 1);
        snowball.move(Direction.LEFT, board);

        assertEquals(1, snowball.getRow());
        assertEquals(0, snowball.getCol());
    }

    /**
     * Tests that a snowball can move up into a SNOW cell, triggering growth.
     * After moving UP from (1,1), the snowball should be at (0,1).
     */
    @Test
    @DisplayName("Move the snowball up")
    void testMoveSnowballToUp() {
        Snowball snowball = board.getSnowballInPosition(1, 1);
        snowball.move(Direction.UP, board);

        assertEquals(0, snowball.getRow());
        assertEquals(1, snowball.getCol());
    }

    /**
     * Verifies that pushing a SMALL snowball over snow changes it to MID.
     * After the UP move over snow at (0,1), the type should be MID.
     */
    @Test
    @DisplayName("Create average snowball")
    void testCreateAverageSnowball() {
        Snowball snowball = board.getSnowballInPosition(1, 1);
        snowball.move(Direction.UP, board);

        assertEquals(0, snowball.getRow());
        assertEquals(1, snowball.getCol());
        assertEquals(SnowballType.MID, snowball.getType());
    }

    /**
     * Tests that an invalid move (attempting to move off the board or into a block)
     * returns false and does not change position after an initial valid move.
     */
    @Test
    @DisplayName("Test invalid snowball move")
    void testSnowballInvalidMove() {
        Snowball snowball = board.getSnowballInPosition(1, 1);

        // First move UP is valid
        boolean status = snowball.move(Direction.UP, board);
        assertTrue(status);
        assertEquals(0, snowball.getRow());
        assertEquals(1, snowball.getCol());

        // Second move UP is invalid (out of bounds), should return false and remain in place
        status = snowball.move(Direction.UP, board);
        assertFalse(status);
        assertEquals(0, snowball.getRow());
        assertEquals(1, snowball.getCol());
    }

    /**
     * Tests explicit snowball growth logic:
     * SMALL → MID → BIG, and that BIG does not grow further.
     */
    @Test
    @DisplayName("Test Increase snowball type")
    void testIncreaseSnowballType() {
        Snowball small = new Snowball(0, 0, SnowballType.SMALL);
        small.increaseSnowballType();
        assertEquals(SnowballType.MID, small.getType(), "SMALL should grow to MID");

        Snowball mid = new Snowball(0, 0, SnowballType.MID);
        mid.increaseSnowballType();
        assertEquals(SnowballType.BIG, mid.getType(), "MID should grow to BIG");

        Snowball big = new Snowball(0, 0, SnowballType.BIG);
        big.increaseSnowballType();
        assertEquals(SnowballType.BIG, big.getType(), "BIG should remain BIG");
    }

    /**
     * Tests stacking eligibility and resulting stack types:
     * - SMALL on MID → MID_SMALL
     * - SMALL on BIG → BIG_SMALL
     * - MID on BIG → BIG_MID
     * - Invalid stack returns null
     */
    @Test
    @DisplayName("Test if can stack on")
    void testCanStackOn() {
        Snowball small = new Snowball(0, 0, SnowballType.SMALL);
        Snowball mid   = new Snowball(0, 0, SnowballType.MID);
        Snowball big   = new Snowball(0, 0, SnowballType.BIG);

        // Check stacking eligibility
        assertTrue(small.canStackOn(mid));
        assertTrue(small.canStackOn(big));
        assertTrue(mid.canStackOn(big));
        assertFalse(mid.canStackOn(small));
        assertFalse(big.canStackOn(small));

        // Check resulting stack types
        assertEquals(SnowballType.MID_SMALL, small.stackOn(mid));
        assertEquals(SnowballType.BIG_SMALL, small.stackOn(big));
        assertEquals(SnowballType.BIG_MID, mid.stackOn(big));
        assertNull(mid.stackOn(small));
    }
}
