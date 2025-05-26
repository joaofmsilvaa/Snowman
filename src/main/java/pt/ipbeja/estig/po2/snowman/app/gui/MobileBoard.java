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
    public MobileBoard() {
        this.board = new BoardModel();
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
    public void onSnowballMoved(int newRow, int newCol, int oldRow, int oldCol) {
        buttons[oldRow][oldCol].setSnowballVisible(false);

// Se o monstro está na mesma posição que a nova bola
        if (board.getMonster().getRow() == newRow && board.getMonster().getCol() == newCol) {
            buttons[newRow][newCol].setMonsterVisible(true);
        }

// Atualiza a bola na nova posição
        buttons[newRow][newCol].setSnowballVisible(true);
        }




    @Override
    public void onSnowmanCreated(int row, int col) {
        System.out.println("Boneco de neve criado em: " + row + ", " + col);
    }

    @Override
    public void onSnowballStacked(int row, int col, SnowballType newType) {
        buttons[row][col].setSnowballType(newType);
        System.out.println("Empilhamento na posição: " + row + ", " + col + " -> " + newType);
    }

    @Override
    public void onMonsterCleared(int row, int col) {

        buttons[row][col].setMonsterVisible(false);
    }
}
