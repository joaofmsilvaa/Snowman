package pt.ipbeja.estig.po2.snowman.app.model;

public interface View {
    void onSnowmanCreated(int row, int col);
    void onSnowballStacked(int row, int col, SnowballType newType);
}
