package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.rest.dto.BoardGETDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.TextMessageDTO;
import ch.uzh.ifi.hase.soprafs23.service.WebsocketService;
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

@RestController
    public class WebSocketController {
        private final WebsocketService websocketService = new WebsocketService();
        @Autowired
        SimpMessagingTemplate template;

        @PostMapping("/ongoingGame")
        public ResponseEntity<Void> sendBoard(@RequestBody BoardGETDTO boardGETDTO) throws JsonProcessingException {

            template.convertAndSend("/ongoingGame", websocketService.ObjectToJson(boardGETDTO));

            return new ResponseEntity<>(HttpStatus.OK);
        }

        @MessageMapping("/sendMessage")
        public void receiveMessage(@Payload BoardGETDTO boardGETDTO) {
            // receive message from client
        }


        @SendTo("/ongoingGame")
        public BoardGETDTO broadcastMessage(@Payload BoardGETDTO boardGETDTO) {
            return boardGETDTO;
        }
    }

