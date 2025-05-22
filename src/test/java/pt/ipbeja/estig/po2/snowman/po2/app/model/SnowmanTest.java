package pt.ipbeja.estig.po2.snowman.po2.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipbeja.estig.po2.snowman.app.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SnowmanTest {
    // Não esta 100% funcional
    private BoardModel board;
    private Monster monster;
    List<List<PositionContent>> content = new ArrayList<>();
    private List<Snowball> snowballs = new ArrayList<>();


    @BeforeEach
    void setUp() {
        // 1. Inicializa tabuleiro 5x5 vazio
        List<List<PositionContent>> grid = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<PositionContent> row = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                row.add(PositionContent.NO_SNOW);
            }
            grid.add(row);
        }

        // 2. Configura bolas na coluna central (coluna 2)
        snowballs = new ArrayList<>();
        snowballs.add(new Snowball(4, 2, SnowballType.BIG));    // Base (linha 4)
        snowballs.add(new Snowball(3, 2, SnowballType.MID));    // Meio (linha 3)
        snowballs.add(new Snowball(2, 2, SnowballType.SMALL));  // Topo (linha 2)

        // 3. Monstro posicionado ao lado da bola pequena (linha 2, coluna 1)
        Monster monster = new Monster(2, 1);

        // 4. Cria o BoardModel
        board = new BoardModel(grid, monster, snowballs);
    }

    @Test
    void testCompleteVerticalSnowman() {
        // 1. Monstro empurra SMALL para direita (coluna 2) e depois para baixo
        board.moveMonster(Direction.RIGHT); // Move para coluna 2
        board.moveMonster(Direction.DOWN);  // Empurra SMALL sobre MID

        // Verifica empilhamento SMALL + MID = MID_SMALL
        assertEquals(2, snowballs.size());
        assertEquals(SnowballType.MID_SMALL, snowballs.get(1).getType());

        // 2. Monstro reposicionado ao lado de MID_SMALL
        board.moveMonster(Direction.LEFT);  // Volta para coluna 1
        board.moveMonster(Direction.DOWN);  // Desce para linha 3
        board.moveMonster(Direction.RIGHT); // Move para coluna 2

        // 3. Empurra MID_SMALL para baixo sobre BIG
        board.moveMonster(Direction.DOWN);

        // Verificações finais
        assertEquals(1, snowballs.size(), "Deveria haver apenas o snowman completo");
        assertEquals(SnowballType.COMPLETE, snowballs.get(0).getType());
        assertEquals(PositionContent.SNOWMAN, board.getPositionContent(4, 2));
    }


}