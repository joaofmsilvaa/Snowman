package pt.ipbeja.estig.po2.snowman.app.model;

import java.util.ArrayList;
import java.util.List;

public class BoardModel {
    private List<List<PositionContent>> boardContent;
    private Monster monster;
    private List<Snowball> snowballs;
    private View view;

    public BoardModel() {
        boardContent = new ArrayList<>();
        snowballs = new ArrayList<>();
        startGame();
    }

    public BoardModel(List<List<PositionContent>> content, Monster monster, List<Snowball> snowballs) {
        this.monster = monster;
        this.snowballs = snowballs;
        this.boardContent = content;
    }

    public void setView(View view) {
        this.view = view;
    }

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

        snowballs.add(new Snowball(2, 1, SnowballType.SMALL));
        snowballs.add(new Snowball(2, 2, SnowballType.SMALL));
        snowballs.add(new Snowball(2, 3, SnowballType.SMALL));
    }

    public int getRowCount() {
        return boardContent.size();
    }

    public int getColCount() {
        return boardContent.isEmpty() ? 0 : boardContent.get(0).size();
    }

    public PositionContent getPositionContent(int row, int col) {
        return boardContent.get(row).get(col);
    }

    public boolean validPosition(int newRow, int newCol) {
        try {
            return getPositionContent(newRow, newCol) != PositionContent.BLOCK;
        } catch (Exception e) {
            return false;
        }
    }

    public Snowball snowballInPosition(int row, int col) {
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
            case UP -> snowballInPosition(row - 1, col);
            case DOWN -> snowballInPosition(row + 1, col);
            case LEFT -> snowballInPosition(row, col - 1);
            case RIGHT -> snowballInPosition(row, col + 1);
        };
    }

    public boolean moveMonster(Direction direction) {
        int oldRow = monster.getRow();
        int oldCol = monster.getCol();

        Snowball snowball = snowballInFrontOfMonster(direction);
        int oldSnowballRow = -1, oldSnowballCol = -1;
        if (snowball != null) {
            oldSnowballRow = snowball.getRow();
            oldSnowballCol = snowball.getCol();
        }

        boolean moved = monster.move(direction, this);

        if (moved && view != null) {
            view.onMonsterCleared(oldRow, oldCol);
            view.onMonsterMoved(monster.getRow(), monster.getCol());

            if (snowball != null) {
                view.onSnowballMoved(snowball, oldSnowballRow, oldSnowballCol);
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

        snowballs.remove(top);
        snowballs.remove(bottom);

        Snowball stacked = new Snowball(bottom.getRow(), bottom.getCol(), newType);
        snowballs.add(stacked);

        if (view != null) {
            view.onSnowballStacked(bottom.getRow(), bottom.getCol(), newType);
        }

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

    public boolean isSnowballStack(Snowball snowball) {
        return snowball.isSnowballStack();
    }

    void checkCompleteSnowman(int row, int col) {
        Snowball base = snowballInPosition(row, col);
        if (base == null || base.getType() != SnowballType.BIG_MID) return;

        Snowball top = snowballInPosition(row - 1, col);
        if (top != null && top.getType() == SnowballType.SMALL) {
            snowballs.remove(base);
            snowballs.remove(top);

            Snowball snowman = new Snowball(row, col, SnowballType.COMPLETE);
            snowballs.add(snowman);

            boardContent.get(row).set(col, PositionContent.SNOWMAN);

            if (view != null) {
                view.onSnowmanCreated(row, col, SnowballType.COMPLETE);
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

    public Monster getMonster() {
        return monster;
    }
}
