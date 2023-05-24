package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.game.Game;
import ch.uzh.ifi.hase.soprafs23.game.Room;
import ch.uzh.ifi.hase.soprafs23.game.army.ArmyType;
import ch.uzh.ifi.hase.soprafs23.game.board.Axis;
import ch.uzh.ifi.hase.soprafs23.game.board.Board;
import ch.uzh.ifi.hase.soprafs23.game.board.Square;
import ch.uzh.ifi.hase.soprafs23.game.board.SquareType;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;
import ch.uzh.ifi.hase.soprafs23.game.piece.PieceType;
import ch.uzh.ifi.hase.soprafs23.game.states.GameState;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PieceGETDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.ResignPutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.SocketMessageDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.SquareGETDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    @InjectMocks
    private GameService gameService;
    @Mock
    private RoomService roomService;
    @Mock
    private DTOMapper dtoMapper;
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


    @Test
    public void enterGame_withTwoPlayersInRoom_success() {
        assertEquals(GameState.PRE_PLAY, testGame.getGameState());
    }

    @Test
    public void enterGame_withSinglePlayerInRoom_fail() {
        ArrayList<Long> singlePlayer = new ArrayList<Long>();
        singlePlayer.add(1L);
        testRoom.setUserIds(singlePlayer);
        System.out.println(testRoom.getUserIds());
        assertThrows(ResponseStatusException.class, () -> gameService.enterGame(testRoom.getRoomId()));
    }

    @Test
    public void startGame_withTwoPlayers_success_operatingPlayerIsUser1() {
        assertEquals(GameState.PRE_PLAY, testGame.getGameState());
        setUpMockBoard(testGame.getBoard());
        gameService.startGame(testRoom.getRoomId());
        assertEquals(GameState.IN_PROGRESS, testRoom.getGame().getGameState());

        assertEquals(1L, gameService.getOperatingPlayerId(testRoom.getRoomId()));

        SocketMessageDTO actualMessage = gameService.getGameInfo(testRoom.getRoomId());
        assertEquals(1L, actualMessage.getCurrentPlayerId());
        assertEquals(-1L, actualMessage.getWinnerId());
        assertEquals(-1L, actualMessage.getPlayerIdResigned());
        // check if 0-39: blue scout, 60-99: red scout
        for(int i=0; i<40; i++) {
            assertEquals(SquareType.BATTLE_FIELD,
                    actualMessage.getBoard().get(i).getType());
            assertEquals(ArmyType.BLUE,
                    actualMessage.getBoard().get(i).getContent().getArmyType());
            assertEquals(PieceType.SCOUT,
                    actualMessage.getBoard().get(i).getContent().getPieceType());
            assertEquals(SquareType.BATTLE_FIELD,
                    actualMessage.getBoard().get(i+60).getType());
            assertEquals(ArmyType.RED,
                    actualMessage.getBoard().get(i+60).getContent().getArmyType());
            assertEquals(PieceType.SCOUT,
                    actualMessage.getBoard().get(i+60).getContent().getPieceType());
        }
    }

    @Test
    public void user1Resigned_user2Wins_user1Lost_onePlayer() {
        testGame.setGameState(GameState.IN_PROGRESS);
        assertNull(testGame.getWinner());
        assertNull(testGame.getLoser());

        ResignPutDTO resignPutDTO = new ResignPutDTO();
        resignPutDTO.setPlayerIdResigned(1L);

        gameService.resign(testRoom.getRoomId(), resignPutDTO);

        assertEquals(2L, testGame.getWinner().getUserId());
        assertEquals(1L, testGame.getLoser().getUserId());
        assertEquals(GameState.FINISHED, testGame.getGameState());

        gameService.decrementPendingPlayersConfirmationByRoomId(testRoom.getRoomId());
        gameService.decrementPendingPlayersConfirmationByRoomId(testRoom.getRoomId());

        assertEquals(GameState.WAITING, testGame.getGameState());
    }

//    @Test
//    public void test_get_GameInfo() {
//        testGame.start();
//        assertEquals();
//        SocketMessageDTO actual = gameService.getGameInfo(testRoom.getRoomId());
//        assertEquals(testGame.getOperatingPlayer().getUserId(), actual.getCurrentPlayerId());
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
    private void setUpMockBoard(Board board) {
//        Board testBoard = testGame.getBoard();
        Piece blueScout = new Piece(PieceType.SCOUT, ArmyType.BLUE);
        Piece redScout = new Piece(PieceType.SCOUT, ArmyType.RED);
        for(int i=0; i<10; i++) {
            for(int j=0; j<4; j++) {
                board.setPiece(i,j,blueScout);
                board.setPiece(i, j+6, redScout);
            }
        }
//        System.out.println(board.getSquare(3,0).getContent().getPieceType());
//        System.out.println(board.getSquare(4,0).getContent());
//        System.out.println(testBoard.getSquare(0,1).getContent().getPieceType());
//        System.out.println(testBoard.getSquare(4,1).getContent().getPieceType());
//        System.out.println(testBoard.getSquare(6,1).getContent().getPieceType());
//        System.out.println(testBoard.getSquare(9,1).getContent().getPieceType());
    }

//    // helper
//    private List<SquareGETDTO> setUpMockBoardDTO() {
//        List<SquareGETDTO> mockBoardDTO = new ArrayList<SquareGETDTO>();
//        SquareGETDTO squareGETDTO = setUpMockSquareGETDTO();
//        for(int i=0; i<100; i++) {
//            mockBoardDTO.add(squareGETDTO);
//        }
//        return mockBoardDTO;
//    }
//
//    private SquareGETDTO setUpMockSquareGETDTO() {
//        SquareGETDTO squareGETDTO = new SquareGETDTO();
//        PieceGETDTO pieceGETDTO = new PieceGETDTO();
//        pieceGETDTO.setArmyType(ArmyType.BLUE);
//        pieceGETDTO.setPieceType(PieceType.SCOUT);
//        squareGETDTO.setAxisX(Axis._0);
//        squareGETDTO.setAxisY(Axis._1);
//        squareGETDTO.setType(SquareType.BATTLE_FIELD);
//        squareGETDTO.setContent(pieceGETDTO);
//        return squareGETDTO;
//    }

}