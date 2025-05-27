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

        Snowball snowball = new Snowball(2,1, SnowballType.MID);
        snowballs.add(snowball);

        snowball = new Snowball(2,2, SnowballType.BIG);
        snowballs.add(snowball);

        snowball = new Snowball(2,3, SnowballType.SMALL);
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

    public Snowball snowballInFrontOfMonster(Direction direction) {
        int row = monster.getRow();
        int col = monster.getCol();

        return switch (direction) {
            case UP -> snowballInPosition(row - 1, col);
            case DOWN -> snowballInPosition(row + 1, col);
            case LEFT -> snowballInPosition(row, col - 1);
            case RIGHT -> snowballInPosition(row, col + 1);
        };
    }

    public boolean moveMonster(Direction direction) {
        int oldRow = monster.getRow();
        int oldCol = monster.getCol();

        // Guarda a referência da bola antes do movimento
        Snowball snowball = snowballInFrontOfMonster(direction);
        int oldSnowballRow = -1, oldSnowballCol = -1;
        if (snowball != null) {
            oldSnowballRow = snowball.getRow();
            oldSnowballCol = snowball.getCol();
        }

        // Faz o movimento completo (inclui mover a snowball e o monstro)
        boolean moved = monster.move(direction, this);

        if (moved && view != null) {
            // Atualiza a UI corretamente
            view.onMonsterCleared(oldRow, oldCol);
            view.onMonsterMoved(monster.getRow(), monster.getCol());

            // Atualiza a snowball apenas na UI (não move de novo!)
            if (snowball != null) {
                view.onSnowballMoved(snowball, oldSnowballRow, oldSnowballCol);
            }
        }

        return moved;
    }

    public boolean moveSnowball(Direction direction, Snowball snowball) {
        return snowball.move(direction, this);
    }

    //Tentar empelhar uma bola sobre outra
    public boolean tryStackSnowballs(Snowball top, Snowball bottom) {
        SnowballType newType = top.stackOn(bottom);
        System.out.println("Resultado do empilhamento: " + newType);

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
        if (newType == SnowballType.COMPLETE) {
            boardContent.get(bottom.getRow()).set(bottom.getCol(), PositionContent.SNOWMAN);
            if (view != null) {
                view.onSnowmanCreated(bottom.getRow(), bottom.getCol(), newType);
            }
        }

        return true;
    }

    public boolean unstackSnowballs(Snowball stacked, Direction direction) {
        Snowball bottom = getBottom(stacked);
        Snowball top = getTop(stacked, direction);

        // Se a bola de cima não for movida para um campo inválido adiciona as bolas (executa o unstack)
        if(validPosition(top.getRow(), top.getCol())) {
            snowballs.remove(stacked);
            snowballs.add(top);
            snowballs.add(bottom);

            if(view != null){
                view.onSnowballUnstacked(top, bottom);
            }
        }
        else{
            return false;
        }


        return true;
    }

    public boolean isSnowballStack(Snowball snowball) {
        return snowball.isSnowballStack();
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
                view.onSnowmanCreated(row, col, SnowballType.COMPLETE);
            }
        }
    }

    public Snowball getBottom(Snowball stack){
        switch (stack.getType()){
            case MID_SMALL -> {
                return new Snowball(stack.getRow(),stack.getCol(), SnowballType.MID);
            }
            case BIG_MID, BIG_SMALL -> {
                return new Snowball(stack.getRow(),stack.getCol(), SnowballType.BIG);
            }
        }

        return null;
    }

    public Snowball getTop(Snowball stack, Direction direction) {
        SnowballType type = stack.getType();
        Position position = new Position(stack.row, stack.col);
        position = position.changePosition(direction);
        int newRow = position.getRow(), newCol = position.getCol();

        switch (stack.getType()){
            case MID_SMALL, BIG_SMALL -> {
                type = SnowballType.SMALL;
            }
            case BIG_MID -> {
                type = SnowballType.MID;
            }
        }

        // Retorna uma nova bola de neve com a posição e tipo
        return new Snowball(newRow, newCol, type);
    }

    public Monster getMonster() {
        return monster;
    }

}

