package pt.ipbeja.estig.po2.snowman.app.model;

import java.util.List;

public record GameState (
    Monster monster,
    List<Snowball> snowballs,
    List<List<PositionContent>> boardContent) {}

