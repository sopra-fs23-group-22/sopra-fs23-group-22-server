package ch.uzh.ifi.hase.soprafs23.game.board;

public enum Axis {
    _1(1),
    _2(2),
    _3(3),
    _4(4),
    _5(5),
    _6(6),
    _7(7),
    _8(8),
    _9(9),
    _10(10);

    private final int value;

    Axis(int value){ this.value = value; }
    public int getInt(){
        return this.value;
    }

    public int getIntIndex() { return this.value - 1; }

}
