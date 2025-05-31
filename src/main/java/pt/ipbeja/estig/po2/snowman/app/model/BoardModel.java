package pt.ipbeja.estig.po2.snowman.app.model;

import pt.ipbeja.estig.po2.snowman.app.model.interfaces.MoveListener;
import pt.ipbeja.estig.po2.snowman.app.model.interfaces.View;

import java.util.ArrayList;
import java.util.List;

/**
 * BoardModel gere a logica toda do jogo:
 * - Conteúdo do tabuleiro (boardContent)
 * - Posições do monstro e das bolas de neve
 * - Contagem de movimentos e nome do jogador
 * - Registo de pontuações através do ScoreManager
 *
 * É também responsável por notificar a View e o MoveListener quando ocorrem
 * eventos relevantes (movimento do monstro, empurrar bolas, empilhar, criar boneco).
 */

public class BoardModel {
    private List<List<PositionContent>> boardContent;
    private Monster monster;
    private List<Snowball> snowballs;
    private View view;
    private MoveListener moveListener;
    private int moveCount = 0;
    private String playerName;
    private final ScoreManager scoreManager = new ScoreManager();


    public BoardModel() {
        boardContent = new ArrayList<>();
        snowballs = new ArrayList<>();
        startGame(); /// Inicializa tabuleiro, monstro e bolas de neve
    }


    /// Construtor para os atributos da class BoardModel
    public BoardModel(List<List<PositionContent>> content, Monster monster, List<Snowball> snowballs) {
        this.monster = monster;
        this.snowballs = snowballs;
        this.boardContent = content;
    }

    /// Define que a view sera notificada
    public void setView(View view) {

        this.view = view;
    }

    /// Define o MoveListener para receber eventos de movimento
    public void setMoveListener(MoveListener moveListener) {

        this.moveListener = moveListener;
    }

    public void recordScore(String playerName, int moves, String levelName) {
        Score s = new Score(playerName, moves, levelName);
        scoreManager.addScore(s);
    }

    public List<Score> getTopScores() {
        return scoreManager.getTopScores();
    }

    /// Configura o estado inicial do jogo (tabuleiro 5x5 e 3 bolas pequenas)
    public void startGame() {
        monster = new Monster(2, 0);

        for (int i = 0; i < 5; i++) {
            List<PositionContent> row = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                if (i == 0) {
                    row.add(PositionContent.SNOW);
                } else {
                    row.add(PositionContent.NO_SNOW);
                }
            }
            boardContent.add(row);
        }

        /// Coloca tres bolas pequenas na linha 2, colunas 1, 2 e 3
        snowballs.add(new Snowball(2, 1, SnowballType.SMALL));
        snowballs.add(new Snowball(2, 2, SnowballType.SMALL));
        snowballs.add(new Snowball(2, 3, SnowballType.SMALL));
    }

    /// Numero de linhas do tabuleiro
    public int getRowCount() {

        return boardContent.size();
    }

    /// Numero de colunas do tabuleiro

    public int getColCount() {

        return boardContent.isEmpty() ? 0 : boardContent.get(0).size();
    }

    /// Devolve o conteudo de uma célula específica do tabuleiro
    public PositionContent getPositionContent(int row, int col) {

        return boardContent.get(row).get(col);
    }

    /// Obtem a posiçao do monstro
    public Monster getMonster() {

        return monster;
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

    /// Verifica se uma posição é válida (dentro dos limites e não é BLOCK)
    public boolean validPosition(int newRow, int newCol) {
        try {
            return getPositionContent(newRow, newCol) != PositionContent.BLOCK;
        } catch (Exception e) {
            return false;
        }
    }

    /// Procura uma bola de neve na posição indicada
    public Snowball snowballInPosition(int row, int col) {
        for (Snowball snowball : snowballs) {
            if (snowball.getRow() == row && snowball.getCol() == col) {
                return snowball;
            }
        }
        return null;
    }

    /// Verifica se existe uma bola de neve diretamente à frente do monstro
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

    /// Move o monstro e, se for possivel, empurra a bola em frente
    public boolean moveMonster(Direction direction) {
        Position oldPosition = new Position(monster.getRow(), monster.getCol());

        /// Verifica se há bola em frente ao monstro
        Snowball snowball = snowballInFrontOfMonster(direction);
        Position oldSnowballPosition = new Position(-1,-1);
        if (snowball != null) {
            oldSnowballPosition = new Position(snowball.getRow(), snowball.getCol());
        }

        /// O metodo monster.move valida para mover ou empurrar bola)
        boolean moved = monster.move(direction, this);

        if (moved && view != null) {
            /// Atualiza contagem de movimentos
            incrementMoveCount();

            Position currentPosition = new Position(monster.getRow(), monster.getCol());

            /// Notifica a View para redesenhar monstro e bola se necessário
            view.onMonsterCleared(oldPosition);
            view.onMonsterMoved(currentPosition);


            /// Se havia bola em frente, notifica a View que essa bola mudou
            if (snowball != null) {
                view.onSnowballMoved(snowball, oldSnowballPosition);
            }

            /// Notifica listener de movimento externo
            moveListener.onMove(oldPosition,currentPosition);
        }

        return moved;
    }

    /// Move uma bola de neve numa determinada direção
    public boolean moveSnowball(Direction direction, Snowball snowball) {

        return snowball.move(direction, this);
    }

    /// Tenta empilhar duas bolas (top sobre bottom), atualiza estado e notifica a View
    public boolean tryStackSnowballs(Snowball top, Snowball bottom) {
        SnowballType newType = top.stackOn(bottom);
        if (newType == null) return false;

        /// Remove as bolas originais e cria a nova bola stack na posição de bottom
        snowballs.remove(top);
        snowballs.remove(bottom);
        Snowball stacked = new Snowball(bottom.getRow(), bottom.getCol(), newType);
        snowballs.add(stacked);

        Position bottomPos = new Position(bottom.getRow(), bottom.getCol());
        if (view != null) {
            view.onSnowballStacked(bottomPos, newType);
        }

        /// Se resultou num boneco completo, atualiza o tabuleiro e grava ficheiro
        if (newType == SnowballType.COMPLETE) {
            boardContent.get(bottom.getRow()).set(bottom.getCol(), PositionContent.SNOWMAN);
            if (view != null) {
                view.onSnowmanCreated(bottomPos, newType);
                Position snowmanPos = new Position(bottom.getRow() + 1, bottom.getCol()); // Offset da coluna de coordenadas
                storeGameDetails(snowmanPos);
            }
        }

        return true;
    }

    /// Grava informações do jogo num ficheiro quando o Snowman é criado
    public void storeGameDetails(Position snowmanPosition) {
        SnowmanFile snowmanFile = new SnowmanFile();
        snowmanFile.setFilename("Snowman" + snowmanFile.getCurrentDate() + ".txt");
        snowmanFile.createFile();
        String[] moves = {"2a -> 2b"};
        snowmanFile.writeFile("map name", moves, getMoveCount(),snowmanPosition );
    }

    /// Desfaz o stack de bolas, validando a posição e notificando a View
    public boolean unstackSnowballs(Snowball stacked, Direction direction) {
        Snowball bottom = getBottom(stacked);
        Snowball top = getTop(stacked, direction);

        if (validPosition(top.getRow(), top.getCol())) {
            snowballs.remove(stacked);
            snowballs.add(top);
            snowballs.add(bottom);

            if (view != null) {
                view.onSnowballUnstacked(top, bottom);
            }

            return true;
        }

        return false;
    }

    /// Verifica se a bola é um stack (parcial)
    public boolean isSnowballStack(Snowball snowball) {

        return snowball.isSnowballStack();
    }

    /// Verifica se existe um boneco completo na posição indicada e notifica a View
    void checkCompleteSnowman(Position snowmanPos) {
        Snowball base = snowballInPosition(snowmanPos.getRow(), snowmanPos.getCol());
        if (base == null || base.getType() != SnowballType.BIG_MID) return;

        Snowball top = snowballInPosition(snowmanPos.getRow() - 1, snowmanPos.getCol());
        if (top != null && top.getType() == SnowballType.SMALL) {
            snowballs.remove(base);
            snowballs.remove(top);

            Snowball snowman = new Snowball(snowmanPos.getRow(), snowmanPos.getCol(), SnowballType.COMPLETE);
            snowballs.add(snowman);

            boardContent.get(snowmanPos.getRow()).set(snowmanPos.getCol(), PositionContent.SNOWMAN);

            if (view != null) {
                view.onSnowmanCreated(snowmanPos, SnowballType.COMPLETE);
            }
        }
    }

    /// A partir do stack, devolve a bola de baixo
    public Snowball getBottom(Snowball stack) {
        return switch (stack.getType()) {
            case MID_SMALL -> new Snowball(stack.getRow(), stack.getCol(), SnowballType.MID);
            case BIG_MID, BIG_SMALL -> new Snowball(stack.getRow(), stack.getCol(), SnowballType.BIG);
            default -> null;
        };
    }

    ///  A partir do stack e direção, devolve a bola de topo que sairá ao desfazer
    public Snowball getTop(Snowball stack, Direction direction) {
        Position position = new Position(stack.getRow(), stack.getCol()).changePosition(direction);
        SnowballType type = switch (stack.getType()) {
            case MID_SMALL, BIG_SMALL -> SnowballType.SMALL;
            case BIG_MID -> SnowballType.MID;
            default -> null;
        };
        return type == null ? null : new Snowball(position.getRow(), position.getCol(), type);
    }


}
