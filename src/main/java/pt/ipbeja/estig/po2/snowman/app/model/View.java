package pt.ipbeja.estig.po2.snowman.app.model;

public interface View {
    void onSnowmanCreated(int row, int col);
    void onSnowballStacked(int row, int col, SnowballType newType);
    void onMonsterMoved(int row, int col);
    void onSnowballMoved(int row, int col);
    void onMonsterCleared(int row, int col);
}
