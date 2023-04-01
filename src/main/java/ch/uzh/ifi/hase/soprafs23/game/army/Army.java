package ch.uzh.ifi.hase.soprafs23.game.army;

import ch.uzh.ifi.hase.soprafs23.game.piece.*;
import ch.uzh.ifi.hase.soprafs23.game.states.AliveState;

import java.util.*;

import static ch.uzh.ifi.hase.soprafs23.game.piece.PieceType.*;
import static ch.uzh.ifi.hase.soprafs23.game.states.AliveState.*;

public class Army {
    private ArmyType armyType;
    private ArrayList<Piece> army = new ArrayList<Piece>();

    // A variable to store the configuration of the number of pieces for each PieceType
    static HashMap<PieceType, Integer> numOfPiecesPerType = new HashMap<PieceType, Integer>() {{
        put(BOMB, 6);
        put(MARSHAL, 1);
        put(GENERAL, 1);
        put(COLONEL, 2);
        put(MAJOR, 3);
        put(CAPTAIN, 4);
        put(LIEUTENANT, 4);
        put(MINER, 5);
        put(SCOUT, 8);
        put(SPY, 1);
        put(FLAG, 1);
    }};

    public Army(ArmyType armyType) {
        this.armyType = armyType;
        // Instantiating all pieces in this army. Each with the number specified in numOfPiecesPerType
        for (HashMap.Entry<PieceType, Integer> config : numOfPiecesPerType.entrySet()) {
            PieceType pieceType = config.getKey();
            Integer numOfThisType = config.getValue();
            for (int i = 0; i < numOfThisType; i++){
                army.add(new Piece(pieceType, armyType));
            }
        }
    }

    public ArmyType getType(){ return this.armyType; }

    public AliveState getAliveState() {
        AliveState state = DOWN;
        for (Piece piece : army) {
            if (piece.getAliveState() == ALIVE) {
                state = ALIVE;
                continue;
            }
        }
        return state;
    }
}
