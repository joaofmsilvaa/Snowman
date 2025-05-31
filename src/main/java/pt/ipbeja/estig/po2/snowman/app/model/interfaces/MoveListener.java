package pt.ipbeja.estig.po2.snowman.app.model.interfaces;

import pt.ipbeja.estig.po2.snowman.app.model.Position;

public interface MoveListener {
    
    /// Chamado sempre que o monstro efetua um movimento bem-sucedido.
    void onMove(Position from, Position to);
}
