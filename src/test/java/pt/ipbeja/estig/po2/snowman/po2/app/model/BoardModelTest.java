package pt.ipbeja.estig.po2.snowman.po2.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.ipbeja.estig.po2.snowman.app.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoardModelTest {
    Monster monster;
    List<List<PositionContent>> content = new ArrayList<>();
    List<Snowball> snowballs = new ArrayList<>();
    BoardModel board;

    @BeforeEach
    public void setUp() {
        monster = new Monster(1,1);

        for (int i = 0; i < 2; i++) {
            List<PositionContent> row = new ArrayList<>();
            for (int j = 0; j < 2; j++) {
                if(j % 2 == 0) {
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
        board.moveMonster(Direction.LEFT);

        assertEquals(1, monster.getRow());
        assertEquals(0, monster.getCol());

    }

    @Test
    void testPositionContent(){

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                PositionContent element = board.getPositionContent(i,j);
                System.out.print(" | " + element);
            }
            System.out.println(" |");
        }
    }

}
