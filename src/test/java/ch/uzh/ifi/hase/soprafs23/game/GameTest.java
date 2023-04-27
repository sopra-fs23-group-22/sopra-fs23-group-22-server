package ch.uzh.ifi.hase.soprafs23.game;

import ch.uzh.ifi.hase.soprafs23.game.army.ArmyType;
import ch.uzh.ifi.hase.soprafs23.game.board.SquareType;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;
import ch.uzh.ifi.hase.soprafs23.game.piece.PieceType;
import ch.uzh.ifi.hase.soprafs23.game.states.GameState;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static ch.uzh.ifi.hase.soprafs23.game.board.Axis._2;
import static ch.uzh.ifi.hase.soprafs23.game.board.Axis._4;
import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    @Test
    public void testGameSetup(){
        Game game = new Game();
        ArrayList<Long> input = new ArrayList<>();
        input.add(1L);
        input.add(2L);
        game.setup(input);
        //check if the game has been put in gamestate Pre play
        assert(game.getGameState().equals(GameState.PRE_PLAY));

        //check if Board initialisation worked
        //if this square is of type lake we know that the board was set up correctly
        assert(game.getBoard().getSquare(4, 2).getSquareType().equals(SquareType.LAKE));
    }

    @Test public void testPlacePieces(){
        Game game = new Game();
        ArrayList<Long> input = new ArrayList<>();
        input.add(1L);
        input.add(2L);
        game.setup(input);
        Piece[] pieces = new Piece[40];
        for(int i=0; i<40; i++){
            pieces[i] = new Piece(PieceType.BOMB, ArmyType.BLUE);

        }

        game.placePieces(pieces);
        //check the outer points of the blue army
        assert(game.getBoard().getSquare(0, 0).getContent().getPieceType().equals(PieceType.BOMB));
        assert(game.getBoard().getSquare(3, 9).getContent().getPieceType().equals(PieceType.BOMB));
        //check the outer points of where red pieces would be placed
        assertEquals(game.getBoard().getSquare(6, 0).getContent(), null);

    }
}
