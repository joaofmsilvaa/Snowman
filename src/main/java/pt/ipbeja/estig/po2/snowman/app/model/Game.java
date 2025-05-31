package pt.ipbeja.estig.po2.snowman.app.model;

public class Game {
    private int moveCount = 0;
    private String playerName;

    public Game(String playerName) {
        this.playerName = playerName;
        this.moveCount = 0;
    }

    /// Retorna o número de movimentos realizados
    public int getMoveCount() {
        return moveCount;
    }

    /// Incrementa o contador de movimentos
    public void incrementMoveCount() {
        moveCount++;
    }

    /// Retorna o nome do jogador atual
    public String getPlayerName() {
        return playerName;
    }

    /// Define o nome do jogado
    public void setPlayerName(String name) {
        this.playerName = name;
    }
}
