package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.game.Game;
import ch.uzh.ifi.hase.soprafs23.game.Lobby;
import ch.uzh.ifi.hase.soprafs23.game.Room;
import ch.uzh.ifi.hase.soprafs23.game.army.ArmyType;
import ch.uzh.ifi.hase.soprafs23.game.board.Axis;
import ch.uzh.ifi.hase.soprafs23.game.board.Board;
import ch.uzh.ifi.hase.soprafs23.game.board.Square;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;
import ch.uzh.ifi.hase.soprafs23.game.piece.PieceType;
import ch.uzh.ifi.hase.soprafs23.game.states.GameState;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.ResignPutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoomGetDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebAppConfiguration
@SpringBootTest
public class GameServiceIntegrationTest {
    @Autowired
    private GameService gameService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoomService roomService;

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    User testUser1;
    User testUser2;
    Room testRoom;
    Game testGame;

    @BeforeEach
    public void setup() {
        // create 2 users and a room with the 2 users
        testUser1 = createUserForTest(1L);
        testUser2 = createUserForTest(2L);
        clearLobby();
        RoomGetDTO createdRoomGetDTO = roomService.createRoom(testUser1.getId());
        testRoom = roomService.findRoomById(createdRoomGetDTO.getRoomId());
        testRoom.addUser(testUser2.getId());
    }

    @Test
    public void testWholeGameProcess() {
        /*
        Here in the integration test of GameService, we found that
        it might be hard to fully isolate each method in this class,
        since they represent part of the flow of the game (i.e. from setting up the game,
        placing the initial board, to the end of the game), which means at each step of the game,
        we need to invoke several methods in the earlier steps. Therefore, it would be more
        efficient to test the whole game process in one test method. Such that in each step,
        we test whether the method in this step gives the expected result.
         */


        // test: gameState change when enter game
        gameService.enterGame(testRoom.getRoomId());
        testGame = gameService.findGameByRoomId(testRoom.getRoomId());
        assertEquals(GameState.PRE_PLAY, testGame.getGameState());

        // ...  by the way, here we also test:
        //      findBoardByRoomId()
        //      findGameStateByRoomId()
        assertEquals(GameState.PRE_PLAY, gameService.findGameStateByRoomId(testRoom.getRoomId()));
        assertEquals(testGame.getBoard(), gameService.findBoardByRoomId(testRoom.getRoomId()));


        // test: setup initial board
        // ... setup test initial board configuration 1
        Piece[] redArmy = new Piece[40];
        Piece[] blueArmy = new Piece[40];
        for (int i = 0; i < 39; i++) {
            redArmy[i] = new Piece(PieceType.GENERAL, ArmyType.RED);
            blueArmy[i] = new Piece(PieceType.GENERAL, ArmyType.BLUE);
        }
        redArmy[39] = new Piece(PieceType.FLAG, ArmyType.RED);
        blueArmy[39] = new Piece(PieceType.FLAG, ArmyType.BLUE);
        gameService.setInitialBoard(testRoom.getRoomId(), redArmy);
        gameService.setInitialBoard(testRoom.getRoomId(), blueArmy);

        // test: gameState change when start game
        gameService.startGame(testRoom.getRoomId());
        assertEquals(GameState.IN_PROGRESS, testGame.getGameState());

        // test: available moving options
        Axis[] coordinatesOfExpectedMovingOptions = new Axis[2];
        coordinatesOfExpectedMovingOptions[0] = Axis._4;
        coordinatesOfExpectedMovingOptions[1] = Axis._0;
        ArrayList<Square> expectedMovingOptions = new ArrayList<>();
        expectedMovingOptions.add(testGame.getBoard().getSquareViaAxis(coordinatesOfExpectedMovingOptions));

        Axis[] testSourceAxis = new Axis[2];
        testSourceAxis[0] = Axis._3;
        testSourceAxis[1] = Axis._0;
        ArrayList<Square> actualMovingOptions = testGame.getAvailableTargets(testSourceAxis);

        assertEquals(expectedMovingOptions, actualMovingOptions);

        // test: operate piece (say, to the moving option) and check:
        // ... whether the piece is moved to the target square
        // ... whether operating player changes
        Axis[][] testOperatingAxis = new Axis[2][2];
        testOperatingAxis[0] = testSourceAxis;
        testOperatingAxis[1] = coordinatesOfExpectedMovingOptions;
        Piece pieceBeingMoved = testGame.getBoard().getSquareViaAxis(testSourceAxis).getContent();
        gameService.operatePiece(testRoom.getRoomId(), testOperatingAxis);
        assertEquals(pieceBeingMoved, testGame.getBoard().getSquareViaAxis(coordinatesOfExpectedMovingOptions).getContent());
        assertEquals(null, testGame.getBoard().getSquareViaAxis(testSourceAxis).getContent());
        assertEquals(testUser2.getId(), testGame.getOperatingPlayer().getUserId());

        // test: resign, and check whether the winner is another player
        ResignPutDTO resignPutDTO = new ResignPutDTO();
        resignPutDTO.setPlayerIdResigned(testUser1.getId());
        gameService.resign(testRoom.getRoomId(), resignPutDTO);
        assertEquals(testUser2.getId(), testGame.getWinner().getUserId());

        // test: one of the players / both confirming result
        // ... one of them confirms the game result
        gameService.decrementPendingPlayersConfirmationByRoomId(testRoom.getRoomId());
        assertEquals(GameState.FINISHED, testGame.getGameState());
        gameService.decrementPendingPlayersConfirmationByRoomId(testRoom.getRoomId());
        assertEquals(GameState.WAITING, testGame.getGameState());
    }

    private User createUserForTest(long userId) {
        User newUser = new User();
        newUser.setId(userId);
        newUser.setPassword("testPassword");
        newUser.setStatus(UserStatus.ONLINE);
        newUser.setUsername("testUser" + userId);
        newUser.setToken(UUID.randomUUID().toString());
        userRepository.saveAndFlush(newUser);
        return newUser;
    }

    private void clearLobby() {
        HashMap<Integer, Room> allRooms = Lobby.getInstance().getRooms();
        if (!allRooms.isEmpty()) {
            Iterator<Integer> iterator = allRooms.keySet().iterator();
            while (iterator.hasNext()) {
                int roomId = iterator.next();
                iterator.remove();
                Lobby.getInstance().removeRoom(roomId);
            }
        }
    }
}
