package pt.ipbeja.estig.po2.snowman.po2.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.ipbeja.estig.po2.snowman.app.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SnowmanTest {
    Monster monster;
    List<List<PositionContent>> content = new ArrayList<>();
    List<Snowball> snowballs = new ArrayList<>();
    BoardModel board;

    int rows = 5;
    int cols = 5;

    @BeforeEach
    public void setUp() {
        monster = new Monster(2, 0);

        for (int i = 0; i < rows; i++) {
            List<PositionContent> row = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                if (i == 2 && j > 1) {
                    row.add(PositionContent.SNOW);
                } else {
                    row.add(PositionContent.NO_SNOW);
                }
            }
            content.add(row);
        }

        Snowball snowball = new Snowball(2, 1, SnowballType.SMALL);
        snowballs.add(snowball);
        snowball = new Snowball(2, 2, SnowballType.BIG_MID);
        snowballs.add(snowball);
        board = new BoardModel(content, monster, snowballs);
    }

    @Test
    @DisplayName("Test stack small sized snowball in a Big_Mid snowball")
    void testCompleteSnowman(){
        Snowball small = board.getSnowballInPosition(2, 1);
        assertEquals(2, small.getRow(), "Initial row position");
        assertEquals(1, small.getCol(), "Initial col position");
        assertEquals(SnowballType.SMALL, small.getType());

        Snowball BigMidball = board.getSnowballInPosition(2, 2);
        assertEquals(2, BigMidball.getRow(), "Second row position");
        assertEquals(2, BigMidball.getCol(), "Second col position");
        assertEquals(SnowballType.BIG_MID, BigMidball.getType());

        assertTrue(small.canStackOn(BigMidball));

        board.moveMonster(Direction.RIGHT);

        Snowball snowman = board.getSnowballInPosition(2, 2);
        assertEquals(2, snowman.getRow(), "Stacked row position");
        assertEquals(2, snowman.getCol(), "Stacked col position");
        assertEquals(SnowballType.COMPLETE, snowman.getType());
    }
}
