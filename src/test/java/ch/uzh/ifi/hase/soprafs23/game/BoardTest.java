package ch.uzh.ifi.hase.soprafs23.game;

import ch.uzh.ifi.hase.soprafs23.game.army.Army;
import ch.uzh.ifi.hase.soprafs23.game.army.ArmyType;
import ch.uzh.ifi.hase.soprafs23.game.board.Axis;
import ch.uzh.ifi.hase.soprafs23.game.board.Board;
import ch.uzh.ifi.hase.soprafs23.game.board.Square;
import ch.uzh.ifi.hase.soprafs23.game.board.SquareType;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;
import ch.uzh.ifi.hase.soprafs23.game.piece.PieceType;
import ch.uzh.ifi.hase.soprafs23.game.piece.attackstrategies.AttackResult;
import ch.uzh.ifi.hase.soprafs23.game.piece.movestrategies.MoveResult;
import ch.uzh.ifi.hase.soprafs23.game.states.AliveState;
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

    @Test
    public void testMovePieceIllegalTarget_LAKE(){
        //create board
        Board board = new Board();
        Piece piece = new Piece(PieceType.GENERAL, ArmyType.BLUE);
        //TODO:setPiece axis coordinate change
        board.setPiece(4, 4, piece);

        //verify that Square 4, 3 is of Type lake
        assertEquals(board.getSquare(4, 3).getType(), SquareType.LAKE);
        //verify that Square 4, 3 is not occupied to avoid wrong exception
        assertNull(board.getSquare(4, 3).getContent());
        //create Axis[] for parameter input
        Axis[] source = {Axis._4, Axis._4};
        Axis[] target = {Axis._4, Axis._3};
        assertThrows(IllegalStateException.class, () -> {
            board.movePiece(source, target);
        });
    }

    @Test
    public void testMovePieceIllegalTarget_OCCUPIED(){
        Board board = new Board();
        Piece piece = new Piece(PieceType.GENERAL, ArmyType.BLUE);
        Piece piece2 = new Piece(PieceType.CAPTAIN, ArmyType.BLUE);
        //TODO:setPiece axis coordinate change
        board.setPiece(4, 4, piece);
        board.setPiece(5,4, piece2);
        //verify that Square 4, 5 is of Type Battlefield to avoid wrong exception
        assertEquals(board.getSquare(4, 5).getType(), SquareType.BATTLE_FIELD);
        //verify that Square 4, 5 is occupied
        assertNotNull(board.getSquare(4, 5).getContent());
        //create Axis[] for parameter input
        Axis[] source = {Axis._4, Axis._4};
        Axis[] target = {Axis._4, Axis._5};

        assertThrows(IllegalStateException.class, () -> {
            board.movePiece(source, target);
        });
    }

    @Test
    public void testMoveSUCCESFUL(){
        Board board = new Board();
        Piece piece = new Piece(PieceType.GENERAL, ArmyType.BLUE);
        board.setPiece(4, 4, piece);

        Axis[] source = {Axis._4, Axis._4};
        Axis[] target = {Axis._4, Axis._5};
        //verify that target is empty
        assertNull(board.getSquare(4, 5).getContent());
        assertEquals(board.movePiece(source, target), MoveResult.SUCCESSFUL);
    }
    //TODO: There will probably need to be additional checks to ensure a piece is not attacking
    //TODO: a piece of their own army!
    @Test
    public void testAttackPieceSUCCESFUL(){
        //create board
        Board board = new Board();
        //Captain has rank 6, general has rank 9 so the attacker should win
        Piece attacker = new Piece(PieceType.GENERAL, ArmyType.BLUE);
        Piece targetPiece = new Piece(PieceType.CAPTAIN, ArmyType.RED);
        board.setPiece(4,4,attacker);
        board.setPiece(5,4,targetPiece);
        Axis[] sourceSquare = {Axis._4, Axis._4};
        Axis[] targetSquare = {Axis._4, Axis._5};

        board.attackPiece(sourceSquare, targetSquare);
        //verify that targetPiece is set as down
        assertEquals(AliveState.DOWN, targetPiece.getAliveState());
        assertEquals(AliveState.ALIVE, attacker.getAliveState());
        //verify that attacker moved to the target square
        assertNull(board.getSquare(4, 4).getContent());
        assertEquals(board.getSquare(4,5).getContent(), attacker);
    }

    @Test
    public void testAttackPieceDEFEATED(){
        //create board
        Board board = new Board();
        //Lieutenant has rank 5, Marshal has rank 10 so the attacker should lose
        Piece attacker = new Piece(PieceType.LIEUTENANT, ArmyType.BLUE);
        Piece targetPiece = new Piece(PieceType.MARSHAL, ArmyType.RED);
        board.setPiece(4,4,attacker);
        board.setPiece(5,4,targetPiece);
        Axis[] sourceSquare = {Axis._4, Axis._4};
        Axis[] targetSquare = {Axis._4, Axis._5};

        board.attackPiece(sourceSquare, targetSquare);
        //verify that targetPiece is set as down
        assertEquals(AliveState.DOWN, attacker.getAliveState());
        assertEquals(AliveState.ALIVE, targetPiece.getAliveState());
        //verify that attacker moved to the target square
        assertNull(board.getSquare(4, 4).getContent());
        assertEquals(board.getSquare(4,5).getContent(), targetPiece);
    }

    @Test
    public void testAttackPieceDRAW(){
        //create board
        Board board = new Board();
        //Both Pieces have the same rank so both of them should be removed
        Piece attacker = new Piece(PieceType.LIEUTENANT, ArmyType.BLUE);
        Piece targetPiece = new Piece(PieceType.LIEUTENANT, ArmyType.RED);
        board.setPiece(4,4,attacker);
        board.setPiece(5,4,targetPiece);
        Axis[] sourceSquare = {Axis._4, Axis._4};
        Axis[] targetSquare = {Axis._4, Axis._5};

        board.attackPiece(sourceSquare, targetSquare);

        //verify that both pieces have aliveState DOWN
        assertEquals(AliveState.DOWN, attacker.getAliveState());
        assertEquals(AliveState.DOWN, targetPiece.getAliveState());
        //verify that both squares are empty now
        assertNull(board.getSquare(4, 4).getContent());
        assertNull(board.getSquare(4,5).getContent());
    }

    @Test
    public void testGetPathLegalInputs(){
        //create Board
        Board board = new Board();
        Axis[] sourceSquare = {Axis._0, Axis._0};
        Axis[] targetSquare = {Axis._9, Axis._9};

        //define array of legal inputs
        Axis[][][] input = {{{Axis._0, Axis._9}, {Axis._9, Axis._9}}, {{Axis._9, Axis._9}, {Axis._9, Axis._0}}, {{Axis._0, Axis._0}, {Axis._0, Axis._9}}, {{Axis._3, Axis._3}, {Axis._5, Axis._3}}};
        int[][][] expected = {{{1,9}, {2,9}, {3,9}, {4,9}, {5,9}, {6,9}, {7,9}, {8,9}},{{9,8}, {9,7}, {9,6}, {9,5}, {9,4}, {9,3}, {9,2}, {9,1}}, {{0,1}, {0,2}, {0,3},{0,4},{0,5},{0,6},{0,7},{0,8}}, {{4,3}}};
        for(int i=0; i<input.length; i++){
            Square[] result = board.getPath(input[i][0], input[i][1]);
            for(int j=0; j<result.length; j++){
                assertEquals(expected[i][j][0], result[j].getAxisX().getInt());
                assertEquals(expected[i][j][1], result[j].getAxisY().getInt());
            }
        }
    }
}
