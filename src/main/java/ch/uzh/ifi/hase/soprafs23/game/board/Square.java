package ch.uzh.ifi.hase.soprafs23.game.board;

import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;

import static ch.uzh.ifi.hase.soprafs23.game.board.SquareType.BATTLE_FIELD;
import static java.lang.Math.abs;

public class Square {
    // An object of this class stands for a grid to place a chess piece
    private Axis axisX;
    private Axis axisY;
    private SquareType type;
    private Piece content;
    public Square(Axis axisX, Axis axisY) {
        this.axisX = axisX;
        this.axisY = axisY;
        this.type = BATTLE_FIELD;
        content = null; // This might unnecessarily increase object state space. We'll see if we can improve a bit.
    }

    public Axis[] getAxis() {
        Axis[] axisPair = new Axis[2];
        axisPair[0] = this.axisX;
        axisPair[1] = this.axisY;
        return axisPair;
    }

    public void setType(SquareType type) { this.type = type; }

    public void setContent(Piece piece) { this.content = piece; }

    public Piece getContent(){ return this.content; }

    public SquareType getType() { return this.type; }

    public int calculateDistanceTo(Square targetSquare) {
        Axis[] tagetAxis = targetSquare.getAxis();
        if (this.axisX == tagetAxis[0] && this.axisY != tagetAxis[1]) {
            // X axis equals, we compare distance in Y
            return abs(this.axisY.getInt() - tagetAxis[1].getInt());
        }
        else if (this.axisX != tagetAxis[0] && this.axisY == tagetAxis[1]) {
            // Y axis equals, we compare distance in X
            return abs(this.axisX.getInt() - tagetAxis[0].getInt());
        }
        else {
            return -1;
        }
    }

    public void clear() { this.content = null; }

}
