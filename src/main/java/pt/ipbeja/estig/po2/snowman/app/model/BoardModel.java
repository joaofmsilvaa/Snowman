package pt.ipbeja.estig.po2.snowman.app.model;

import pt.ipbeja.estig.po2.snowman.app.model.interfaces.BoardListener;
import pt.ipbeja.estig.po2.snowman.app.model.interfaces.MoveListener;
import pt.ipbeja.estig.po2.snowman.app.model.interfaces.ScoreListener;
import pt.ipbeja.estig.po2.snowman.app.model.interfaces.View;

import java.util.ArrayList;
import java.util.List;

/**
 * BoardModel manages all game logic with improved undo/redo functionality
 */
public class BoardModel {
    private List<List<PositionContent>> boardContent;
    private Monster monster;
    private List<Snowball> snowballs;
    private View view;
    private MoveListener moveListener;
    private BoardListener boardListener;
    private Game game;
    private ScoreListener scoreListener;
    private String mapFileName;

    // Undo/Redo system
    private int currentStateIndex = -1;
    private List<GameState> history = new ArrayList<>();
    private static final int MAX_HISTORY_SIZE = 50;
    /**
     * Default constructor: initializes data structures and calls startGame()
     * to set up a 5×5 board and initial snowballs.
     */
    public BoardModel() {
        boardContent = new ArrayList<>();
        snowballs = new ArrayList<>();
        startGame();
        // Salvar estado inicial APÓS configurar o jogo
        saveInitialState();
    }

    /**
     * Constructor for creating a BoardModel from existing content.
     */
    public BoardModel(List<List<PositionContent>> content, Monster monster, List<Snowball> snowballs) {
        this.monster = monster;
        this.snowballs = new ArrayList<>(snowballs); // Criar cópia defensiva
        this.boardContent = new ArrayList<>();
        // Criar cópia profunda do conteúdo
        for (List<PositionContent> row : content) {
            this.boardContent.add(new ArrayList<>(row));
        }
        saveInitialState();
    }

    public void setView(View view) {
        this.view = view;
    }

    public void setMoveListener(MoveListener moveListener) {
        this.moveListener = moveListener;
    }

    public void setBoardListener(BoardListener boardListener) {
        this.boardListener = boardListener;
    }

    public void setMapFileName(String mapFileName) {
        this.mapFileName = mapFileName;
    }

    public void setScoreListener(ScoreListener listener) {
        this.scoreListener = listener;
    }

    public void setPlayerName(String playerName) {
        if (game != null) {
            game.setPlayerName(playerName);
        }
    }

    /**
     * Sets up the initial game state with proper initialization
     */
    public void startGame() {
        // Limpar estado anterior
        boardContent.clear();
        snowballs.clear();

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

        snowballs.add(new Snowball(2, 1, SnowballType.SMALL));
        snowballs.add(new Snowball(2, 2, SnowballType.SMALL));
        snowballs.add(new Snowball(2, 3, SnowballType.SMALL));

        // Resetar histórico
        history.clear();
        currentStateIndex = -1;
    }

    /**
     * Salva o estado inicial do jogo (deve ser chamado após startGame())
     */
    private void saveInitialState() {
        history.clear();
        currentStateIndex = -1;
        saveState();
    }

    public void setGame(Game game) {
        this.game = game;
        if (game != null && game.getPlayerName() != null) {
            this.game.setPlayerName(game.getPlayerName());
        }
    }

    // Getters (mantidos como no original)
    public String getMapFileName() {
        return this.mapFileName;
    }

    public int getRowCount() {
        return boardContent.size();
    }

    public int getColCount() {
        return boardContent.isEmpty() ? 0 : boardContent.get(0).size();
    }

    public PositionContent getPositionContent(int row, int col) {
        if (row < 0 || row >= getRowCount() || col < 0 || col >= getColCount()) {
            return PositionContent.BLOCK; // Fora dos limites = bloco
        }
        return boardContent.get(row).get(col);
    }

    public Monster getMonster() {
        return monster;
    }

    public String getPlayerName() {
        return game != null ? game.getPlayerName() : "";
    }

    public int getMoveCount() {
        return game != null ? game.getMoveCount() : 0;
    }

    public Game getGame() {
        return this.game;
    }

    public void setMapName(String mapName) {
        if (game != null) {
            game.setMapName(mapName);
        }
    }

    /**
     * Improved position validation
     */
    public boolean validPosition(int newRow, int newCol) {
        if (newRow < 0 || newRow >= getRowCount() || newCol < 0 || newCol >= getColCount()) {
            return false;
        }
        return getPositionContent(newRow, newCol) != PositionContent.BLOCK;
    }

    public boolean canUnstack(int newRow, int newCol) {
        return validPosition(newRow, newCol) && getSnowballInPosition(newRow, newCol) == null;
    }

    public Snowball getSnowballInPosition(int row, int col) {
        for (Snowball snowball : snowballs) {
            if (snowball.getRow() == row && snowball.getCol() == col) {
                return snowball;
            }
        }
        return null;
    }

    public Snowball snowballInFrontOfMonster(Direction direction) {
        int row = monster.getRow();
        int col = monster.getCol();

        return switch (direction) {
            case UP -> getSnowballInPosition(row - 1, col);
            case DOWN -> getSnowballInPosition(row + 1, col);
            case LEFT -> getSnowballInPosition(row, col - 1);
            case RIGHT -> getSnowballInPosition(row, col + 1);
        };
    }

    /**
     * Improved moveMonster with proper state management
     */
    public boolean moveMonster(Direction direction) {
        // Salvar estado ANTES da tentativa de movimento
        Position oldPosition = new Position(monster.getRow(), monster.getCol());
        Snowball snowball = snowballInFrontOfMonster(direction);
        Position oldSnowballPosition = null;

        if (snowball != null) {
            oldSnowballPosition = new Position(snowball.getRow(), snowball.getCol());
        }

        // Tentar mover o monstro
        boolean moved = monster.move(direction, this);

        if (moved) {
            // Salvar estado APÓS movimento bem-sucedido
            saveState();

            Position currentPosition = new Position(monster.getRow(), monster.getCol());

            // Notificar view
            if (view != null) {
                view.onMonsterCleared(oldPosition);
                view.onMonsterMoved(currentPosition);

                if (snowball != null && oldSnowballPosition != null) {
                    view.onSnowballMoved(snowball, oldSnowballPosition);
                }
            }

            // Notificar listeners
            if (moveListener != null) {
                moveListener.onMove(oldPosition, currentPosition);
            }
            if (game != null) {
                game.onMove(oldPosition, currentPosition);
            }
        }

        return moved;
    }

    public boolean moveSnowball(Direction direction, Snowball snowball) {
        return snowball.move(direction, this);
    }

    public boolean tryStackSnowballs(Snowball top, Snowball bottom) {
        SnowballType newType = top.stackOn(bottom);
        if (newType == null) return false;

        // Remove as bolas originais e adiciona a empilhada
        snowballs.remove(top);
        snowballs.remove(bottom);
        Snowball stacked = new Snowball(bottom.getRow(), bottom.getCol(), newType);
        snowballs.add(stacked);

        Position bottomPos = new Position(bottom.getRow(), bottom.getCol());
        if (view != null) {
            view.onSnowballStacked(bottomPos, newType);
        }

        // Se completou um boneco de neve
        if (newType == SnowballType.COMPLETE) {
            boardContent.get(bottom.getRow()).set(bottom.getCol(), PositionContent.SNOWMAN);

            if (view != null) {
                view.onSnowmanCreated(bottomPos, newType);
                Position snowmanPos = new Position(bottom.getRow() + 1, bottom.getCol());
                storeGameDetails(snowmanPos);
            }
        }

        return true;
    }

    public void storeGameDetails(Position snowmanPosition) {
        SnowmanFile snowmanFile = new SnowmanFile();
        snowmanFile.setFilename("Snowman" + snowmanFile.getCurrentDate() + ".txt");
        snowmanFile.createFile();

        snowmanFile.writeFile(
                game != null ? game.getMapName() : "Unknown",
                generateMapString(),
                game != null ? game.getMoveHistoryArray() : new String[0],
                game != null ? game.getMoveCount() : 0,
                game != null ? game.getPlayerName() : "Unknown",
                snowmanPosition
        );

        // Criar e notificar pontuação
        if (game != null && scoreListener != null) {
            Score score = new Score(game.getPlayerName(), game.getMapName(), game.getMoveCount());
            scoreListener.onScore(score);
        }
    }

    public String[] generateMapString() {
        String[] mapLines = new String[getRowCount()];
        Position snowmanPosition = null;

        // Encontrar posição do boneco de neve completo
        for (int row = 0; row < getRowCount(); row++) {
            for (int col = 0; col < getColCount(); col++) {
                if (boardContent.get(row).get(col) == PositionContent.SNOWMAN) {
                    snowmanPosition = new Position(row, col);
                    break;
                }
            }
        }

        // Construir cada linha
        for (int row = 0; row < getRowCount(); row++) {
            StringBuilder line = getStringBuilder(row, snowmanPosition);
            mapLines[row] = line.toString();
        }

        return mapLines;
    }

    private StringBuilder getStringBuilder(int row, Position snowmanPosition) {
        StringBuilder line = new StringBuilder();

        for (int col = 0; col < getColCount(); col++) {
            Position current = new Position(row, col);

            if (monster.getRow() == row && monster.getCol() == col) {
                line.append("\t\uD83D\uDC79\t");
            } else if (snowmanPosition != null && snowmanPosition.equals(current)) {
                line.append(" SM ");
            } else {
                PositionContent content = boardContent.get(row).get(col);
                switch (content) {
                    case BLOCK -> line.append("\tB\t");
                    case SNOW -> line.append("\tS\t");
                    case NO_SNOW -> line.append("\tX\t");
                    default -> line.append("\t☃️\t");
                }
            }
        }
        return line;
    }

    public boolean unstackSnowballs(Snowball stacked, Direction direction) {
        Snowball bottom = getBottom(stacked);
        Snowball top = getTop(stacked, direction);

        if (bottom != null && top != null && canUnstack(top.getRow(), top.getCol())) {
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

    public boolean isSnowballStack(Snowball snowball) {
        return snowball.isSnowballStack();
    }

    void checkCompleteSnowman(Position snowmanPos) {
        Snowball base = getSnowballInPosition(snowmanPos.getRow(), snowmanPos.getCol());
        if (base == null || base.getType() != SnowballType.BIG_MID) return;

        Snowball top = getSnowballInPosition(snowmanPos.getRow() - 1, snowmanPos.getCol());
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

    public Snowball getBottom(Snowball stack) {
        return switch (stack.getType()) {
            case MID_SMALL -> new Snowball(stack.getRow(), stack.getCol(), SnowballType.MID);
            case BIG_MID, BIG_SMALL -> new Snowball(stack.getRow(), stack.getCol(), SnowballType.BIG);
            default -> null;
        };
    }

    public Snowball getTop(Snowball stack, Direction direction) {
        Position position = new Position(stack.getRow(), stack.getCol()).changePosition(direction);
        SnowballType type = switch (stack.getType()) {
            case MID_SMALL, BIG_SMALL -> SnowballType.SMALL;
            case BIG_MID -> SnowballType.MID;
            default -> null;
        };
        return type == null ? null : new Snowball(position.getRow(), position.getCol(), type);
    }

    // === SISTEMA UNDO/REDO MELHORADO ===

    /**
     * Salva o estado atual do jogo
     */
    public void saveState() {
        // Remover estados futuros se estivermos no meio do histórico
        while (history.size() > currentStateIndex + 1) {
            history.remove(history.size() - 1);
        }

        // Limitar tamanho do histórico
        if (history.size() >= MAX_HISTORY_SIZE) {
            history.remove(0);
            currentStateIndex--;
        }

        GameState snapshot = cloneCurrentState();
        history.add(snapshot);
        currentStateIndex++;
    }

    /**
     * Cria uma cópia profunda do estado atual
     */
    private GameState cloneCurrentState() {
        // Clonar board content
        List<List<PositionContent>> boardCopy = new ArrayList<>();
        for (List<PositionContent> row : boardContent) {
            boardCopy.add(new ArrayList<>(row));
        }

        // Clonar snowballs
        List<Snowball> snowballCopy = new ArrayList<>();
        for (Snowball s : snowballs) {
            snowballCopy.add(new Snowball(s.getRow(), s.getCol(), s.getType()));
        }

        // Clonar monster
        Monster clonedMonster = new Monster(monster.getRow(), monster.getCol());

        return new GameState(clonedMonster, snowballCopy, boardCopy);
    }

    /**
     * Desfaz a última ação
     */
    public boolean undo() {
        if (canUndo()) {
            currentStateIndex--;
            restoreState(history.get(currentStateIndex));

            if (view != null) {
                view.updateBoard();
            }

            return true;
        }
        return false;
    }

    /**
     * Refaz a próxima ação
     */
    public boolean redo() {
        if (canRedo()) {
            currentStateIndex++;
            restoreState(history.get(currentStateIndex));

            if (view != null) {
                view.updateBoard();
            }

            return true;
        }
        return false;
    }

    /**
     * Verifica se é possível desfazer
     */
    public boolean canUndo() {
        return currentStateIndex > 0;
    }

    /**
     * Verifica se é possível refazer
     */
    public boolean canRedo() {
        return currentStateIndex < history.size() - 1;
    }

    /**
     * Restaura um estado específico
     */
    private void restoreState(GameState state) {
        // Restaurar board content
        boardContent.clear();
        for (List<PositionContent> row : state.boardContent()) {
            boardContent.add(new ArrayList<>(row));
        }

        // Restaurar snowballs
        snowballs.clear();
        for (Snowball s : state.snowballs()) {
            snowballs.add(new Snowball(s.getRow(), s.getCol(), s.getType()));
        }

        // Restaurar monster
        monster = new Monster(state.monster().getRow(), state.monster().getCol());
    }

    /**
     * Limpa o histórico de undo/redo
     */
    public void clearHistory() {
        history.clear();
        currentStateIndex = -1;
        saveState(); // Salvar estado atual como inicial
    }

    /**
     * Retorna informações sobre o estado do histórico (para debug)
     */
    public String getHistoryInfo() {
        return String.format("History: %d states, current index: %d, can undo: %b, can redo: %b",
                history.size(), currentStateIndex, canUndo(), canRedo());
    }

    public void setPositionContent(int row, int col, PositionContent content) {
        if (row >= 0 && row < getRowCount() && col >= 0 && col < getColCount()) {
            boardContent.get(row).set(col, content);

            if (boardListener != null) {
                boardListener.onTerrainChanged(row, col, content);
            }
        }
    }
}