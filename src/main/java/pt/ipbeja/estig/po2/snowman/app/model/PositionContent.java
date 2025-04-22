package pt.ipbeja.estig.po2.snowman.app.model;

public enum PositionContent{

    NO_SNOW(Mark.NO_SNOW),
    SNOW(Mark.SNOW),
    BLOCK(Mark.BLOCK),
    SNOWMAN(Mark.SNOWMAN);


    private final Mark mark;

    PositionContent(Mark mark) {
        this.mark = mark;
    }

    public Mark getMark() {
        return mark;
    }
}

