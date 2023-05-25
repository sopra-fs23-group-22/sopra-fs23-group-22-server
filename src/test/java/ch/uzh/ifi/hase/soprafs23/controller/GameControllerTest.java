package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.game.Game;
import ch.uzh.ifi.hase.soprafs23.game.army.ArmyType;
import ch.uzh.ifi.hase.soprafs23.game.board.Axis;
import ch.uzh.ifi.hase.soprafs23.game.board.Board;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;
import ch.uzh.ifi.hase.soprafs23.game.piece.PieceType;
import ch.uzh.ifi.hase.soprafs23.game.states.GameState;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameController.class)
public class GameControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SimpMessagingTemplate template;
    @MockBean
    private GameService gameService;
    @MockBean
    private DTOMapper dtoMapper;
    private Game testGame;
    private Board testBoard;

    private int TEST_ROOM_ID = 1;
    private long TEST_PLAYER_1 = 1L;
    private long TEST_PLAYER_2 = 2L;
    private ArrayList<Long> players;
    private GameState gameState;

    @BeforeEach
    void setUp() {
        testGame = new Game();
        testBoard = new Board();
        gameState = GameState.WAITING;
        given(gameService.findGameByRoomId(TEST_ROOM_ID)).willReturn(testGame);
        given(gameService.findBoardByRoomId(TEST_ROOM_ID)).willReturn(testBoard);
        given(gameService.findGameStateByRoomId(TEST_ROOM_ID)).willReturn(gameState);
    }

    @Test
    public void givenRoomIdAsPathVariable_thenReturnBoard() throws Exception {

        MockHttpServletRequestBuilder getRequest = get("/rooms/{roomId}/game", TEST_ROOM_ID)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", is(100)));
    }

    @Test
    public void givenRoomIdAsPathVariable_thenReturnGameState() throws Exception {
        MockHttpServletRequestBuilder getRequest = get("/rooms/{roomId}/gameState", TEST_ROOM_ID)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isString())
                // ... and expect the correct response body to be WAITING
                .andExpect(jsonPath("$", is("WAITING")));
    }

    @Test
    public void givenRoomIdAsPathVariable_thenConfirmResult() throws Exception {
        // ... do nothing when the save method on the mocked Game object is called
        doNothing().when(gameService).decrementPendingPlayersConfirmationByRoomId(TEST_ROOM_ID);

        MockHttpServletRequestBuilder getRequest = put("/rooms/{roomId}/game/confirmResult", TEST_ROOM_ID)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk());
        // ... verify that decrementPendingPlayersConfirmationByRoomId was called exactly once
        verify(gameService, times(1)).decrementPendingPlayersConfirmationByRoomId(TEST_ROOM_ID);
    }

    @Test
    public void givenRoomIdAsPathVariable_thenEnterGame() throws Exception {
        // ... do nothing when the save method on the mocked Game object is called
        doNothing().when(gameService).enterGame(TEST_ROOM_ID);

        MockHttpServletRequestBuilder putRequest = put("/rooms/{roomId}/game/start", TEST_ROOM_ID)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());

        verify(gameService, times(1)).enterGame(TEST_ROOM_ID);
        verify(template).convertAndSend("/topic/room/1/state", GameState.WAITING);
    }

    @Test
    public void givenRoomIdAsPathVariable_thenSetConfiguration() throws Exception {
        // ... mock the behaviour of convertConfigurationToInitialBoard

        Piece[] testPieces = new Piece[1];
        testPieces[0] = new Piece(PieceType.BOMB, ArmyType.BLUE);

        PiecePUTDTO[] testPiecePUTDTOs = new PiecePUTDTO[1];
        testPiecePUTDTOs[0] = new PiecePUTDTO();

        given(dtoMapper.convertConfigurationToInitialBoard(Mockito.any())).willReturn(testPieces);
        doNothing().when(gameService).setInitialBoard(Mockito.anyInt(), Mockito.any());
        doNothing().when(gameService).startGame(Mockito.anyInt());

        MockHttpServletRequestBuilder putRequest = put("/rooms/{roomId}/setBoard", TEST_ROOM_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(testPiecePUTDTOs));

        mockMvc.perform(putRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isString())
                // ... and expect the correct response body to be WAITING
                .andExpect(jsonPath("$", is("WAITING")));


        verify(gameService, times(1)).setInitialBoard(Mockito.anyInt(), Mockito.any());
        verify(gameService, times(1)).startGame(Mockito.anyInt());
        verify(template).convertAndSend("/topic/loading/" + TEST_ROOM_ID, GameState.WAITING);
    }


    @Test
    public void givenRoomIdAndPlayerId_thenOperatePiece() throws Exception {
        // setup the coordinates variable
        Axis[] source = new Axis[2];
        Axis[] target = new Axis[2];
        source[0] = Axis._0;
        source[1] = Axis._0;
        target[0] = Axis._0;
        target[1] = Axis._1;
        MovingDTO movingDTO = new MovingDTO();
        movingDTO.setSource(source);
        movingDTO.setTarget(target);
        Axis[][] coordinates = new Axis[2][1];
        coordinates[0] = movingDTO.getSource();
        coordinates[1] = movingDTO.getTarget();

        // mock the behaviour of convertMovingDTOtoCoordinates
        given(dtoMapper.convertMovingDTOtoCoordinates(movingDTO)).willReturn(coordinates);

        // mock the behaviour of functions invoked by the http request
        doNothing().when(gameService).operatePiece(TEST_ROOM_ID, coordinates);
        // (here I don't care about what's actually inside socketMessageDTO, it's whether it is invoked matters)
        SocketMessageDTO socketMessageDTO = new SocketMessageDTO();
        given(gameService.getGameInfo(TEST_ROOM_ID)).willReturn(socketMessageDTO);

        MockHttpServletRequestBuilder putRequest = put("/rooms/{roomId}/players/{playerId}/moving",
                TEST_ROOM_ID, TEST_PLAYER_1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(movingDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());

        verify(template).convertAndSend("/topic/ongoingGame/" + TEST_ROOM_ID, socketMessageDTO);
    }

    @Test
    public void givenRoomIdAsPathVariable_thenGetPlayerIdOfCurrentTurn() throws Exception {
        // ... mock the behaviour of getPlayerIdOfCurrentTurn
        given(gameService.getOperatingPlayerId(TEST_ROOM_ID)).willReturn(TEST_PLAYER_1);

        MockHttpServletRequestBuilder getRequest = get("/rooms/{roomId}/turn", TEST_ROOM_ID)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                // ... the response body should be a string and equal to TEST_PLAYER_1
                .andExpect(jsonPath("$").isNumber())
                .andExpect(jsonPath("$").value(TEST_PLAYER_1));
    }

    @Test
    public void givenRoomIdAsPathVariable_thenResign() throws Exception {
        ResignPutDTO testResignPutDTO = new ResignPutDTO();
        testResignPutDTO.setPlayerIdResigned(TEST_PLAYER_1);
        SocketMessageDTO socketMessageDTO = new SocketMessageDTO();
        socketMessageDTO.setPlayerIdResigned(TEST_PLAYER_1);

        doNothing().when(gameService).resign(TEST_ROOM_ID, testResignPutDTO);
        given(gameService.getGameInfo(TEST_ROOM_ID)).willReturn(socketMessageDTO);

        MockHttpServletRequestBuilder putRequest = put("/rooms/{roomId}/resign", TEST_ROOM_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(testResignPutDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isOk());

        verify(template).convertAndSend("/topic/ongoingGame/" + TEST_ROOM_ID, socketMessageDTO);
    }

    @Test
    public void givenRoomIdAsPathVariable_thenGetAvailableMovingOptions() throws Exception {
        // mock the input
        Axis TEST_AXIS_X = Axis._0;
        Axis TEST_AXIS_Y = Axis._0;
        // mock the output
        ArrayList<SquareGETDTO> availableMovingOptionsDTO = new ArrayList<>();
        given(dtoMapper.convertBoardToSquareGETDTOList(Mockito.any())).willReturn(availableMovingOptionsDTO);
        given(gameService.getAvailableMovingOptions(TEST_ROOM_ID, TEST_AXIS_X, TEST_AXIS_Y)).willReturn(availableMovingOptionsDTO);

        MockHttpServletRequestBuilder getRequest = get("/rooms/{roomId}/availableMovements",
                TEST_ROOM_ID)
                .param("x", String.valueOf(TEST_AXIS_X.toString())) // assuming Axis has a getValue() method
                .param("y", String.valueOf(TEST_AXIS_Y.toString()))
                .contentType(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                // ... expect the returned value is availableMovingOptionsDTO
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(availableMovingOptionsDTO));

        verify(gameService, times(1)).getAvailableMovingOptions(TEST_ROOM_ID, TEST_AXIS_X, TEST_AXIS_Y);
    }

    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }
}
