package pt.ipbeja.estig.po2.snowman.app.model.interfaces;

import pt.ipbeja.estig.po2.snowman.app.model.Position;
import pt.ipbeja.estig.po2.snowman.app.model.Snowball;
import pt.ipbeja.estig.po2.snowman.app.model.SnowballType;

public interface View {

    /// Called when a complete snowman is formed (type COMPLETE).
    void onSnowmanCreated(Position snowballPosition, SnowballType newType);

    ///  Called whenever two snowballs partially stack ( SMALL + MID = MID_SMALL).
    void onSnowballStacked(Position snowballPosition, SnowballType newType);

    /// Called when a snowball stack is undone.
    void onSnowballUnstacked(Snowball topSnowball, Snowball bottomSnowball);

    /// Called when the monster finishes moving to a new position.
    void onMonsterMoved(Position monsterPosition);

    /// Called when a snowball is pushed to another cell.
    void onSnowballMoved(Snowball snowball, Position oldPosition);

    /// Called before drawing the monster in its new cell, to clear it from the old one.
    void onMonsterCleared(Position monsterPosition);
}
