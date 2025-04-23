package pt.ipbeja.estig.po2.snowman.po2.app.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipbeja.estig.po2.snowman.app.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*public class BoardModelTest {
    private BoardModel board;
    private Monster monster;

    @BeforeEach
    public void setUp() {
        // Criar um tabuleiro 5x5 com neve no centro
        List<List<PositionContent>> grid = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<PositionContent> row = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                row.add(PositionContent.NO_SNOW);
            }
            grid.add(row);
        }
        grid.get(2).set(2, PositionContent.SNOW);

        monster = new Monster(1, 1);
        List<Snowball> snowballs = new ArrayList<>();
        snowballs.add(new Snowball(1, 2, SnowballType.SMALL));

        board = new BoardModel(grid, monster, snowballs);
    }

    @Test
    public void testMonsterToTheLeft() {
        assertTrue(board.moveMonster(Direction.LEFT));
        assertEquals(1, monster.getRow());
        assertEquals(0, monster.getCol());
    }

    @Test
    public void testCreateAverageSnowball() {
        // Mover monstro para empurrar bola pequena para neve
        board.moveMonster(Direction.RIGHT);
        board.moveMonster(Direction.DOWN);

        Snowball snowball = board.getSnowballAt(2, 2);
        assertNotNull(snowball);
        assertEquals(SnowballType.MID, snowball.getType());
    }

    @Test
    public void testCreateBigSnowball() {
        // Configurar teste com bola média
        board.getSnowballs().clear();
        board.getSnowballs().add(new Snowball(1, 2, SnowballType.MID));

        // Mover monstro para empurrar bola média para neve
        board.moveMonster(Direction.RIGHT);
        board.moveMonster(Direction.DOWN);

        Snowball snowball = board.getSnowballAt(2, 2);
        assertEquals(SnowballType.BIG, snowball.getType());
    }

    @Test
    public void testMaintainBigSnowball() {
        // Configurar teste com bola grande
        board.getSnowballs().clear();
        board.getSnowballs().add(new Snowball(1, 2, SnowballType.BIG));

        // Mover monstro para empurrar bola grande para neve
        board.moveMonster(Direction.RIGHT);
        board.moveMonster(Direction.DOWN);

        Snowball snowball = board.getSnowballAt(2, 2);
        assertEquals(SnowballType.BIG, snowball.getType());
    }

    @Test
    public void testAverageBigSnowman() {
        // Configurar teste com bola grande e bola média
        board.getSnowballs().clear();
        board.getSnowballs().add(new Snowball(1, 2, SnowballType.BIG));
        board.getSnowballs().add(new Snowball(2, 2, SnowballType.MID));

        // Mover monstro para empurrar bola média para cima da grande
        board.moveMonster(Direction.RIGHT);
        board.moveMonster(Direction.DOWN);
        board.moveMonster(Direction.UP);

        Snowball snowball = board.getSnowballAt(1, 2);
        assertEquals(SnowballType.BIG_MID, snowball.getType());
    }

    @Test
    public void testCompleteSnowman() {
        // Configurar teste com bola grande, média e pequena
        board.getSnowballs().clear();
        board.getSnowballs().add(new Snowball(1, 2, SnowballType.BIG));
        board.getSnowballs().add(new Snowball(2, 2, SnowballType.MID));
        board.getSnowballs().add(new Snowball(3, 2, SnowballType.SMALL));

        // Mover para empilhar todas
        board.moveMonster(Direction.RIGHT);
        board.moveMonster(Direction.DOWN);
        board.moveMonster(Direction.UP);
        board.moveMonster(Direction.DOWN);
        board.moveMonster(Direction.DOWN);
        board.moveMonster(Direction.UP);

        Snowball snowball = board.getSnowballAt(1, 2);
        assertEquals(SnowballType.COMPLETE, snowball.getType());
        assertEquals(PositionContent.SNOWMAN, board.getPositionContent(1, 2));
    }
}
*/
