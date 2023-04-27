package ch.uzh.ifi.hase.soprafs23.game;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.game.army.Army;
import ch.uzh.ifi.hase.soprafs23.game.army.ArmyType;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;
import ch.uzh.ifi.hase.soprafs23.game.states.AliveState;
import org.junit.jupiter.api.Test;

public class ArmyTest {


    @Test
    public void testGetAliveStateALIVE(){
        Army army = new Army(ArmyType.BLUE);

        assert(army.getAliveState().equals(AliveState.ALIVE));


    }
    @Test
    public void testGetAliveStateDOWN(){
        Army army = new Army(ArmyType.BLUE);
        for(Piece piece : army.getPieces()){
            piece.setAliveState(AliveState.DOWN);
        }
        assert(army.getAliveState().equals(AliveState.DOWN));
    }
}
