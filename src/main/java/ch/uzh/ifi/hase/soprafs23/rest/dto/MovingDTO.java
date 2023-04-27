package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.game.board.Axis;

public class MovingDTO {

    Axis[] source;
    Axis[] target;

    public Axis[] getSource() {
        return source;
    }

    public void setSource(Axis[] source) {
        this.source = source;
    }

    public Axis[] getTarget() {
        return target;
    }

    public void setTarget(Axis[] target) {
        this.target = target;
    }
}
