package pt.ipbeja.estig.po2.snowman.app.gui;

import javafx.scene.layout.GridPane;
import pt.ipbeja.estig.po2.snowman.app.model.*;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.List;

public class MobileBoard extends GridPane implements View{

    private BoardModel board;
    private EntityButton[][] buttons = new EntityButton[board.SIZE][board.SIZE];

    // Construtor que inicializa o SnowmanBoard com o modelo do tabuleiro
    public MobileBoard(BoardModel boardModel) {
        this.board = boardModel;
        this.buttons = new EntityButton[BoardModel.SIZE][BoardModel.SIZE];

        this.board.setView(this);
        drawBoard();

        this.setFocusTraversable(true);
        this.requestFocus();

        this.setOnKeyPressed(event -> {
            Direction direction = null;

            switch (event.getCode()) {
                case W -> direction = Direction.UP;
                case A -> direction = Direction.LEFT;
                case S -> direction = Direction.DOWN;
                case D -> direction = Direction.RIGHT;
            }

            if (direction != null) {
                board.moveMonster(direction);
            }
        });
    }

    public void drawBoard(){
        this.getChildren().clear();
        for (int row = 0; row < board.SIZE; row++) {
            for (int col = 0; col < board.SIZE; col++) {
                Monster monster = board.getMonster();
                MobileEntity entity = MobileEntity.EMPTY;
                Snowball snowball = board.snowballInPosition(row, col);
                boolean hasMonster = monster.getRow() == row && monster.getCol() == col;

                if(hasMonster){
                    entity = MobileEntity.MONSTER;
                } else if (snowball != null) {
                    entity = MobileEntity.SNOWBALL;
                }

                EntityButton button;

                button = new EntityButton(entity);


                this.add(button, col, row);
                buttons[row][col] = button;
            }
        }
    }

    // Implementações da interface View:
    @Override
    public void onMonsterMoved(int row, int col) {
        Monster monster = board.getMonster();
        int oldRow = monster.getPrevRow();
        int oldCol = monster.getPrevCol();

        buttons[oldRow][oldCol].setMonsterVisible(false);
        buttons[row][col].setMonsterVisible(true);
    }

    @Override
    public void onSnowballMoved(Snowball snowball, int oldRow, int oldCol) {
        buttons[oldRow][oldCol].clearEntity();
      
        if (board.getMonster().getRow() == oldRow && board.getMonster().getCol() == oldCol) {
            buttons[oldRow][oldCol].setMonsterVisible(true);
        }
        int newRow = snowball.getRow();
        int newCol = snowball.getCol();
        buttons[newRow][newCol].setSnowballType(snowball.getType());

        // Garante que o monstro ainda está visível na nova posição
        if (board.getMonster().getRow() == newRow && board.getMonster().getCol() == newCol) {
            buttons[newRow][newCol].setMonsterVisible(true);
        }
    }



    @Override
    public void onSnowmanCreated(int row, int col, SnowballType newType) {
        buttons[row][col].setSnowballType(SnowballType.COMPLETE);
        System.out.println("Boneco de neve criado em: " + row + ", " + col);
    }

    @Override
    public void onSnowballStacked(int row, int col, SnowballType newType) {
        buttons[row][col].setSnowballType(newType);
        if (board.getMonster().getRow() == row && board.getMonster().getCol() == col) {
            buttons[row][col].setMonsterVisible(true);
        }
        System.out.println("Empilhamento na posição: " + row + ", " + col + " -> " + newType);
    }

    @Override
    public void onSnowballUnstacked(Snowball topSnowball, Snowball bottomSnowball) {
        int topRow = topSnowball.getRow();
        int topCol = topSnowball.getCol();
        int bottomRow = bottomSnowball.getRow();
        int bottomCol = bottomSnowball.getCol();

        // Limpa as posições antigas (caso tenham mudado)
        buttons[topRow][topCol].clearEntity();
        buttons[bottomRow][bottomCol].clearEntity();

        // Define os tipos corretos nas novas posições
        buttons[topRow][topCol].setSnowballType(topSnowball.getType());
        buttons[bottomRow][bottomCol].setSnowballType(bottomSnowball.getType());

        // Se o monstro estiver em alguma das posições, reexibe-o
        Monster monster = board.getMonster();
        if (monster.getRow() == topRow && monster.getCol() == topCol) {
            buttons[topRow][topCol].setMonsterVisible(true);
        }
        if (monster.getRow() == bottomRow && monster.getCol() == bottomCol) {
            buttons[bottomRow][bottomCol].setMonsterVisible(true);
        }
    }

    @Override
    public void onMonsterCleared(int row, int col) {

        buttons[row][col].setMonsterVisible(false);
    }
}
