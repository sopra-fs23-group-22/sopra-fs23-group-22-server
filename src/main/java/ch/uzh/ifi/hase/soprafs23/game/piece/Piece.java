package ch.uzh.ifi.hase.soprafs23.game.piece;

import ch.uzh.ifi.hase.soprafs23.game.army.ArmyType;
import ch.uzh.ifi.hase.soprafs23.game.board.Square;
import ch.uzh.ifi.hase.soprafs23.game.piece.attackstrategies.AttackResult;
import ch.uzh.ifi.hase.soprafs23.game.piece.attackstrategies.AttackStrategy;
import ch.uzh.ifi.hase.soprafs23.game.piece.movestrategies.MoveResult;
import ch.uzh.ifi.hase.soprafs23.game.piece.movestrategies.MoveStrategy;
import ch.uzh.ifi.hase.soprafs23.game.states.AliveState;
import ch.uzh.ifi.hase.soprafs23.game.board.Axis;
import ch.uzh.ifi.hase.soprafs23.game.army.Army;

import static ch.uzh.ifi.hase.soprafs23.game.states.AliveState.ALIVE;

public class Piece {
    private PieceType pieceType;
    private ArmyType armyType;
    //private Square location;
    private AliveState aliveState;
    private boolean isRevealed = false;

    public Piece(PieceType pieceType, ArmyType armyType) {
        this.pieceType = pieceType;
        this.armyType = armyType;
        aliveState = ALIVE;
    }

    public PieceType getPieceType() { return this.pieceType; }
    public ArmyType getArmyType() { return this.armyType; }
    //public Square getLocation() { return this.location; }
    public AliveState getAliveState() { return this.aliveState; }
    public void setAliveState(AliveState aliveState) { this.aliveState = aliveState; }
    public AttackResult attack(Square sourceSquare, Square targetSquare) { return this.pieceType.getAttackStrategy().attack(sourceSquare, targetSquare); }
    public MoveResult move(Square sourceSquare, Square targetSquare) { return this.pieceType.getMoveStrategy().move(sourceSquare, targetSquare); }
    public boolean isRevealed() { return this.isRevealed; }
    public void setRevealed(boolean revealed) { this.isRevealed = revealed; }

    public void setPieceType(PieceType pieceType) {
        this.pieceType = pieceType;
    }

    public void setArmyType(ArmyType armyType) {
        this.armyType = armyType;
    }
}
