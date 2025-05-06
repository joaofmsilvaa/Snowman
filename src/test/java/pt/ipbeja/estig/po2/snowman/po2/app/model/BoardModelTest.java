package pt.ipbeja.estig.po2.snowman.po2.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.ipbeja.estig.po2.snowman.app.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class BoardModelTest {
    Monster monster;
    List<List<PositionContent>> content = new ArrayList<>();
    List<Snowball> snowballs = new ArrayList<>();
    BoardModel board;

    int rows = 3;
    int cols = 1;

    @BeforeEach
    public void setUp() {
        monster = new Monster(2,0);

        for (int i = 0; i < rows; i++) {
            List<PositionContent> row = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                if(i == 0) {
                    row.add(PositionContent.SNOW);
                }
                else{
                    row.add(PositionContent.NO_SNOW);
                }
            }
            content.add(row);
        }

        Snowball snowball = new Snowball(1,0, SnowballType.SMALL);
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
    @DisplayName("Test invalid move")
    void testMonsterInvalidMove(){
        board.moveMonster(Direction.UP);

        assertEquals(0, monster.getRow());
        assertEquals(1, monster.getCol());

        boolean validMove = board.moveMonster(Direction.UP);

        assertFalse(validMove);
    }

/*    @Test
    void testPositionContent(){
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                PositionContent element = board.getPositionContent(i,j);
                System.out.print(" | " + element);
            }
            System.out.println(" |");
        }
    }*/

    @Test
    @DisplayName("Move snowball to the left")
    void testMoveSnowballToTheLeft(){
        Snowball snowball = board.snowballInPosition(0, 1);
        board.moveMonster(Direction.LEFT);

        assertEquals(0, snowball.getRow());
        assertEquals(0, snowball.getCol());

        assertEquals(0, monster.getRow());
        assertEquals(1, monster.getCol());

    }

    @Test
    @DisplayName("Move the snowball up")
    void testMoveSnowballToUp(){
        Snowball snowball = board.snowballInPosition(1, 0);
        board.moveMonster(Direction.UP);

        assertEquals(0, snowball.getRow());
        assertEquals(0, snowball.getCol());

        assertEquals(1, monster.getRow());
        assertEquals(0, monster.getCol());
    }

    @Test
    @DisplayName("Create average snowball")
    void testCreateAverageSnowball(){
        Snowball snowball = board.snowballInPosition(1, 0);
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

}
