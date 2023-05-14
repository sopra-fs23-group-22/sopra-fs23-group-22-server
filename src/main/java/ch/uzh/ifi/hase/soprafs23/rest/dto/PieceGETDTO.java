package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.game.army.ArmyType;
import ch.uzh.ifi.hase.soprafs23.game.piece.PieceType;

import javax.servlet.http.PushBuilder;

public class PieceGETDTO {

    private PieceType pieceType;
    private ArmyType armyType;
    private boolean isRevealed;

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

    public boolean isRevealed() { return isRevealed; }

    public void setRevealed(boolean revealed) { isRevealed = revealed; }
}
