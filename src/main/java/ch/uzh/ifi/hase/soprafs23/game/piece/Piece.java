package ch.uzh.ifi.hase.soprafs23.game.piece;

import ch.uzh.ifi.hase.soprafs23.game.army.ArmyType;
import ch.uzh.ifi.hase.soprafs23.game.piece.attackstrategies.AttackStrategy;
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
    private MoveStrategy moveStrategy;
    private AttackStrategy attackStrategy;

    public Piece(PieceType pieceType, ArmyType armyType) {
        this.pieceType = pieceType;
        this.armyType = armyType;
        aliveState = ALIVE;
    }

    public PieceType getPieceType() { return this.pieceType; }
    public ArmyType getArmyType() { return this.armyType; }
    //public Square getLocation() { return this.location; }
    public AliveState getAliveState() { return this.aliveState; }
    public void attack(Axis[] targetAxis) { this.attackStrategy; }
    public void move(Axis[] targetAxis) { this.moveStrategy; }

}
