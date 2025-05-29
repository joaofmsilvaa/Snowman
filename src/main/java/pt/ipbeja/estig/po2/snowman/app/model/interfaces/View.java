package pt.ipbeja.estig.po2.snowman.app.model.interfaces;

import pt.ipbeja.estig.po2.snowman.app.model.Position;
import pt.ipbeja.estig.po2.snowman.app.model.Snowball;
import pt.ipbeja.estig.po2.snowman.app.model.SnowballType;

public interface View {
    void onSnowmanCreated(Position snowballPosition, SnowballType newType);
    void onSnowballStacked(Position snowballPosition, SnowballType newType);
    void onSnowballUnstacked(Snowball topSnowball, Snowball bottomSnowball);
    void onMonsterMoved(Position monsterPosition);
    void onSnowballMoved(Snowball snowball, Position oldPosition);
    void onMonsterCleared(Position monsterPosition);
}
