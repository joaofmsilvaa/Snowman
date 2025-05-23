package pt.ipbeja.estig.po2.snowman.app.model;

import java.util.ArrayList;
import java.util.List;

public class BoardModel {
    private List<List<PositionContent>> boardContent;
    private Monster monster;
    private List<Snowball> snowballs;
    private View view;
    public static final int SIZE = 5;

    public BoardModel() {
        boardContent = new ArrayList<>();
        snowballs = new ArrayList<>();
        startGame();
    }

    public BoardModel(List<List<PositionContent>> content,Monster monster, List<Snowball> snowballs) {
        this.monster = monster;
        this.snowballs = snowballs;
        this.boardContent = content;
    }

    public void setView(View view){
        this.view = view;
    }

    public void startGame(){
        monster = new Monster(2,0);

        for (int i = 0; i < SIZE; i++) {
            List<PositionContent> row = new ArrayList<>();
            for (int j = 0; j < SIZE; j++) {
                if(i == 0) {
                    row.add(PositionContent.SNOW);
                }
                else{
                    row.add(PositionContent.NO_SNOW);
                }
            }
            boardContent.add(row);
        }

        Snowball snowball = new Snowball(1,0, SnowballType.SMALL);
        snowballs.add(snowball);

        snowball = new Snowball(3,1, SnowballType.SMALL);
        snowballs.add(snowball);

        snowball = new Snowball(4,3, SnowballType.SMALL);
        snowballs.add(snowball);
    }

    public PositionContent getPositionContent(int row, int col) {
        return boardContent.get(row).get(col);
    }

    public boolean validPosition(int newRow, int newCol) {
        try{
            return getPositionContent(newRow, newCol) != PositionContent.BLOCK;
        } catch (Exception e) {
            return false;
        }
    }

    public Snowball snowballInPosition(int row, int col) {
        for (Snowball snowball : snowballs) {
            if(snowball.getRow() == row && snowball.getCol() == col) {
                return snowball;
            }
        }

        return null;
    }

    public boolean moveMonster(Direction direction) {
        return monster.move(direction, this);
    }

    public boolean moveSnowball(Direction direction, Snowball snowball) {
        return snowball.move(direction, this);
    }

    //Tentar empelhar uma bola sobre outra
    public boolean tryStackSnowballs(Snowball top, Snowball bottom) {
        SnowballType newType = top.stackOn(bottom);
        if (newType == null) {
            return false; // Não pode empilhar
        }

        // Remove as bolas individuais
        snowballs.remove(top);
        snowballs.remove(bottom);

        // Cria a nova bola combinada
        Snowball stacked = new Snowball(bottom.getRow(), bottom.getCol(), newType);
        snowballs.add(stacked);

        // Notifica a view sobre o empilhamento
        if (view != null) {
            view.onSnowballStacked(bottom.getRow(), bottom.getCol(), newType);
        }
        // Verifica se formou um snowman completo
        if (newType == SnowballType.BIG_MID) {
            checkCompleteSnowman(bottom.getRow(), bottom.getCol());
        }

        return true;
    }


     //Verifica e executa empilhamentos após movimento
    private void checkStackingAfterMove(int row, int col) {
        Snowball bottom = snowballInPosition(row, col);
        if (bottom == null) {
            return;
        }

        // Verifica se há bola acima que pode ser empilhada
        Snowball top = snowballInPosition(row - 1, col);
        if (top != null && top.canStackOn(bottom)) {
            tryStackSnowballs(top, bottom);
        }
    }


    // Metodo para verificar se formou um snowman completo
    void checkCompleteSnowman(int row, int col) {
        Snowball base = snowballInPosition(row, col);
        if (base == null || base.getType() != SnowballType.BIG_MID) return;

        Snowball top = snowballInPosition(row-1, col);
        if (top != null && top.getType() == SnowballType.SMALL) {
            // Remove as bolas individuais
            snowballs.remove(base);
            snowballs.remove(top);

            // Cria o snowman completo
            Snowball snowman = new Snowball(row, col, SnowballType.COMPLETE);
            snowballs.add(snowman);

            // Atualiza o tabuleiro
            boardContent.get(row).set(col, PositionContent.SNOWMAN);

            // Notifica a view
            if (view != null) {
                view.onSnowmanCreated(row, col);
            }
        }
    }

    public Monster getMonster() {
        return monster;
    }

    public List<Snowball> getSnowballs() {
        return snowballs;
    }
}

