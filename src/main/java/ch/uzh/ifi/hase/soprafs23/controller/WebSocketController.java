package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.BoardGETDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.TextMessageDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
    public class WebSocketController {

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


        @SendTo("/boards")
        public TextMessageDTO broadcastMessage(@Payload TextMessageDTO textMessageDTO) {
            return textMessageDTO;
        }
    }

