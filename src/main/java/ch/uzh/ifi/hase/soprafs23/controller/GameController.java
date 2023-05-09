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
//        Board board = this.gameService.findBoardByRoomId(roomId);
        Game game = this.gameService.findGameByRoomId(roomId);
        Board board = game.getBoard();
        return DTOMapper.INSTANCE.convertBoardToSquareGETDTOList(board);
    }

    @PutMapping("rooms/{roomId}/game/start")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void enterGame(@PathVariable int roomId) {
        this.gameService.enterGame(roomId);
        template.convertAndSend("/topic/room/" + roomId + "/state", "preplay");
    }


//    @PutMapping("rooms/{roomId}/start")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @ResponseBody
//    public void startGame(@PathVariable int roomId) {
//        this.gameService.startGame(roomId);
//    }

    @PutMapping("/rooms/{roomId}/setBoard")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameState setConfiguration(@PathVariable int roomId, @RequestBody PiecePUTDTO[] configuration){
        Piece[] pieces = DTOMapper.INSTANCE.convertConfigurationToInitialBoard(configuration);
        Game game = this.gameService.findGameByRoomId(roomId);
//        System.out.println(game.getBoard().getSquare(0,0).getContent());
        this.gameService.setInitialBoard(game, pieces);
//        System.out.println(game.getBoard().getSquare(0,0).getContent().getPieceType());
        try {
            this.gameService.startGame(roomId);
        } catch (Exception e) {
            System.out.println("fail");
        }
        template.convertAndSend("/topic/loading/"+roomId, game.getGameState());
        return game.getGameState();
    }

    @PutMapping("/rooms/{roomId}/players/{playerId}/moving")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
//        public List<SquareGETDTO> operatePiece(@RequestBody MovingDTO movingDTO) {
    public void operatePiece(@PathVariable int roomId, @RequestBody MovingDTO movingDTO) {
        Axis[][] coordinates = DTOMapper.INSTANCE.convertMovingDTOtoCoordinates(movingDTO);
        List<SquareGETDTO> board = this.gameService.operatePiece(roomId, coordinates);
        Game game = Lobby.getInstance().getRoomByRoomId(roomId).getGame();

        SocketMessageDTO messageDTO = this.gameService.getMessage(board, game);
        System.out.println(this.gameService.getOperatingPlayer(game).getArmy().getType());
        template.convertAndSend("/topic/ongoingGame/"+roomId, messageDTO);
    }

}
