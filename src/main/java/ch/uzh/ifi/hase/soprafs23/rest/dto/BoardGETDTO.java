package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.game.board.Square;

public class BoardGETDTO {

    private SquareGETDTO[][] square;

    public SquareGETDTO[][] getSquare() {
        return square;
    }

    public void setSquare(SquareGETDTO[][] square) {
        this.square = square;
    }

}
