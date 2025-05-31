package pt.ipbeja.estig.po2.snowman.app.model.interfaces;

import pt.ipbeja.estig.po2.snowman.app.model.Position;
import pt.ipbeja.estig.po2.snowman.app.model.Snowball;
import pt.ipbeja.estig.po2.snowman.app.model.SnowballType;

public interface View {

    /// Chamado quando se forma um boneco de neve completo (tipo COMPLETE).
    void onSnowmanCreated(Position snowballPosition, SnowballType newType);

    /// Chamado sempre que duas bolas empilham parcialmente tipo SMALL + MID = MID_SMALL
    void onSnowballStacked(Position snowballPosition, SnowballType newType);

    /// Chamado quando se desfaz uma pilha de bolas
    void onSnowballUnstacked(Snowball topSnowball, Snowball bottomSnowball);

    /// Chamado quando o monstro termina um movimento para uma nova posição.
    void onMonsterMoved(Position monsterPosition);

    /// Chamado quando uma bola e empurrada para outra célula.
    void onSnowballMoved(Snowball snowball, Position oldPosition);

    /// Chamado antes de desenhar o monstro na nova célula, para limpar a antiga.
    void onMonsterCleared(Position monsterPosition);
}
