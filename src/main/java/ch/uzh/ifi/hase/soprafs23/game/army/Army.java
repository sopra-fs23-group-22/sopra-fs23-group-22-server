package ch.uzh.ifi.hase.soprafs23.game.army;

import ch.uzh.ifi.hase.soprafs23.game.piece.*;
import ch.uzh.ifi.hase.soprafs23.game.piece.attackstrategies.AttackResult;
import ch.uzh.ifi.hase.soprafs23.game.piece.attackstrategies.AttackStrategy;
import ch.uzh.ifi.hase.soprafs23.game.states.AliveState;

import java.util.*;

import static ch.uzh.ifi.hase.soprafs23.game.piece.PieceType.*;
import static ch.uzh.ifi.hase.soprafs23.game.states.AliveState.*;

public class Army {
    private ArmyType armyType;
    private ArrayList<Piece> army = new ArrayList<Piece>();

    // A variable to store the configuration of the number of pieces for each PieceType
    static HashMap<PieceType, Integer> numOfPiecesPerType = new HashMap<PieceType, Integer>() {{
        numOfPiecesPerType.put(BOMB, 6);
        numOfPiecesPerType.put(MARSHAL, 1);
        numOfPiecesPerType.put(GENERAL, 1);
        numOfPiecesPerType.put(COLONEL, 2);
        numOfPiecesPerType.put(MAJOR, 3);
        numOfPiecesPerType.put(CAPTAIN, 4);
        numOfPiecesPerType.put(LIEUTENANT, 4);
        numOfPiecesPerType.put(SERGEANT, 4);
        numOfPiecesPerType.put(MINER, 5);
        numOfPiecesPerType.put(SCOUT, 8);
        numOfPiecesPerType.put(SPY, 1);
        numOfPiecesPerType.put(FLAG, 1);
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
                break;
            }
        }
        return state;
    }

    public ArrayList<Piece> getPieces() { return this.army; }

    public void setArmyPieces(Piece[] pieceArray) {
        this.army = new ArrayList<Piece>(Arrays.asList(pieceArray));
    }
}
