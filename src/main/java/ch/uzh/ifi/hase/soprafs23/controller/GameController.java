package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.game.Game;
import ch.uzh.ifi.hase.soprafs23.game.Lobby;
import ch.uzh.ifi.hase.soprafs23.game.Room;
import ch.uzh.ifi.hase.soprafs23.game.board.Axis;
import ch.uzh.ifi.hase.soprafs23.game.board.Board;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;
import ch.uzh.ifi.hase.soprafs23.game.states.GameState;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }
    @Autowired
    SimpMessagingTemplate template;
//    @GetMapping("/rooms/{roomId}/players/{playerId}")


    @GetMapping("/rooms/{roomId}/game")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<SquareGETDTO> getBoard(@PathVariable int roomId){
        Game game = this.gameService.findGameByRoomId(roomId);
        Board board = game.getBoard();
        return DTOMapper.INSTANCE.convertBoardToSquareGETDTOList(board);
    }


    // Enter a game for game preparing (setting up board configuration)
    // This one only works for the enter game button in room page
    @PutMapping("rooms/{roomId}/game/start")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void enterGame(@PathVariable int roomId) {
        this.gameService.enterGame(roomId);
        Game game = this.gameService.findGameByRoomId(roomId);
        // It sends the game state (which should be PRE_PLAY) to client for redirecting players to game preparation page
        template.convertAndSend("/topic/room/" + roomId + "/state", game.getGameState());
    }


    // Receiving the configuration from client and set the pieces to the board in server
    @PutMapping("/rooms/{roomId}/setBoard")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameState setConfiguration(@PathVariable int roomId, @RequestBody PiecePUTDTO[] configuration){
        Piece[] pieces = DTOMapper.INSTANCE.convertConfigurationToInitialBoard(configuration);
        Game game = this.gameService.findGameByRoomId(roomId);
        this.gameService.setInitialBoard(game, pieces);
        try {
            this.gameService.startGame(roomId);
        } catch (Exception e) {
            // the catch block works for debugging, might be changed later when we've defined all error messages
            System.out.println("fail");
        }
        // sending the game state to client, so the players can enter the game board page at the same time when both sides finish setting up
        template.convertAndSend("/topic/loading/"+roomId, game.getGameState());
        // also sending the game state to client since the first player should see a spinner when the opponent is not ready yet
        // cannot use web socket here because of some execution order issue
        return game.getGameState();
    }

    // Receiving a moving from client and take action in server
    @PutMapping("/rooms/{roomId}/players/{playerId}/moving")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void operatePiece(@PathVariable int roomId, @RequestBody MovingDTO movingDTO) {
        Axis[][] coordinates = DTOMapper.INSTANCE.convertMovingDTOtoCoordinates(movingDTO);
        List<SquareGETDTO> board = this.gameService.operatePiece(roomId, coordinates);
        Game game = Lobby.getInstance().getRoomByRoomId(roomId).getGame();

        SocketMessageDTO messageDTO = this.gameService.getMessage(board, game);
        // sending back the game status (messageDTO) including current board, player and winner info
        template.convertAndSend("/topic/ongoingGame/"+roomId, messageDTO);
    }

}
