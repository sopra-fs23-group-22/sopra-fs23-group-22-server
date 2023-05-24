package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.game.Game;
import ch.uzh.ifi.hase.soprafs23.game.Room;
import ch.uzh.ifi.hase.soprafs23.game.army.ArmyType;
import ch.uzh.ifi.hase.soprafs23.game.board.Axis;
import ch.uzh.ifi.hase.soprafs23.game.board.Board;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;
import ch.uzh.ifi.hase.soprafs23.game.piece.PieceType;
import ch.uzh.ifi.hase.soprafs23.game.states.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    @InjectMocks
    private GameService gameService;
    @Mock
    private RoomService roomService;
    private Game testGame;
    private Room testRoom;
    private ArrayList<Long> testPlayerIds;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testPlayerIds = new ArrayList<>();
        testPlayerIds.add(1L);
        testPlayerIds.add(2L);

        testRoom = new Room(1);
        testRoom.setUserIds(testPlayerIds);
        testRoom.enterGame();

        testGame = testRoom.getGame();

        Mockito.when(roomService.findRoomById(1)).thenReturn(testRoom);
    }

//    @AfterEach
//    void tearDown() {
//        Lobby.getInstance().removeRoom(testRoom.getRoomId());
//    }

    @Test
    public void givenAValidId_findGame_success() {
        Game actual = gameService.findGameByRoomId(testRoom.getRoomId());
        assertEquals(testGame.getGameId(), actual.getGameId());
        assertEquals(testGame.getGameState(), actual.getGameState());
        assertEquals(testGame.getBoard(), actual.getBoard());
        assertEquals(testGame.getPlayers(), actual.getPlayers());
    }

    @Test
    public void givenInvalidId_throwsException() {
        // room that does not exist -> throw exception
        assertThrows(ResponseStatusException.class, () -> gameService.findGameByRoomId(42));
    }

    @Test
    public void givenPieces_setInitialBoard_success() {
        assertNull(testGame.getBoard().getSquare(0,0).getContent());
        assertNull(testGame.getBoard().getSquare(0,9).getContent());

        Piece[] bluePieces = new Piece[40];
        Piece blueBomb = new Piece(PieceType.BOMB, ArmyType.BLUE);
        for(int i=0; i<40; i++) {
            bluePieces[i] = blueBomb;
        }
        gameService.setInitialBoard(testRoom.getRoomId(), bluePieces);
        assertEquals(PieceType.BOMB, testGame.getBoard().getSquare(0,0).getContent().getPieceType());
        assertEquals(PieceType.BOMB, testGame.getBoard().getSquare(0,9).getContent().getPieceType());
    }

    @Test
    public void givenRoomId_findGame_success() {
        Board actual = gameService.findBoardByRoomId(testRoom.getRoomId());
        assertEquals(testGame.getBoard(), actual);
    }

    @Test
    public void givenRoomId_findGameState_success() {
        GameState actual = gameService.findGameStateByRoomId(testRoom.getRoomId());
        assertEquals(testGame.getGameState(), actual);
    }

//    @Test
//    public void givenRoomId_findOperatingPlayer_success() {
//        testGame.setGameState(GameState.IN_PROGRESS);
//        System.out.println(testGame.getOperatingPlayer());
//    }

////     (3,0) is a blue scout, moving to (4,0) which is an empty square -> success
//    @Test
//    public void movingAPiece_toEmptySquare_operationSuccess() {
//        setUpMockBoard();
//        testGame.setGameState(GameState.IN_PROGRESS);
//        // check: (3,0) -> blue scout, (4,0) -> empty square
//        assertEquals(PieceType.SCOUT, testGame.getBoard().getSquare(3,0).getContent().getPieceType());
//        assertEquals(ArmyType.BLUE, testGame.getBoard().getSquare(3,0).getContent().getArmyType());
//        assertNull(testGame.getBoard().getSquare(4,0).getContent());
//
//        Axis[][] testMoving = new Axis[2][2];
//        testMoving[0][1] = Axis._3;
//        testMoving[0][0] = Axis._0;
//        testMoving[1][1] = Axis._4;
//        testMoving[1][0] = Axis._0;
//
//        System.out.println(testGame.getBoard().getSquareViaAxis(testMoving[0]).getContent());
//        testGame.getBoard().getSquareViaAxis(testMoving[1]).getContent();
//
//        gameService.operatePiece(testRoom.getRoomId(), testMoving);
//        System.out.println(testGame.getBoard().getSquare(3,0).getContent());
//        System.out.println(testGame.getBoard().getSquare(4,0).getContent());
////        System.out.println(testGame.getBoard().getSquare(3,0).getContent().getArmyType());
////        assertNull(testGame.getBoard().getSquare(0,0).getContent());
////        assertEquals(blueScout, testGame.getBoard().getSquare(0,0).getContent());
////        setUpInitialBoardAndStartGame();
//    }




    // helper method
    private void setUpMockBoard() {
        Board testBoard = testGame.getBoard();
        Piece blueScout = new Piece(PieceType.SCOUT, ArmyType.BLUE);
        Piece redScout = new Piece(PieceType.SCOUT, ArmyType.RED);
        for(int i=0; i<10; i++) {
            for(int j=0; j<4; j++) {
                testBoard.setPiece(i,j,blueScout);
                testBoard.setPiece(i, j+6, redScout);
            }
        }
        System.out.println(testBoard.getSquare(3,0).getContent().getPieceType());
        System.out.println(testBoard.getSquare(4,0).getContent());
//        System.out.println(testBoard.getSquare(0,1).getContent().getPieceType());
//        System.out.println(testBoard.getSquare(4,1).getContent().getPieceType());
//        System.out.println(testBoard.getSquare(6,1).getContent().getPieceType());
//        System.out.println(testBoard.getSquare(9,1).getContent().getPieceType());
    }

}