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
        public SnowballType getType() { return type; }
        public void setType(SnowballType type) { this.type = type; }


        public void increaseSnowballType() {
            switch (type) {
                case SMALL -> setType(SnowballType.MID);
                case MID -> setType(SnowballType.BIG);
            }
        }

    /**
         * Implementação do movimento da bola de neve
         * @param direction - Direção do movimento
         * @param board - Referência ao tabuleiro
         * @return true se o movimento foi bem sucedido
         */
        @Override
        public boolean move(Direction direction, BoardModel board) {
            int newRow = row;
            int newCol = col;

            switch (direction) {
                case UP:    newRow--; break;
                case DOWN:  newRow++; break;
                case LEFT:  newCol--; break;
                case RIGHT: newCol++; break;
            }

            if (!board.validPosition(newRow, newCol)) {
                return false;
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

}
