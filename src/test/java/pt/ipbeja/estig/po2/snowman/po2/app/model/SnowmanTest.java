package pt.ipbeja.estig.po2.snowman.po2.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.ipbeja.estig.po2.snowman.app.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for completing a snowman by stacking a SMALL snowball onto a BIG_MID stack.
 *
 * This test verifies that:
 * 1. A SMALL snowball can stack onto a BIG_MID snowball.
 * 2. After stacking, the resulting snowball is of type COMPLETE.
 * 3. The position of the new COMPLETE snowman remains at the base coordinates.
 *
 * @author João Silva
 * @author Paulo Neves
 */
public class SnowmanTest {
    private Monster monster;
    private List<List<PositionContent>> content = new ArrayList<>();
    private List<Snowball> snowballs = new ArrayList<>();
    private BoardModel board;

    private final int rows = 5;
    private final int cols = 5;

    /**
     * Sets up a 5×5 board before each test:
     * - Monster at (2,0).
     * - Snow on row 2, columns 2–4.
     * - A SMALL snowball at (2,1).
     * - A BIG_MID stacked snowball at (2,2).
     */
    @BeforeEach
    public void setUp() {
        monster = new Monster(2, 0);

        // Initialize board terrain: snow only on row 2 for cols > 1
        for (int i = 0; i < rows; i++) {
            List<PositionContent> row = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                row.add((i == 2 && j > 1) ? PositionContent.SNOW : PositionContent.NO_SNOW);
            }
            content.add(row);
        }

        // Place a SMALL snowball at (2,1)
        snowballs.add(new Snowball(2, 1, SnowballType.SMALL));
        // Place a BIG_MID snowball at (2,2)
        snowballs.add(new Snowball(2, 2, SnowballType.BIG_MID));

        board = new BoardModel(content, monster, snowballs);
    }

    /**
     * Tests stacking a SMALL snowball onto a BIG_MID stack to form a COMPLETE snowman.
     *
     * Steps:
     * 1. Verify initial positions and types of the SMALL and BIG_MID snowballs.
     * 2. Ensure SMALL can stack on BIG_MID.
     * 3. Simulate the monster pushing RIGHT to perform the stack.
     * 4. Assert that a COMPLETE snowman appears at the base coordinates (2,2).
     */
    @Test
    @DisplayName("Test stack small sized snowball in a Big_Mid snowball")
    void testCompleteSnowman() {
        // Retrieve and verify the SMALL snowball
        Snowball small = board.getSnowballInPosition(2, 1);
        assertEquals(2, small.getRow(), "Initial SMALL row position");
        assertEquals(1, small.getCol(), "Initial SMALL col position");
        assertEquals(SnowballType.SMALL, small.getType(), "Initial SMALL type");

        // Retrieve and verify the BIG_MID snowball
        Snowball bigMid = board.getSnowballInPosition(2, 2);
        assertEquals(2, bigMid.getRow(), "Initial BIG_MID row position");
        assertEquals(2, bigMid.getCol(), "Initial BIG_MID col position");
        assertEquals(SnowballType.BIG_MID, bigMid.getType(), "Initial BIG_MID type");

        // Confirm stacking eligibility
        assertTrue(small.canStackOn(bigMid), "SMALL should be able to stack on BIG_MID");

        // Perform the stack by moving the monster right
        board.moveMonster(Direction.RIGHT);

        // Verify that the resulting snowman is COMPLETE at (2,2)
        Snowball snowman = board.getSnowballInPosition(2, 2);
        assertEquals(2, snowman.getRow(), "Snowman row position");
        assertEquals(2, snowman.getCol(), "Snowman col position");
        assertEquals(SnowballType.COMPLETE, snowman.getType(), "Resulting type should be COMPLETE");
    }
}