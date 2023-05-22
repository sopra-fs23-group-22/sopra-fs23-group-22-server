package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.game.Game;
import ch.uzh.ifi.hase.soprafs23.game.Player;
import ch.uzh.ifi.hase.soprafs23.game.army.Army;
import ch.uzh.ifi.hase.soprafs23.game.army.ArmyType;
import ch.uzh.ifi.hase.soprafs23.game.board.Axis;
import ch.uzh.ifi.hase.soprafs23.game.board.Board;
import ch.uzh.ifi.hase.soprafs23.game.board.Square;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
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
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private Long TEST_PLAYER_1 = 1L;
    private Long TEST_PLAYER_2 = 2L;
    private ArrayList<Long> players;

    @BeforeEach
    void setUp() {
        testGame = mock(Game.class);
        testBoard = new Board();
        given(gameService.findGameByRoomId(Mockito.anyInt())).willReturn(testGame);
        given(testGame.getBoard()).willReturn(testBoard);
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
    public void givenRoomIdAsPathVariable_startGame_thenReturnStateIs_PRE_PLAY() throws Exception {

        given(testGame.getGameState()).willReturn(GameState.PRE_PLAY);

        MockHttpServletRequestBuilder getRequest = get("/rooms/{roomId}/gameState", TEST_ROOM_ID)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("PRE_PLAY")));
    }

    @Test
    public void testEnteringGame_withTwoPlayers_success() throws Exception {
        given(testGame.getGameState()).willReturn(GameState.PRE_PLAY);
        doNothing().when(gameService).enterGame(TEST_ROOM_ID);

        MockHttpServletRequestBuilder enterGame = put("/rooms/{roomId}/game/start", TEST_ROOM_ID)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(enterGame)
                .andExpect(status().isNoContent());
        verify(gameService,Mockito.times(1)).enterGame(TEST_ROOM_ID);
        verify(template).convertAndSend("/topic/room/1/state", GameState.PRE_PLAY);

    }





    @Test
    public void singlePlayerSetConfiguration_notEnteringGame() throws Exception {
        Piece piece = new Piece(PieceType.BOMB, ArmyType.BLUE);
        Piece[] pieces = new Piece[40];
        PiecePUTDTO piecePUTDTO = new PiecePUTDTO();
        piecePUTDTO.setPieceType(PieceType.BOMB);
        piecePUTDTO.setArmyType(ArmyType.BLUE);
        PiecePUTDTO[] piecePUTDTOS = new PiecePUTDTO[40];
        for(int i=0; i<40; i++) {
            piecePUTDTOS[i] = piecePUTDTO;
            pieces[i] = piece;
        }

        // when single player send the configuration to server, the game will not start yet
        given(dtoMapper.convertConfigurationToInitialBoard(piecePUTDTOS)).willReturn(pieces);
        doNothing().when(gameService).setInitialBoard(testGame, pieces);
        doNothing().when(gameService).startGame(TEST_ROOM_ID);
        given(testGame.getGameState()).willReturn(GameState.PRE_PLAY);

        MockHttpServletRequestBuilder putRequest = put("/rooms/{roomId}/setBoard",1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(piecePUTDTOS));

        mockMvc.perform(putRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("PRE_PLAY")));

        verify(template).convertAndSend("/topic/loading/1", GameState.PRE_PLAY);
    }

    @Test
    public void playerMakeAMoveOperation_success() throws Exception {

        Axis[] source = new Axis[2];
        Axis[] target = new Axis[2];
        source[0] = Axis._0;
        source[1] = Axis._0;
        target[0] = Axis._0;
        target[1] = Axis._1;
        MovingDTO testMovingDTO = new MovingDTO();
        testMovingDTO.setSource(source);
        testMovingDTO.setTarget(target);

        // set up request body: moving from 0,0 to 0,1
        Axis[][] coordinates = new Axis[2][2];
        coordinates[0][0] = Axis._0;
        coordinates[0][1] = Axis._0;
        coordinates[1][0] = Axis._0;
        coordinates[1][1] = Axis._1;

        List<SquareGETDTO> returnBoard = new ArrayList<>();
        SquareGETDTO squareGETDTO = new SquareGETDTO();
        PieceGETDTO pieceGETDTO = new PieceGETDTO();
        pieceGETDTO.setArmyType(ArmyType.BLUE);
        pieceGETDTO.setPieceType(PieceType.CAPTAIN);
        squareGETDTO.setContent(pieceGETDTO);
        squareGETDTO.setAxisX(Axis._0);
        squareGETDTO.setAxisY(Axis._0);
        returnBoard.add(squareGETDTO);
        SocketMessageDTO socketMessageDTO = new SocketMessageDTO();
        socketMessageDTO.setBoard(returnBoard);
        socketMessageDTO.setCurrentPlayerId(TEST_PLAYER_1);
        given(dtoMapper.convertMovingDTOtoCoordinates(Mockito.any())).willReturn(coordinates);
        given(gameService.operatePiece(TEST_ROOM_ID, coordinates)).willReturn(returnBoard);
        given(gameService.getMessage(returnBoard, testGame)).willReturn(socketMessageDTO);

        MockHttpServletRequestBuilder putRequest = put("/rooms/{roomId}/players/{playerId}/moving",1, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(testMovingDTO));


        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> messageCaptor = ArgumentCaptor.forClass(Object.class);


        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());


        verify(template).convertAndSend(destinationCaptor.capture(), messageCaptor.capture());

        String actualDestination = destinationCaptor.getValue();
        Object actualMessage = messageCaptor.getValue();

        assertEquals("/topic/ongoingGame/1", actualDestination);
        // TODO: complete the test cases, verify if all values equals, check if the server is working properly first!
    }


    // the initial configuration has changed (it will be sent by user, so we don't need this anymore)
    /*@Test
    public void testSetConfiguration() throws Exception {
        Piece piece = new Piece(PieceType.BOMB, ArmyType.BLUE);
        Piece[] pieces = new Piece[2];
        pieces[0] = piece;
        pieces[1] = piece;
        PiecePUTDTO piecePUTDTO = new PiecePUTDTO();
        PiecePUTDTO[] piecePUTDTOS = new PiecePUTDTO[2];
        piecePUTDTOS[0] = piecePUTDTO;
        piecePUTDTOS[1] = piecePUTDTO;
        given(gameService.findGameByRoomId(Mockito.anyInt())).willReturn(game);
        given(dtoMapper.convertConfigurationToInitialBoard(Mockito.any())).willReturn(pieces);

        mockMvc.perform(put("/rooms/{roomId}/setBoard", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(piecePUTDTOS)))
                .andExpect(status().isNoContent());
    }*/



    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }
}
