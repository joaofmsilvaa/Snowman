package pt.ipbeja.estig.po2.snowman.app.model;

public class Snowball extends MobileElement {
    private SnowballType type;

    /**
     * Construtor
     * @param type - Tipo/tamanho inicial
     */
    public Snowball(int row, int col, SnowballType type) {
        super(row, col);
        this.type = type;
    }

    // Getters e setters
    public SnowballType getType() {
        return type;
    }

    public void setType(SnowballType type) {
        this.type = type;
    }


    public void increaseSnowballType() {
        switch (type) {
            case SMALL -> setType(SnowballType.MID);
            case MID -> setType(SnowballType.BIG);
        }
    }


    //Verifica se esta bola pode ser empilhada sobre outra bola
    public boolean canStackOn(Snowball other) {
        switch (this.type) {
            case SMALL:
                return other.type == SnowballType.MID ||
                        other.type == SnowballType.BIG || other.type == SnowballType.BIG_MID;
            case MID:
                return other.type == SnowballType.BIG;

            case BIG: // BIG não pode ser empilhada
                return false;
        }
        return false;
    }

    // Dar Stack as bolas de neve
    public SnowballType stackOn(Snowball other) {
        if (!canStackOn(other)) {
            return null; // Não pode empilhar
        }

        if (this.type == SnowballType.SMALL && other.type == SnowballType.MID) {
            return SnowballType.MID_SMALL;
        }
        else if (this.type == SnowballType.SMALL && other.type == SnowballType.BIG) {
            return SnowballType.BIG_SMALL;
        }
        else if (this.type == SnowballType.MID && other.type == SnowballType.BIG) {
            return SnowballType.BIG_MID;
        }
        else if (this.type == SnowballType.SMALL && other.type == SnowballType.BIG_MID) {
            return SnowballType.COMPLETE;
        }

        return null;
    }


    /**
     * Implementação do movimento da bola de neve
     * @param direction - Direção do movimento
     * @param board - Referência ao tabuleiro
     * @return true se o movimento foi bem sucedido
     */
    @Override
    public boolean move(Direction direction, BoardModel board) {
        // Snowman completo não pode mover
        if (this.type == SnowballType.COMPLETE) {
            return false;
        }

        Position position = new Position(row, col);
        position = position.changePosition(direction);
        int newRow = position.getRow();
        int newCol = position.getCol();

        if (!board.validPosition(newRow, newCol)) {
            return false;
        }
        // Verifica se há outra bola no destino
        Snowball target = board.snowballInPosition(newRow, newCol);
        if (target != null) {

            return board.tryStackSnowballs(this, target);
        }

        PositionContent destination = board.getPositionContent(newRow, newCol);

        if (destination == PositionContent.BLOCK) {
            return false;
        }

        if (destination == PositionContent.SNOW) {
            increaseSnowballType();
        }

        row = newRow;
        col = newCol;
        return true;
    }


    public boolean isSnowballStack(){
        SnowballType type = this.getType();
        if(type == SnowballType.BIG_MID || type == SnowballType.MID_SMALL || type == SnowballType.BIG_SMALL) {
            return true;
        }

        return false;
    }

}