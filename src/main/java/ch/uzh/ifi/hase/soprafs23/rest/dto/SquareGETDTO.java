package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.game.board.Axis;
import ch.uzh.ifi.hase.soprafs23.game.board.SquareType;

public class SquareGETDTO {

    private Axis axisX;
    private Axis axisY;
    private SquareType type;
    private PieceGETDTO content;


    public Axis getAxisX() {
        return axisX;
    }

    public void setAxisX(Axis axisX) {
        this.axisX = axisX;
    }

    public Axis getAxisY() {
        return axisY;
    }

    public void setAxisY(Axis axisY) {
        this.axisY = axisY;
    }

    public SquareType getType() {
        return type;
    }

    public void setType(SquareType type) {
        this.type = type;
    }

    public PieceGETDTO getContent() {
        return content;
    }

    public void setContent(PieceGETDTO content) {
        this.content = content;
    }
}
