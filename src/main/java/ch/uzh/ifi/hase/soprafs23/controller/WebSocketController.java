package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.game.board.Axis;
import ch.uzh.ifi.hase.soprafs23.rest.dto.BoardGETDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.MovingDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.SquareGETDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.TextMessageDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.util.List;

@RestController
    public class WebSocketController {
        private final GameService gameService;

        public WebSocketController(GameService gameService){this.gameService = gameService;}
        @Autowired
        SimpMessagingTemplate template;

        @PostMapping("/boards")
        public ResponseEntity<Void> sendBoard(@RequestBody BoardGETDTO boardGETDTO) throws JsonProcessingException {
            //convert Object to json
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(boardGETDTO);

            template.convertAndSend("/boards", json);

            return new ResponseEntity<>(HttpStatus.OK);
        }

        @MessageMapping("/sendMessage")
        public void receiveMessage(@Payload TextMessageDTO textMessageDTO) {
            // receive message from client
        }

        // this is a test method
        @MessageMapping("/boards")
        @SendTo("/boards")
        public List<SquareGETDTO> operatePiece(@RequestBody MovingDTO movingDTO) {
            Axis[][] coordinates = DTOMapper.INSTANCE.convertMovingDTOtoCoordinates(movingDTO);
            return gameService.operatePiece(coordinates);
        }


        @SendTo("/boards")
        public TextMessageDTO broadcastMessage(@Payload TextMessageDTO textMessageDTO) {
            return textMessageDTO;
        }
    }

