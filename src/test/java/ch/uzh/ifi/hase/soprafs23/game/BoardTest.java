package ch.uzh.ifi.hase.soprafs23.game;

import ch.uzh.ifi.hase.soprafs23.game.army.Army;
import ch.uzh.ifi.hase.soprafs23.game.army.ArmyType;
import ch.uzh.ifi.hase.soprafs23.game.board.Axis;
import ch.uzh.ifi.hase.soprafs23.game.board.Board;
import ch.uzh.ifi.hase.soprafs23.game.board.Square;
import ch.uzh.ifi.hase.soprafs23.game.board.SquareType;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;
import ch.uzh.ifi.hase.soprafs23.game.piece.PieceType;
import ch.uzh.ifi.hase.soprafs23.game.piece.movestrategies.MoveResult;
import ch.uzh.ifi.hase.soprafs23.game.states.AliveState;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    //This test probably can be omitted, usually the setPiece method is used
    @Test
    public void testPlace() {
        //create Board
        Board board = new Board();
        //create Piece to Place
        Piece piece = new Piece(PieceType.GENERAL, ArmyType.RED);
        Axis[] targetSquare = {Axis._4, Axis._1};
        board.place(piece, board.getSquareViaAxis(targetSquare));

        assertEquals(piece, board.getPieceViaAxis(targetSquare));
    }

    @Test
    public void testIsPlayerPiecesPlacedTrue() {
        //create Board
        Board board = new Board();

        //set 40 Pieces
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 4; j++) {
                board.setPiece(i, j, new Piece(PieceType.CAPTAIN, ArmyType.BLUE));
            }
        }
        //check if the army for the blue player has been set
        assertTrue(board.isPlayerPiecesPlaced(new Player(1L, new Army(ArmyType.BLUE))));
    }

    @Test
    public void testIsPlayerPiecesPlacedFalse() {
        //create Board
        Board board = new Board();

        //set 39 Pieces
        int count = 0;
        //set 40 Pieces
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 4; j++) {
                count += 1;
                if (count < 40) {
                    board.setPiece(i, j, new Piece(PieceType.CAPTAIN, ArmyType.BLUE));
                }
            }
        }

        //check if method returns false on not completed army setup
        assertFalse(board.isPlayerPiecesPlaced(new Player(1L, new Army(ArmyType.BLUE))));
    }

    @Test
    public void testClear() {
        //create board
        Board board = new Board();
        //fill whole board
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                board.setPiece(i, j, new Piece(PieceType.CAPTAIN, ArmyType.BLUE));
                assertNotNull(board.getSquare(j, i).getContent());
            }
        }
        board.clear();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                assertNull(board.getSquare(i, j).getContent());
            }
        }
    }

    @Test
    public void testMovePieceIllegalTarget_LAKE() {
        //create board
        Board board = new Board();
        Piece piece = new Piece(PieceType.GENERAL, ArmyType.BLUE);
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
    public void testMovePieceIllegalTarget_OCCUPIED() {
        Board board = new Board();
        Piece piece = new Piece(PieceType.GENERAL, ArmyType.BLUE);
        Piece piece2 = new Piece(PieceType.CAPTAIN, ArmyType.BLUE);
        board.setPiece(4, 4, piece);
        board.setPiece(5, 4, piece2);
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
    public void testMoveSUCCESFUL() {
        Board board = new Board();
        Piece piece = new Piece(PieceType.GENERAL, ArmyType.BLUE);
        board.setPiece(4, 4, piece);

        Axis[] source = {Axis._4, Axis._4};
        Axis[] target = {Axis._4, Axis._5};
        //verify that target is empty
        assertNull(board.getSquare(4, 5).getContent());
        assertEquals(board.movePiece(source, target), MoveResult.SUCCESSFUL);
    }

    @Test
    public void testAttackPieceSUCCESFUL() {
        //create board
        Board board = new Board();
        //Captain has rank 6, general has rank 9 so the attacker should win
        Piece attacker = new Piece(PieceType.GENERAL, ArmyType.BLUE);
        Piece targetPiece = new Piece(PieceType.CAPTAIN, ArmyType.RED);
        board.setPiece(4, 4, attacker);
        board.setPiece(5, 4, targetPiece);
        Axis[] sourceSquare = {Axis._4, Axis._4};
        Axis[] targetSquare = {Axis._4, Axis._5};

        board.attackPiece(sourceSquare, targetSquare);
        //verify that targetPiece is set as down
        assertEquals(AliveState.DOWN, targetPiece.getAliveState());
        assertEquals(AliveState.ALIVE, attacker.getAliveState());
        //verify that attacker moved to the target square
        assertNull(board.getSquare(4, 4).getContent());
        assertEquals(board.getSquare(4, 5).getContent(), attacker);
    }

    @Test
    public void testAttackPieceDEFEATED() {
        //create board
        Board board = new Board();
        //Lieutenant has rank 5, Marshal has rank 10 so the attacker should lose
        Piece attacker = new Piece(PieceType.LIEUTENANT, ArmyType.BLUE);
        Piece targetPiece = new Piece(PieceType.MARSHAL, ArmyType.RED);
        board.setPiece(4, 4, attacker);
        board.setPiece(5, 4, targetPiece);
        Axis[] sourceSquare = {Axis._4, Axis._4};
        Axis[] targetSquare = {Axis._4, Axis._5};

        board.attackPiece(sourceSquare, targetSquare);
        //verify that targetPiece is set as down
        assertEquals(AliveState.DOWN, attacker.getAliveState());
        assertEquals(AliveState.ALIVE, targetPiece.getAliveState());
        //verify that attacker moved to the target square
        assertNull(board.getSquare(4, 4).getContent());
        assertEquals(board.getSquare(4, 5).getContent(), targetPiece);
    }

    @Test
    public void testAttackPieceDRAW() {
        //create board
        Board board = new Board();
        //Both Pieces have the same rank so both of them should be removed
        Piece attacker = new Piece(PieceType.LIEUTENANT, ArmyType.BLUE);
        Piece targetPiece = new Piece(PieceType.LIEUTENANT, ArmyType.RED);
        board.setPiece(4, 4, attacker);
        board.setPiece(5, 4, targetPiece);
        Axis[] sourceSquare = {Axis._4, Axis._4};
        Axis[] targetSquare = {Axis._4, Axis._5};

        board.attackPiece(sourceSquare, targetSquare);

        //verify that both pieces have aliveState DOWN
        assertEquals(AliveState.DOWN, attacker.getAliveState());
        assertEquals(AliveState.DOWN, targetPiece.getAliveState());
        //verify that both squares are empty now
        assertNull(board.getSquare(4, 4).getContent());
        assertNull(board.getSquare(4, 5).getContent());
    }

    @Test
    public void testGetPathLegalInputs() {
        //create Board
        Board board = new Board();
        Axis[] sourceSquare = {Axis._0, Axis._0};
        Axis[] targetSquare = {Axis._9, Axis._9};

        //define array of legal inputs
        Axis[][][] input = {{{Axis._0, Axis._9}, {Axis._9, Axis._9}}, {{Axis._9, Axis._9}, {Axis._9, Axis._0}}, {{Axis._0, Axis._0}, {Axis._0, Axis._9}}, {{Axis._3, Axis._3}, {Axis._5, Axis._3}}};
        int[][][] expected = {{{1, 9}, {2, 9}, {3, 9}, {4, 9}, {5, 9}, {6, 9}, {7, 9}, {8, 9}}, {{9, 8}, {9, 7}, {9, 6}, {9, 5}, {9, 4}, {9, 3}, {9, 2}, {9, 1}}, {{0, 1}, {0, 2}, {0, 3}, {0, 4}, {0, 5}, {0, 6}, {0, 7}, {0, 8}}, {{4, 3}}};
        for (int i = 0; i < input.length; i++) {
            Square[] result = board.getPath(input[i][0], input[i][1]);
            for (int j = 0; j < result.length; j++) {
                assertEquals(expected[i][j][0], result[j].getAxisX().getInt());
                assertEquals(expected[i][j][1], result[j].getAxisY().getInt());
            }
        }
    }

    private Board setupAvailableTargets() {
        Board board = new Board();
        Piece bombBlue = new Piece(PieceType.BOMB, ArmyType.BLUE);
        Piece flagBlue = new Piece(PieceType.FLAG, ArmyType.BLUE);
        Piece scoutBlue = new Piece(PieceType.SCOUT, ArmyType.BLUE);
        Piece captainBlue = new Piece(PieceType.CAPTAIN, ArmyType.BLUE);
        Piece generalBlue = new Piece(PieceType.GENERAL, ArmyType.BLUE);
        Piece bombRed1 = new Piece(PieceType.BOMB, ArmyType.RED);
        Piece bombRed2 = new Piece(PieceType.BOMB, ArmyType.RED);
        board.setPiece(0, 5, bombBlue);
        board.setPiece(1, 5, captainBlue);
        board.setPiece(1, 6, bombRed1);
        board.setPiece(0, 7, generalBlue);
        board.setPiece(3, 7, scoutBlue);
        board.setPiece(8, 7, bombRed2);

        /*This is how the set up board looks like:
x - empty squares, L - lake squares, B - blue bombs, C - blue captain, G - blue general, S - blue scout, R - Red Pieces
            xxxxxxxxxx
            xxxxxxxxxx
            xxxxxxxxxx
            xxxxxxxxxx
            xxLLxxLLxx
            BCLLxxLLxx
            xRxxxxxxxx
            GxxSxxxxRx
            xxxxxxxxxx
            xxxxxxxxxx
        */
        return board;
    }

    @Test
    public void test_getAvailableTargets() {
        //setup Board
        Board board = setupAvailableTargets();
        //getAvailableTargets for the bomb should return empty list
        List<Square> expected = new ArrayList<>();
        ArrayList<Square> actual = board.getAvailableTargets(new Axis[]{Axis._5, Axis._0});
        assertEquals(expected, actual);
        //getAvailableTargets for the captain should return Axis (6,1), (4, 1)
        expected = Arrays.asList(board.getSquare(4, 1), board.getSquare(6, 1));
        actual = board.getAvailableTargets(new Axis[]{Axis._5, Axis._1});
        assertTrue(actual.containsAll(expected) && actual.size() == expected.size());
        //getAvailableTargets for the general should return Axis (6,0), (7,1), (8,0)
        expected = Arrays.asList(board.getSquare(6, 0), board.getSquare(7, 1), board.getSquare(8, 0));
        actual = board.getAvailableTargets(new Axis[]{Axis._7, Axis._0});
        assertTrue(actual.containsAll(expected) && actual.size() == expected.size());
        //getAvailableTargets for the scout should return Axis (7,1),(7,2),(6,3),(8,3),(9,3),(7,4),(7,5),(7,6),(7,7),(7,8)
        expected = Arrays.asList(board.getSquare(7, 1), board.getSquare(7, 2), board.getSquare(6, 3), board.getSquare(8, 3), board.getSquare(9, 3), board.getSquare(7, 4), board.getSquare(7, 5), board.getSquare(7, 6), board.getSquare(7, 7), board.getSquare(7, 8));
        actual = board.getAvailableTargets(new Axis[]{Axis._7, Axis._3});
        assertTrue(actual.containsAll(expected) && actual.size() == expected.size());
    }

    @Test
    public void test_getSurroundingSquaresViaAxis() {
        //Since this is a private method we will test it Via getAvailableTargets
        //There are 9 distinct cases to cover so we will create 9 pieces
        Board board = new Board();
        Piece piece = new Piece(PieceType.SERGEANT, ArmyType.BLUE);
        board.setPiece(0, 0, piece);
        board.setPiece(9, 0, piece);
        board.setPiece(0, 9, piece);
        board.setPiece(9, 9, piece);
        board.setPiece(0, 7, piece);
        board.setPiece(7, 0, piece);
        board.setPiece(7, 9, piece);
        board.setPiece(9, 7, piece);
        board.setPiece(2, 2, piece);
        //case 1: x=0, y=0
        List<Square> actual = board.getAvailableTargets(new Axis[]{Axis._0, Axis._0});
        List<Square> expected = Arrays.asList(board.getSquare(0, 1), board.getSquare(1, 0));
        assertTrue(actual.size() == 2 && actual.containsAll(expected));
        //case 2: x=0, y=9
        actual = board.getAvailableTargets(new Axis[]{Axis._0, Axis._9});
        expected = Arrays.asList(board.getSquare(1, 9), board.getSquare(0, 8));
        assertTrue(actual.size() == 2 && actual.containsAll(expected));
        //case 3: x=9, y=0
        actual = board.getAvailableTargets(new Axis[]{Axis._9, Axis._0});
        expected = Arrays.asList(board.getSquare(9, 1), board.getSquare(8, 0));
        assertTrue(actual.size() == 2 && actual.containsAll(expected));
        //case 4: x=9, y=9
        actual = board.getAvailableTargets(new Axis[]{Axis._9, Axis._9});
        expected = Arrays.asList(board.getSquare(8, 9), board.getSquare(9, 8));
        assertTrue(actual.size() == 2 && actual.containsAll(expected));
        //case 5: x=0, y=7
        actual = board.getAvailableTargets(new Axis[]{Axis._0, Axis._7});
        expected = Arrays.asList(board.getSquare(1, 7), board.getSquare(0, 6), board.getSquare(0, 8));
        assertTrue(actual.size() == 3 && actual.containsAll(expected));
        //case 6: x=9, y=7
        actual = board.getAvailableTargets(new Axis[]{Axis._9, Axis._7});
        expected = Arrays.asList(board.getSquare(8, 7), board.getSquare(9, 6), board.getSquare(9, 8));
        assertTrue(actual.size() == 3 && actual.containsAll(expected));
        //case 7: x=7, y=0
        actual = board.getAvailableTargets(new Axis[]{Axis._7, Axis._0});
        expected = Arrays.asList(board.getSquare(7, 1), board.getSquare(6, 0), board.getSquare(8, 0));
        assertTrue(actual.size() == 3 && actual.containsAll(expected));
        //case 8: x=7, y=9
        actual = board.getAvailableTargets(new Axis[]{Axis._7, Axis._9});
        expected = Arrays.asList(board.getSquare(7, 8), board.getSquare(6, 9), board.getSquare(8, 9));
        assertTrue(actual.size() == 3 && actual.containsAll(expected));
        //case 9: x=2, y=2
        actual = board.getAvailableTargets(new Axis[]{Axis._2, Axis._2});
        expected = Arrays.asList(board.getSquare(2, 1), board.getSquare(1, 2), board.getSquare(2, 3), board.getSquare(3, 2));
        assertTrue(actual.size() == 4 && actual.containsAll(expected));
    }


}
