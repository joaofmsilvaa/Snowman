package pt.ipbeja.estig.po2.snowman.po2.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.ipbeja.estig.po2.snowman.app.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SnowmanTest {
    class BoardModelTest {
        private BoardModel board;
        private Monster monster;

        @BeforeEach
        void setUp() {
            List<List<PositionContent>> grid = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                List<PositionContent> row = new ArrayList<>();
                for (int j = 0; j < 5; j++) {
                    row.add(PositionContent.NO_SNOW);
                }
                grid.add(row);
            }
            monster = new Monster(2, 2);
            List<Snowball> snowballs = new ArrayList<>();
            board = new BoardModel(grid, monster, snowballs);
        }

    }
}