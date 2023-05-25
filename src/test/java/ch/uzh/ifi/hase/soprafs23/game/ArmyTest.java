package ch.uzh.ifi.hase.soprafs23.game;

import ch.uzh.ifi.hase.soprafs23.game.army.Army;
import ch.uzh.ifi.hase.soprafs23.game.army.ArmyType;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;
import ch.uzh.ifi.hase.soprafs23.game.piece.PieceType;
import ch.uzh.ifi.hase.soprafs23.game.states.AliveState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ArmyTest {
    private Piece[] pieces = new Piece[5];

    @BeforeEach

    private void setUp() {
        pieces[0] = new Piece(PieceType.MARSHAL, ArmyType.BLUE);
        pieces[1] = new Piece(PieceType.COLONEL, ArmyType.BLUE);
        pieces[2] = new Piece(PieceType.SCOUT, ArmyType.BLUE);
        pieces[3] = new Piece(PieceType.CAPTAIN, ArmyType.BLUE);
        pieces[4] = new Piece(PieceType.FLAG, ArmyType.BLUE);
    }

    @Test
    public void testGetAliveStateALIVE() {
        Army army = new Army(ArmyType.BLUE);
        army.setArmyPieces(pieces);
        for (Piece piece : army.getPieces()) {
            piece.setAliveState(AliveState.DOWN);
        }
        army.getPieces().get(0).setAliveState(AliveState.ALIVE);
        assert (army.getAliveState().equals(AliveState.ALIVE));
    }

    @Test
    public void testGetAliveStateDOWN() {
        Army army = new Army(ArmyType.BLUE);
        army.setArmyPieces(pieces);
        Piece[] pieces = new Piece[1];
        pieces[0] = new Piece(PieceType.MARSHAL, ArmyType.BLUE);
        army.setArmyPieces(pieces);

        for (Piece piece : army.getPieces()) {
            piece.setAliveState(AliveState.DOWN);
        }
        assert (army.getAliveState().equals(AliveState.DOWN));
    }
}
