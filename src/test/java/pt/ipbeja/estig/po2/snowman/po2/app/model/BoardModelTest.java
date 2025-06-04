package pt.ipbeja.estig.po2.snowman.po2.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.ipbeja.estig.po2.snowman.app.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BoardModelTest {
    Monster monster;
    List<List<PositionContent>> content = new ArrayList<>();
    List<Snowball> snowballs = new ArrayList<>();
    BoardModel board;

    int rows = 5;
    int cols = 5;

    @BeforeEach
    public void setUp() {
        monster = new Monster(2,0);

        for (int i = 0; i < rows; i++) {
            List<PositionContent> row = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                if(i == 2 && j > 1) {
                    row.add(PositionContent.SNOW);
                }
                else{
                    row.add(PositionContent.NO_SNOW);
                }
            }
            content.add(row);
        }

        Snowball snowball = new Snowball(2,1, SnowballType.MID);
        snowballs.add(snowball);
        snowball = new Snowball(2,2, SnowballType.BIG);
        snowballs.add(snowball);
        board = new BoardModel(content, monster, snowballs);


    }

    @Test
    @DisplayName("Move the monster to the left")
    void testMonsterToTheLeft()
    {
        board.moveMonster(Direction.LEFT);

        assertEquals(1, monster.getRow());
        assertEquals(0, monster.getCol());
    }

    @Test
    @DisplayName("Move the monster up")
    void testMonsterToUp(){
        board.moveMonster(Direction.UP);

        assertEquals(0, monster.getRow());
        assertEquals(1, monster.getCol());
    }

    @Test
    @DisplayName("Test invalid monster move")
    void testMonsterInvalidMove(){
        board.moveMonster(Direction.UP);

        assertEquals(0, monster.getRow());
        assertEquals(1, monster.getCol());

        boolean validMove = board.moveMonster(Direction.UP);

        assertFalse(validMove);
    }

    @Test
    void testPositionContent(){
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                PositionContent element = board.getPositionContent(i,j);
                System.out.print("\t|\t" + element);
            }
            System.out.println("\t|");
        }
    }

    @Test
    @DisplayName("Move snowball to the left")
    void testMoveSnowballToTheLeft(){
        Snowball snowball = board.getSnowballInPosition(0, 1);
        board.moveMonster(Direction.LEFT);

        assertEquals(0, snowball.getRow());
        assertEquals(0, snowball.getCol());

        assertEquals(0, monster.getRow());
        assertEquals(1, monster.getCol());

    }

    @Test
    @DisplayName("Move the snowball up")
    void testMoveSnowballToUp(){
        Snowball snowball = board.getSnowballInPosition(1, 0);
        board.moveMonster(Direction.UP);

        assertEquals(0, snowball.getRow());
        assertEquals(0, snowball.getCol());

        assertEquals(1, monster.getRow());
        assertEquals(0, monster.getCol());
    }

    @Test
    @DisplayName("Create average snowball")
    void testCreateAverageSnowball(){
        Snowball snowball = board.getSnowballInPosition(1, 0);
        assertEquals(1, snowball.getRow());
        assertEquals(0, snowball.getCol());
        assertEquals(SnowballType.SMALL, snowball.getType());

        board.moveMonster(Direction.UP);

        assertEquals(1, monster.getRow());
        assertEquals(0, monster.getCol());

        assertEquals(0, snowball.getRow());
        assertEquals(0, snowball.getCol());
        assertEquals(SnowballType.MID, snowball.getType());
    }

    @Test
    @DisplayName("Create big snowball")
    void testCreateBigSnowball(){
        Snowball snowball = board.getSnowballInPosition(2, 1);
        assertEquals(2, snowball.getRow(), "Initial position row");
        assertEquals(1, snowball.getCol(), "Initial position col");
        assertEquals(SnowballType.SMALL, snowball.getType());

        board.moveMonster(Direction.RIGHT);

        assertEquals(2, snowball.getRow(), "Second position row");
        assertEquals(2, snowball.getCol(), "Second position col");
        assertEquals(SnowballType.MID, snowball.getType());

        board.moveMonster(Direction.RIGHT);

        assertEquals(2, snowball.getRow(), "Third position row");
        assertEquals(3, snowball.getCol(), "Third position col");
        assertEquals(SnowballType.BIG, snowball.getType());
    }

    @Test
    @DisplayName("Maintain big snowball")
    void testMaintainBigSnowball(){
        Snowball snowball = board.getSnowballInPosition(2, 1);
        assertEquals(2, snowball.getRow(), "Initial position row");
        assertEquals(1, snowball.getCol(), "Initial position col");
        assertEquals(SnowballType.BIG, snowball.getType());

        board.moveMonster(Direction.RIGHT);

        assertEquals(2, snowball.getRow(), "Second position row");
        assertEquals(2, snowball.getCol(), "Second position col");
        assertEquals(SnowballType.BIG, snowball.getType());
    }

    @Test
    @DisplayName("Test invalid snowball move")
    void testSnowballInvalidMove(){
        Snowball snowball = board.getSnowballInPosition(1, 0);
        assertEquals(1, snowball.getRow());
        assertEquals(0, snowball.getCol());

        boolean move = board.moveMonster(Direction.UP);

        assertEquals(0, snowball.getRow());
        assertEquals(0, snowball.getCol());
        assertTrue(move);

        move = board.moveMonster(Direction.UP);

        assertEquals(0, snowball.getRow());
        assertEquals(0, snowball.getCol());
        assertFalse(move);


    }

    @Test
    @DisplayName("Test stack mid sized snowball in a big snowball")
    void testAverageBigSnowman(){
        Snowball midBall = board.getSnowballInPosition(2, 1);
        assertEquals(2, midBall.getRow(), "Initial row position");
        assertEquals(1, midBall.getCol(), "Initial col position");
        assertEquals(SnowballType.MID, midBall.getType());

        Snowball bigBall = board.getSnowballInPosition(2, 2);
        assertEquals(2, bigBall.getRow(), "Second row position");
        assertEquals(2, bigBall.getCol(), "Second col position");
        assertEquals(SnowballType.BIG, bigBall.getType());

        assertTrue(midBall.canStackOn(bigBall));

        board.moveMonster(Direction.RIGHT);

        Snowball bigMidBall = board.getSnowballInPosition(2, 2);
        assertEquals(2, bigMidBall.getRow(), "Stacked row position");
        assertEquals(2, bigMidBall.getCol(), "Stacked col position");
        assertEquals(SnowballType.BIG_MID, bigMidBall.getType());
    }

    @Test
    @DisplayName("Test unstack big-mid snowball")
    void testUnstackBigMidSnowball(){
        board.moveMonster(Direction.RIGHT);

        Snowball bigMidBall = board.getSnowballInPosition(2, 2);
        assertEquals(2, bigMidBall.getRow(), "Stacked row position");
        assertEquals(2, bigMidBall.getCol(), "Stacked col position");
        assertEquals(SnowballType.BIG_MID, bigMidBall.getType());

        board.moveMonster(Direction.RIGHT);
        Snowball mid = board.getSnowballInPosition(2,2);
        assertEquals(2, mid.getRow(), "Bottom ball row");
        assertEquals(2, mid.getCol(), "Bottom ball col");

        Snowball big = board.getSnowballInPosition(2,3);
        assertEquals(2, big.getRow(), "Top ball row");
        assertEquals(3, big.getCol(), "Top ball col");

        assertEquals(2, monster.getRow(), "Monster row");
        assertEquals(1, monster.getCol(), "Monster col");
    }

    //Check later
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
