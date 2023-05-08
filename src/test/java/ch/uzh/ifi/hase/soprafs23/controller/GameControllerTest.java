package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.game.Game;
import ch.uzh.ifi.hase.soprafs23.game.Player;
import ch.uzh.ifi.hase.soprafs23.game.army.Army;
import ch.uzh.ifi.hase.soprafs23.game.army.ArmyType;
import ch.uzh.ifi.hase.soprafs23.game.board.Board;
import ch.uzh.ifi.hase.soprafs23.game.board.Square;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;
import ch.uzh.ifi.hase.soprafs23.game.piece.PieceType;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PiecePUTDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;


import static ch.uzh.ifi.hase.soprafs23.game.board.Axis._1;
import static ch.uzh.ifi.hase.soprafs23.game.board.Axis._2;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    @MockBean
    private Player player;
    @MockBean
    private Army army;
    @MockBean
    private Board board;
    @MockBean
    private Game game;

    /*@Test
    public void testGetBoard() throws Exception {
        //Board board = new Board();
        Square square = new Square(_1, _2);
        given(gameService.getOperatingPlayer()).willReturn(player);
        given(player.getArmy()).willReturn(army);
        given(army.getType()).willReturn(ArmyType.BLUE);
        given(board.getSquare(Mockito.anyInt(), Mockito.anyInt())).willReturn(square);
        MockHttpServletRequestBuilder getRequest = MockMvcRequestBuilders.get("/boards").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest)
                .andExpect(status().isOk());
    }*/

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
