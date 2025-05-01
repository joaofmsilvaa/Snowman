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

    @Override
    public void move(int newRow, int newCol) {

    }

    /**
         * Implementação do movimento da bola de neve
         * @param direction - Direção do movimento
         * @param board - Referência ao tabuleiro
         * @return true se o movimento foi bem sucedido
         */
        @Override
        public boolean move(Direction direction, BoardModel board) {
            // Lógica de movimento e interação com o tabuleiro
            return false;
        }

}
