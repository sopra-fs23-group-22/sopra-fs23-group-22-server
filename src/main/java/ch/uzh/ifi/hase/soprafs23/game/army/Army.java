package ch.uzh.ifi.hase.soprafs23.game.army;

import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;
import ch.uzh.ifi.hase.soprafs23.game.states.AliveState;

import java.util.ArrayList;
import java.util.Arrays;

import static ch.uzh.ifi.hase.soprafs23.game.states.AliveState.ALIVE;
import static ch.uzh.ifi.hase.soprafs23.game.states.AliveState.DOWN;

public class Army {
    private ArmyType armyType;
    private ArrayList<Piece> army;

    public Army(ArmyType armyType) { this.armyType = armyType; }

    public ArmyType getType() { return this.armyType; }

    public AliveState getAliveState() {
        AliveState state = DOWN;
        for (Piece piece : army) {
            if (piece.getAliveState() == ALIVE) {
                state = ALIVE;
                break;
            }
        }
        return state;
    }

    public ArrayList<Piece> getPieces() {
        return this.army;
    }

    public void setArmyPieces(Piece[] pieceArray) {
        this.army = new ArrayList<Piece>(Arrays.asList(pieceArray));
    }

}
