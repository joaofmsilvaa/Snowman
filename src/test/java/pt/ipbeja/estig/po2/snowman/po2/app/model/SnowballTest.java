package pt.ipbeja.estig.po2.snowman.po2.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.ipbeja.estig.po2.snowman.app.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SnowballTest {
    Monster monster;
    List<List<PositionContent>> content = new ArrayList<>();
    List<Snowball> snowballs = new ArrayList<>();
    BoardModel board;

    int rows = 3;
    int cols = 3;

    @BeforeEach
    public void setUp() {
        monster = new Monster(2, 0);

        for (int i = 0; i < rows; i++) {
            List<PositionContent> row = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                if (i == 0 && j == 1) {
                    row.add(PositionContent.SNOW);
                } else {
                    row.add(PositionContent.NO_SNOW);
                }
            }
            content.add(row);
        }

        Snowball snowball = new Snowball(1, 1, SnowballType.SMALL);
        snowballs.add(snowball);
        board = new BoardModel(content, monster, snowballs);

    }

    @Test
    @DisplayName("Move snowball to the left")
    void testMoveSnowballToTheLeft() {
        Snowball snowball = board.snowballInPosition(1, 1);
        snowball.move(Direction.LEFT, board);

        assertEquals(1, snowball.getRow());
        assertEquals(0, snowball.getCol());

    }

    @Test
    @DisplayName("Move the snowball up")
    void testMoveSnowballToUp() {
        Snowball snowball = board.snowballInPosition(1, 1);
        snowball.move(Direction.UP, board);

        assertEquals(0, snowball.getRow());
        assertEquals(1, snowball.getCol());

    }

    @Test
    @DisplayName("Create average snowball")
    void testCreateAverageSnowball() {
        Snowball snowball = board.snowballInPosition(1, 1);
        snowball.move(Direction.UP, board);

        assertEquals(0, snowball.getRow());
        assertEquals(1, snowball.getCol());
        assertEquals(SnowballType.MID, snowball.getType());
    }

    @Test
    @DisplayName("Test invalid snowball move")
    void testSnowballInvalidMove() {
        Snowball snowball = board.snowballInPosition(1, 1);
        boolean status = snowball.move(Direction.UP, board);

        assertEquals(0, snowball.getRow());
        assertEquals(1, snowball.getCol());
        assertTrue(status);

        status = snowball.move(Direction.UP, board);

        assertEquals(0, snowball.getRow());
        assertEquals(1, snowball.getCol());
        assertFalse(status);


    }


    @Test
    @DisplayName("Test Increase snowball type")
    void testIncreaseSnowballType() {
        Snowball small = new Snowball(0, 0, SnowballType.SMALL);
        small.increaseSnowballType();
        assertEquals(SnowballType.MID, small.getType());

        Snowball mid = new Snowball(0, 0, SnowballType.MID);
        mid.increaseSnowballType();
        assertEquals(SnowballType.BIG, mid.getType());

        Snowball big = new Snowball(0, 0, SnowballType.BIG);
        big.increaseSnowballType();
        assertEquals(SnowballType.BIG, big.getType()); // Não deve mudar
    }
    @Test
    @DisplayName("Test if can stack on")
    void testCanStackOn() {
        Snowball small = new Snowball(0, 0, SnowballType.SMALL);
        Snowball mid = new Snowball(0, 0, SnowballType.MID);
        Snowball big = new Snowball(0, 0, SnowballType.BIG);

        assertTrue(small.canStackOn(mid));
        assertTrue(small.canStackOn(big));
        assertTrue(mid.canStackOn(big));
        assertFalse(mid.canStackOn(small));
        assertFalse(big.canStackOn(small));
    }
}
