package pt.ipbeja.estig.po2.snowman.po2.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.ipbeja.estig.po2.snowman.app.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MonsterTest {
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
                if(i == 0) {
                    row.add(PositionContent.SNOW);
                }
                else{
                    row.add(PositionContent.NO_SNOW);
                }
            }
            content.add(row);
        }


        board = new BoardModel(content, monster, snowballs);

    }

    @Test
    @DisplayName("Move the monster to the left")
    void testMonsterToTheLeft()
    {
        monster.move(Direction.LEFT, board);

        assertEquals(2, monster.getRow());
        assertEquals(1, monster.getCol());
    }

    @Test
    @DisplayName("Move the monster up")
    void testMonsterToUp(){
        monster.move(Direction.UP, board);

        assertEquals(1, monster.getRow());
        assertEquals(2, monster.getCol());
    }

    @Test
    @DisplayName("Test invalid monster move")
    void testMonsterInvalidMove(){
        boolean validMove = board.moveMonster(Direction.RIGHT);

        assertEquals(2, monster.getRow());
        assertEquals(2, monster.getCol());

        assertFalse(validMove);
    }

    @Test
    @DisplayName("Test undo move")
    void testUndoMove(){

        monster.move(Direction.RIGHT, board);
        assertEquals(2, monster.getPosition().getRow());
        assertEquals(1, monster.getPosition().getCol());

        monster.undo();
        assertEquals(2, monster.getPosition().getRow());
        assertEquals(0, monster.getPosition().getCol());
    }

    @Test
    @DisplayName("Test redo move")
    void testRedoMove(){

        monster.move(Direction.RIGHT, board);
        assertEquals(2, monster.getPosition().getRow());
        assertEquals(1, monster.getPosition().getCol());

        monster.undo();
        assertEquals(2, monster.getPosition().getRow());
        assertEquals(0, monster.getPosition().getCol());

        monster.redo();
        assertEquals(2, monster.getPosition().getRow());
        assertEquals(1, monster.getPosition().getCol());
    }
}
