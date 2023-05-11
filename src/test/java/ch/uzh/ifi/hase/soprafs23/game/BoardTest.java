package ch.uzh.ifi.hase.soprafs23.game;

import ch.uzh.ifi.hase.soprafs23.game.army.Army;
import ch.uzh.ifi.hase.soprafs23.game.army.ArmyType;
import ch.uzh.ifi.hase.soprafs23.game.board.Axis;
import ch.uzh.ifi.hase.soprafs23.game.board.Board;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;
import ch.uzh.ifi.hase.soprafs23.game.piece.PieceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    //This test probably can be omitted, usually the setPiece method is used
    @Test
    public void testPlace(){
        //create Board
        Board board = new Board();
        //create Piece to Place
        Piece piece = new Piece(PieceType.GENERAL, ArmyType.RED);
        Axis[] targetSquare = {Axis._4, Axis._1};
        board.place(piece, board.getSquareViaAxis(targetSquare));

        assertEquals(piece, board.getPieceViaAxis(targetSquare));
    }

    @Test
    public void testIsPlayerPiecesPlacedTrue(){
        //create Board
        Board board = new Board();

        //set 40 Pieces
        for(int i=0; i<10; i++){
            for(int j=0; j<4; j++){
                board.setPiece(i, j, new Piece(PieceType.CAPTAIN, ArmyType.BLUE));
            }
        }
        //check if the army for the blue player has been set
        assertTrue(board.isPlayerPiecesPlaced(new Player(1L, new Army(ArmyType.BLUE))));
    }

    @Test
    public void testIsPlayerPiecesPlacedFalse(){
        //create Board
        Board board = new Board();

        //set 39 Pieces
        int count = 0;
        //set 40 Pieces
        for(int i=0; i<10; i++){
            for(int j=0; j<4; j++){
                count +=1;
                if(count < 40){
                    board.setPiece(i, j, new Piece(PieceType.CAPTAIN, ArmyType.BLUE));
                }
            }
        }

        //check if method returns false on not completed army setup
        assertFalse(board.isPlayerPiecesPlaced(new Player(1L, new Army(ArmyType.BLUE))));
    }

    @Test
    public void testClear(){
        //create board
        Board board = new Board();
        //fill whole board
        for(int i=0; i<10; i++){
            for(int j=0; j<10; j++){
                board.setPiece(i, j, new Piece(PieceType.CAPTAIN, ArmyType.BLUE));
                //TODO j and i are exchanged, might need to refactor so that getSquare and setPiece use the same coordinate Axis
                assertNotNull(board.getSquare(j, i).getContent());
            }
        }
        board.clear();
        for(int i=0; i<10; i++){
            for(int j=0; j<10; j++){
                assertNull(board.getSquare(i,j).getContent());
            }
        }
    }
}
