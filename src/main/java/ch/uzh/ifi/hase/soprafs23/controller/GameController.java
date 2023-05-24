package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.game.board.Axis;
import ch.uzh.ifi.hase.soprafs23.game.board.Board;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;
import ch.uzh.ifi.hase.soprafs23.game.states.GameState;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @Autowired
    SimpMessagingTemplate template;


    @GetMapping("/rooms/{roomId}/game")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<SquareGETDTO> getBoard(@PathVariable int roomId){
        Board board = this.gameService.findBoardByRoomId(roomId);
        return DTOMapper.INSTANCE.convertBoardToSquareGETDTOList(board);
    }

    @GetMapping("/rooms/{roomId}/gameState")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameState getGameState(@PathVariable int roomId){ return gameService.findGameStateByRoomId(roomId); }

    @PutMapping("/rooms/{roomId}/game/confirmResult")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void confirmResult(@PathVariable int roomId) {
        this.gameService.decrementPendingPlayersConfirmationByRoomId(roomId);
    }

    // Enter a game for game preparing (setting up board configuration)
    // This one only works for the enter game button in room page
    @PutMapping("/rooms/{roomId}/game/start")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void enterGame(@PathVariable int roomId) {
        this.gameService.enterGame(roomId);
        GameState gameState = this.gameService.findGameStateByRoomId(roomId);
        // It sends the game state (which should be PRE_PLAY) to client for redirecting players to game preparation page
        template.convertAndSend("/topic/room/" + roomId + "/state", gameState);
    }


    // Receiving the configuration from client and set the pieces to the board in server
    @PutMapping("/rooms/{roomId}/setBoard")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameState setConfiguration(@PathVariable int roomId, @RequestBody PiecePUTDTO[] configuration){
        Piece[] pieces = DTOMapper.INSTANCE.convertConfigurationToInitialBoard(configuration);
        this.gameService.setInitialBoard(roomId, pieces);
        this.gameService.startGame(roomId);
        GameState gameState = this.gameService.findGameStateByRoomId(roomId);

        // sending the game state to client, so the players can enter the game board page at the same time when both sides finish setting up
        template.convertAndSend("/topic/loading/"+roomId, gameState);
        // also sending the game state to client since the first player should see a spinner when the opponent is not ready yet
        // cannot use web socket here because of some execution order issue
        return gameState;
    }

    // Receiving a moving from client and take action in server
    @PutMapping("/rooms/{roomId}/players/{playerId}/moving")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void operatePiece(@PathVariable int roomId, @PathVariable int playerId, @RequestBody MovingDTO movingDTO) {
        Axis[][] coordinates = DTOMapper.INSTANCE.convertMovingDTOtoCoordinates(movingDTO);
        this.gameService.operatePiece(roomId, coordinates);
        SocketMessageDTO messageDTO = this.gameService.getGameInfo(roomId);
        // sending back the game status (messageDTO) including current board, player and winner info
        template.convertAndSend("/topic/ongoingGame/"+roomId, messageDTO);
    }

    @GetMapping("rooms/{roomId}/turn")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public long getOperatingPlayerId(@PathVariable int roomId) {
        return this.gameService.getOperatingPlayerId(roomId);
    }

    @PutMapping("rooms/{roomId}/resign")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void resign(@PathVariable int roomId, @RequestBody ResignPutDTO resignPutDTO) {
        this.gameService.resign(roomId, resignPutDTO);
        SocketMessageDTO messageDTO = this.gameService.getGameInfo(roomId);
        messageDTO.setPlayerIdResigned(resignPutDTO.getPlayerIdResigned());
        template.convertAndSend("/topic/ongoingGame/"+roomId, messageDTO);
    }

    // e.g: rooms/{roomId}/availableMovements?x=_1&y=_2
    // receive source coordinate as request parameters in url
    @GetMapping("rooms/{roomId}/availableMovements")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<SquareGETDTO> getAvailableMovingOptions(@PathVariable int roomId,
                                                        @RequestParam(value = "x", required = true) Axis axisX,
                                                        @RequestParam(value = "y", required = true) Axis axisY) {
        return gameService.getAvailableMovingOptions(roomId, axisX, axisY);
    }

}
