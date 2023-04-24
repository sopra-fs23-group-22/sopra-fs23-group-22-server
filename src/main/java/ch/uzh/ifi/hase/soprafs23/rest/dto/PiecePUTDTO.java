package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.game.army.ArmyType;
import ch.uzh.ifi.hase.soprafs23.game.piece.PieceType;

public class PiecePUTDTO {

    private PieceType pieceType;
    private ArmyType armyType;


    public PieceType getPieceType() {
        return pieceType;
    }

    public void setPieceType(PieceType pieceType) {
        this.pieceType = pieceType;
    }

    public ArmyType getArmyType() {
        return armyType;
    }

    public void setArmyType(ArmyType armyType) {
        this.armyType = armyType;
    }

}
