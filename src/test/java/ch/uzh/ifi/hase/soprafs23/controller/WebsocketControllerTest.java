package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.game.Player;
import ch.uzh.ifi.hase.soprafs23.game.army.Army;
import ch.uzh.ifi.hase.soprafs23.game.board.Axis;
import ch.uzh.ifi.hase.soprafs23.game.board.Square;
import ch.uzh.ifi.hase.soprafs23.rest.dto.BoardGETDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.MovingDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.SocketMessageDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.SquareGETDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
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

import static ch.uzh.ifi.hase.soprafs23.game.board.Axis._1;
import static ch.uzh.ifi.hase.soprafs23.game.board.Axis._2;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WebSocketController.class)
public class WebsocketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private GameService gameService;

    @MockBean
    private SimpMessagingTemplate template;

    @MockBean
    private DTOMapper dtoMapper;

    @MockBean
    private SocketMessageDTO messageDTO;
    @MockBean
    private Player player;
    @MockBean
    private Army army;




    @Test
    public void performedMoveGivenValidCoordinates() throws Exception {
        //given
        Axis[] source = new Axis[2];
        Axis[] target = new Axis[2];
        Axis[][] coordinates = new Axis[2][1];
        source[0] = _1;
        source[1] = _2;
        target[0] = _1;
        target[1] = _2;

        MovingDTO movingDTO = new MovingDTO();
        movingDTO.setSource(source);
        movingDTO.setTarget(target);

        ArrayList<SquareGETDTO> response = new ArrayList<>();

        given(gameService.operatePiece(Mockito.any())).willReturn(response);
        given(gameService.getMessage(Mockito.any())).willReturn(messageDTO);
        given(gameService.getOperatingPlayer()).willReturn(player);
        given(player.getArmy()).willReturn(army);
        given(dtoMapper.convertMovingDTOtoCoordinates(Mockito.any())).willReturn(coordinates);
        given(army.getType()).willReturn(Mockito.any());




        mockMvc.perform(put("/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(movingDTO)))
                .andExpect(status().isNoContent());

        verify(gameService).operatePiece(Mockito.any());
        verify(template).convertAndSend("/topic/ongoingGame", messageDTO);

    }
    @Test
    public void TestSendBoard() throws Exception {
        BoardGETDTO boardGETDTO = new BoardGETDTO();
        SquareGETDTO squareGETDTO = new SquareGETDTO();
        SquareGETDTO[][] squareGETDTOS = new SquareGETDTO[0][0];
        boardGETDTO.setSquare(squareGETDTOS);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(boardGETDTO);


        mockMvc.perform(post("/boards")
               .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(boardGETDTO)))
                .andExpect(status().isOk());


        verify(template).convertAndSend("/boards", json);

    }


    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }
}
