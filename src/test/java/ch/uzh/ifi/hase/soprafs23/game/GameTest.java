package ch.uzh.ifi.hase.soprafs23.game;

import ch.uzh.ifi.hase.soprafs23.game.army.ArmyType;
import ch.uzh.ifi.hase.soprafs23.game.board.Axis;
import ch.uzh.ifi.hase.soprafs23.game.board.Board;
import ch.uzh.ifi.hase.soprafs23.game.board.SquareType;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;
import ch.uzh.ifi.hase.soprafs23.game.piece.PieceType;
import ch.uzh.ifi.hase.soprafs23.game.states.AliveState;
import ch.uzh.ifi.hase.soprafs23.game.states.GameState;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class GameTest {


    @Test
    public void testGameSetup() {
        Game game = new Game();
        ArrayList<Long> input = new ArrayList<>();
        input.add(1L);
        input.add(2L);
        game.setup(input);
        //check if the game has been put in gamestate Pre play
        assert (game.getGameState().equals(GameState.PRE_PLAY));

        //check if Board initialisation worked
        //if this square is of type lake we know that the board was set up correctly
        assert (game.getBoard().getSquare(4, 2).getSquareType().equals(SquareType.LAKE));
    }

    @Test
    public void testPlacePieces() {
        Game game = new Game();
        ArrayList<Long> input = new ArrayList<>();
        input.add(1L);
        input.add(2L);
        game.setup(input);
        Piece[] pieces = new Piece[40];
        for (int i = 0; i < 40; i++) {
            pieces[i] = new Piece(PieceType.BOMB, ArmyType.BLUE);

        }

        game.placePieces(pieces);
        //check the outer points of the blue army
        assert (game.getBoard().getSquare(0, 0).getContent().getPieceType().equals(PieceType.BOMB));
        assert (game.getBoard().getSquare(3, 9).getContent().getPieceType().equals(PieceType.BOMB));
        //check the outer points of where red pieces would be placed
        assertNull(game.getBoard().getSquare(6, 0).getContent());

    }


    @Test
    public void testSwitchTurn() {
        Game game = new Game();
        ArrayList<Long> input = new ArrayList<>();
        input.add(1L);
        input.add(2L);
        game.setup(input);
        Piece blue = new Piece(PieceType.BOMB, ArmyType.BLUE);
        Piece red = new Piece(PieceType.BOMB, ArmyType.RED);
        Piece[] redArmy = new Piece[40];
        Piece[] blueArmy = new Piece[40];
        for (int i = 0; i < 40; i++) {
            redArmy[i] = red;
            blueArmy[i] = blue;
        }

        Player player1 = game.getPlayerByUserId(1L);
        Player player2 = game.getPlayerByUserId(2L);
        game.placePieces(redArmy);
        game.placePieces(blueArmy);
        game.start();
        assertSame(game.getOperatingPlayer(), player1);
        game.switchTurn();
        assertSame(game.getOperatingPlayer(), player2);
        game.switchTurn();
        assertSame(game.getOperatingPlayer(), player1);
    }

    @Test
    public void testHasWinner_FLAGDOWN() {
        //set game up
        Game game = new Game();
        ArrayList<Long> input = new ArrayList<>();
        input.add(1L);
        input.add(2L);
        game.setup(input);
        Player player1 = game.getPlayerByUserId(1L);
        Player player2 = game.getPlayerByUserId(2L);
        Piece blue = new Piece(PieceType.CAPTAIN, ArmyType.BLUE);
        Piece blueFlag = new Piece(PieceType.FLAG, ArmyType.BLUE);
        Piece red = new Piece(PieceType.CAPTAIN, ArmyType.RED);
        Piece redFlag = new Piece(PieceType.FLAG, ArmyType.RED);
        Piece[] redArmy = new Piece[40];
        Piece[] blueArmy = new Piece[40];
        for (int i = 0; i < 39; i++) {
            redArmy[i] = red;
            blueArmy[i] = blue;
        }
        redArmy[39] = redFlag;
        blueArmy[39] = blueFlag;
        game.placePieces(redArmy);
        game.placePieces(blueArmy);
        game.start();
        assertFalse(game.hasWinner());
        redFlag.setAliveState(AliveState.DOWN);
        assertTrue(game.hasWinner());
        assertSame(game.getWinner(), player2);
        assertSame(game.getLoser(), player1);

    }

    @Test
    public void testHasWinner_NOMOVINGPIECES() {
        //set game up
        Game game = new Game();
        ArrayList<Long> input = new ArrayList<>();
        input.add(1L);
        input.add(2L);
        game.setup(input);
        Player player1 = game.getPlayerByUserId(1L);
        Player player2 = game.getPlayerByUserId(2L);
        Piece blue = new Piece(PieceType.FLAG, ArmyType.BLUE);
        Piece blueCaptain = new Piece(PieceType.CAPTAIN, ArmyType.BLUE);
        Piece red = new Piece(PieceType.FLAG, ArmyType.RED);
        Piece redCaptain = new Piece(PieceType.CAPTAIN, ArmyType.RED);
        Piece[] redArmy = new Piece[40];
        Piece[] blueArmy = new Piece[40];
        for (int i = 0; i < 39; i++) {
            redArmy[i] = red;
            blueArmy[i] = blue;
        }
        redArmy[39] = redCaptain;
        blueArmy[39] = blueCaptain;
        game.placePieces(redArmy);
        game.placePieces(blueArmy);
        game.start();
        assertFalse(game.hasWinner());
        blueCaptain.setAliveState(AliveState.DOWN);
        assertTrue(game.hasWinner());
        assertSame(game.getWinner(), player1);
        assertSame(game.getLoser(), player2);

    }

    @Test
    public void testResign() {
        //setup
        Game game = new Game();
        ArrayList<Long> input = new ArrayList<>();
        input.add(1L);
        input.add(2L);
        game.setup(input);
        Player player1 = game.getPlayerByUserId(1L);
        Player player2 = game.getPlayerByUserId(2L);


        game.resign(player1);
        assertSame(game.getWinner(), player2);
        assertSame(game.getLoser(), player1);
        assertEquals(game.getGameState(), GameState.FINISHED);

        game.resign(player2);
        assertSame(game.getWinner(), player1);
        assertSame(game.getLoser(), player2);
    }

    @Test
    public void testOperate_GAMENOTINPROGRESS() {
        Game game = new Game();
        ArrayList<Long> input = new ArrayList<>();
        input.add(1L);
        input.add(2L);
        game.setup(input);
        assertThrows(IllegalStateException.class, () -> game.operate(new Axis[]{Axis._0, Axis._4}, new Axis[]{Axis._5, Axis._1}));
    }

    @Test
    public void testOperate_EMPTYSQUARE() {
        //setup
        Game game = new Game();
        ArrayList<Long> input = new ArrayList<>();
        input.add(1L);
        input.add(2L);
        game.setup(input);
        Piece blue = new Piece(PieceType.CAPTAIN, ArmyType.BLUE);
        Piece blueFlag = new Piece(PieceType.FLAG, ArmyType.BLUE);
        Piece red = new Piece(PieceType.CAPTAIN, ArmyType.RED);
        Piece redFlag = new Piece(PieceType.FLAG, ArmyType.RED);
        Piece[] redArmy = new Piece[40];
        Piece[] blueArmy = new Piece[40];
        for (int i = 0; i < 39; i++) {
            redArmy[i] = red;
            blueArmy[i] = blue;
        }
        redArmy[39] = redFlag;
        blueArmy[39] = blueFlag;
        game.placePieces(redArmy);
        game.placePieces(blueArmy);
        game.start();

        assertThrows(IllegalStateException.class, () -> game.operate(new Axis[]{Axis._0, Axis._5}, new Axis[]{Axis._1, Axis._5}));
    }

    @Test
    public void testOperate_SCOUT_EXCEPTIONS() {
        //setup
        Game game = new Game();
        ArrayList<Long> input = new ArrayList<>();
        input.add(1L);
        input.add(2L);
        game.setup(input);
        Piece blue = new Piece(PieceType.SCOUT, ArmyType.BLUE);
        Piece blueFlag = new Piece(PieceType.FLAG, ArmyType.BLUE);
        Piece red = new Piece(PieceType.SCOUT, ArmyType.RED);
        Piece redFlag = new Piece(PieceType.FLAG, ArmyType.RED);
        Piece[] redArmy = new Piece[40];
        Piece[] blueArmy = new Piece[40];
        for (int i = 0; i < 39; i++) {
            redArmy[i] = red;
            blueArmy[i] = blue;
        }
        redArmy[39] = redFlag;
        blueArmy[39] = blueFlag;
        game.placePieces(redArmy);
        game.placePieces(blueArmy);
        game.start();
        assertThrows(IllegalStateException.class, () -> game.operate(new Axis[]{Axis._4, Axis._2}, new Axis[]{Axis._4, Axis._6}));
        assertThrows(IllegalStateException.class, () -> game.operate(new Axis[]{Axis._3, Axis._3}, new Axis[]{Axis._3, Axis._5}));
    }

    @Test
    public void testOperate_BOMBANDFLAG() {
        //setup
        Game game = new Game();
        ArrayList<Long> input = new ArrayList<>();
        input.add(1L);
        input.add(2L);
        game.setup(input);
        Piece blue = new Piece(PieceType.BOMB, ArmyType.BLUE);
        Piece blueFlag = new Piece(PieceType.FLAG, ArmyType.BLUE);
        Piece red = new Piece(PieceType.BOMB, ArmyType.RED);
        Piece redFlag = new Piece(PieceType.FLAG, ArmyType.RED);
        Piece[] redArmy = new Piece[40];
        Piece[] blueArmy = new Piece[40];
        Board board = game.getBoard();
        for (int i = 0; i < 39; i++) {
            redArmy[i] = red;
            blueArmy[i] = blue;
        }
        redArmy[39] = redFlag;
        blueArmy[39] = blueFlag;
        game.placePieces(redArmy);
        game.placePieces(blueArmy);
        game.start();
        //try to move the flag
        assertEquals(board.getPieceViaAxis(new Axis[]{Axis._3, Axis._9}).getPieceType(), PieceType.FLAG);
        assertThrows(IllegalStateException.class, () -> game.operate(new Axis[]{Axis._3, Axis._9}, new Axis[]{Axis._9, Axis._4}));
        //try to move a bomb
        assertEquals(board.getPieceViaAxis(new Axis[]{Axis._3, Axis._5}).getPieceType(), PieceType.BOMB);
        assertThrows(IllegalStateException.class, () -> game.operate(new Axis[]{Axis._3, Axis._5}, new Axis[]{Axis._5, Axis._4}));
    }

    @Test
    public void testOperate_ILLEGAL_MOVE_TO_OCCUPIED_SQUARE() {
        //setup
        Game game = new Game();
        ArrayList<Long> input = new ArrayList<>();
        input.add(1L);
        input.add(2L);
        game.setup(input);
        Piece blue = new Piece(PieceType.CAPTAIN, ArmyType.BLUE);
        Piece blueFlag = new Piece(PieceType.FLAG, ArmyType.BLUE);
        Piece red = new Piece(PieceType.CAPTAIN, ArmyType.RED);
        Piece redFlag = new Piece(PieceType.FLAG, ArmyType.RED);
        Piece[] redArmy = new Piece[40];
        Piece[] blueArmy = new Piece[40];
        for (int i = 0; i < 39; i++) {
            redArmy[i] = red;
            blueArmy[i] = blue;
        }
        redArmy[39] = redFlag;
        blueArmy[39] = blueFlag;
        game.placePieces(redArmy);
        game.placePieces(blueArmy);
        game.start();

        assertThrows(IllegalStateException.class, () -> game.operate(new Axis[]{Axis._3, Axis._4}, new Axis[]{Axis._6, Axis._4}));
    }

    @Test
    public void testOperate_FRIENDLY_FIRE() {
        //setup
        Game game = new Game();
        ArrayList<Long> input = new ArrayList<>();
        input.add(1L);
        input.add(2L);
        game.setup(input);
        Piece blue = new Piece(PieceType.CAPTAIN, ArmyType.BLUE);
        Piece blueFlag = new Piece(PieceType.FLAG, ArmyType.BLUE);
        Piece red = new Piece(PieceType.CAPTAIN, ArmyType.RED);
        Piece redFlag = new Piece(PieceType.FLAG, ArmyType.RED);
        Piece[] redArmy = new Piece[40];
        Piece[] blueArmy = new Piece[40];
        for (int i = 0; i < 39; i++) {
            redArmy[i] = red;
            blueArmy[i] = blue;
        }
        redArmy[39] = redFlag;
        blueArmy[39] = blueFlag;
        game.placePieces(redArmy);
        game.placePieces(blueArmy);
        game.start();

        assertThrows(IllegalStateException.class, () -> game.operate(new Axis[]{Axis._3, Axis._4}, new Axis[]{Axis._3, Axis._5}));
    }

    @Test
    public void testOperate_VALID_ATTACK() {
        //setup
        Game game = new Game();
        ArrayList<Long> input = new ArrayList<>();
        input.add(1L);
        input.add(2L);
        game.setup(input);
        Player player1 = game.getPlayerByUserId(1L);
        Player player2 = game.getPlayerByUserId(2L);
        Piece blue = new Piece(PieceType.GENERAL, ArmyType.BLUE);
        Piece blueFlag = new Piece(PieceType.FLAG, ArmyType.BLUE);
        Piece red = new Piece(PieceType.CAPTAIN, ArmyType.RED);
        Piece redFlag = new Piece(PieceType.FLAG, ArmyType.RED);
        Piece[] redArmy = new Piece[40];
        Piece[] blueArmy = new Piece[40];
        Board board = game.getBoard();
        for (int i = 0; i < 39; i++) {
            redArmy[i] = red;
            blueArmy[i] = blue;
        }
        redArmy[39] = redFlag;
        blueArmy[39] = blueFlag;
        game.placePieces(redArmy);
        game.placePieces(blueArmy);
        game.start();
        board.setPiece(4, 5, blue);
        assertNotNull(board.getSquareViaAxis(new Axis[]{Axis._5, Axis._4}).getContent());
        assertEquals(board.getSquareViaAxis(new Axis[]{Axis._5, Axis._4}).getContent().getArmyType(), ArmyType.BLUE);
        assertEquals(board.getSquareViaAxis(new Axis[]{Axis._6, Axis._4}).getContent().getArmyType(), ArmyType.RED);
        assertSame(player1, game.getOperatingPlayer());
        game.operate(new Axis[]{Axis._5, Axis._4}, new Axis[]{Axis._6, Axis._4});
        assertNull(board.getSquareViaAxis(new Axis[]{Axis._5, Axis._4}).getContent());
        assertEquals(board.getSquareViaAxis(new Axis[]{Axis._6, Axis._4}).getContent().getArmyType(), ArmyType.BLUE);
        assertSame(player2, game.getOperatingPlayer());
    }

    @Test
    public void testOperate_VALID_MOVE() {
        //setup
        Game game = new Game();
        ArrayList<Long> input = new ArrayList<>();
        input.add(1L);
        input.add(2L);
        game.setup(input);
        Player player1 = game.getPlayerByUserId(1L);
        Player player2 = game.getPlayerByUserId(2L);
        Piece blue = new Piece(PieceType.CAPTAIN, ArmyType.BLUE);
        Piece blueFlag = new Piece(PieceType.FLAG, ArmyType.BLUE);
        Piece red = new Piece(PieceType.CAPTAIN, ArmyType.RED);
        Piece redFlag = new Piece(PieceType.FLAG, ArmyType.RED);
        Piece[] redArmy = new Piece[40];
        Piece[] blueArmy = new Piece[40];
        Board board = game.getBoard();
        for (int i = 0; i < 39; i++) {
            redArmy[i] = red;
            blueArmy[i] = blue;
        }
        redArmy[39] = redFlag;
        blueArmy[39] = blueFlag;
        game.placePieces(redArmy);
        game.placePieces(blueArmy);
        game.start();
        assertSame(player1, game.getOperatingPlayer());
        assertNotNull(board.getSquareViaAxis(new Axis[]{Axis._3, Axis._4}).getContent());
        assertNull(board.getSquareViaAxis(new Axis[]{Axis._4, Axis._4}).getContent());
        game.operate(new Axis[]{Axis._3, Axis._4}, new Axis[]{Axis._4, Axis._4});
        assertNotNull(board.getSquareViaAxis(new Axis[]{Axis._4, Axis._4}).getContent());
        assertNull(board.getSquareViaAxis(new Axis[]{Axis._3, Axis._4}).getContent());
        assertSame(player2, game.getOperatingPlayer());
    }

    @Test
    public void testOperate_FAILEDMOVE() {
        //setup
        Game game = new Game();
        ArrayList<Long> input = new ArrayList<>();
        input.add(1L);
        input.add(2L);
        game.setup(input);
        Player player1 = game.getPlayerByUserId(1L);
        Piece blue = new Piece(PieceType.CAPTAIN, ArmyType.BLUE);
        Piece blueFlag = new Piece(PieceType.FLAG, ArmyType.BLUE);
        Piece red = new Piece(PieceType.CAPTAIN, ArmyType.RED);
        Piece redFlag = new Piece(PieceType.FLAG, ArmyType.RED);
        Piece[] redArmy = new Piece[40];
        Piece[] blueArmy = new Piece[40];
        Board board = game.getBoard();
        for (int i = 0; i < 39; i++) {
            redArmy[i] = red;
            blueArmy[i] = blue;
        }
        redArmy[39] = redFlag;
        blueArmy[39] = blueFlag;
        game.placePieces(redArmy);
        game.placePieces(blueArmy);
        game.start();
        assertSame(player1, game.getOperatingPlayer());
        assertNotNull(board.getSquareViaAxis(new Axis[]{Axis._3, Axis._4}).getContent());
        assertNull(board.getSquareViaAxis(new Axis[]{Axis._5, Axis._4}).getContent());
        assertThrows(IllegalStateException.class, () -> game.operate(new Axis[]{Axis._3, Axis._4}, new Axis[]{Axis._5, Axis._4}));
        assertNotNull(board.getSquareViaAxis(new Axis[]{Axis._3, Axis._4}).getContent());
        assertNull(board.getSquareViaAxis(new Axis[]{Axis._5, Axis._4}).getContent());
        assertSame(player1, game.getOperatingPlayer());
    }
}
