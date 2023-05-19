package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.game.Game;
import ch.uzh.ifi.hase.soprafs23.game.Lobby;
import ch.uzh.ifi.hase.soprafs23.game.board.Axis;
import ch.uzh.ifi.hase.soprafs23.game.board.Board;
import ch.uzh.ifi.hase.soprafs23.game.board.Square;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;
import ch.uzh.ifi.hase.soprafs23.game.states.GameState;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/rooms/{roomId}/gameState")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameState getGameState(@PathVariable int roomId){
        Game game = this.gameService.findGameByRoomId(roomId);
        GameState gameState = game.getGameState();
        return game.getGameState();
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
    public void operatePiece(@PathVariable int roomId, @PathVariable String playerId, @RequestBody MovingDTO movingDTO) {
        Axis[][] coordinates = DTOMapper.INSTANCE.convertMovingDTOtoCoordinates(movingDTO);
        List<SquareGETDTO> board = this.gameService.operatePiece(roomId, coordinates);
        Game game = Lobby.getInstance().getRoomByRoomId(roomId).getGame();
        System.out.println(roomId);
        System.out.println(playerId);
        SocketMessageDTO messageDTO = this.gameService.getMessage(board, game);
        System.out.println(messageDTO.getCurrentPlayerId());
        // sending back the game status (messageDTO) including current board, player and winner info
        template.convertAndSend("/topic/ongoingGame/"+roomId, messageDTO);
    }

    @GetMapping("rooms/{roomId}/turn")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public long getOperatingPlayer(@PathVariable int roomId) {
        Game game = this.gameService.findGameByRoomId(roomId);
        System.out.println(game.getOperatingPlayer().getArmy().getType());
        return game.getOperatingPlayer().getUserId();
    }

    @PutMapping("rooms/{roomId}/resign")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void resign(@PathVariable int roomId, @RequestBody ResignPutDTO resignPutDTO) {
        Game game = this.gameService.findGameByRoomId(roomId);
        Board board =  game.getBoard();
        List<SquareGETDTO> boardInSquares = new ArrayList<SquareGETDTO>();
        for(int i = 0; i<10; i++) {
            for(int j=0; j<10; j++) {
                boardInSquares.add(DTOMapper.INSTANCE.convertSquareToSquareGETDTO(board.getSquare(i,j)));
            }
        }
        this.gameService.resign(game, resignPutDTO);
        SocketMessageDTO messageDTO = this.gameService.getMessage(boardInSquares, game);
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
        System.out.println(axisX);
        System.out.println(axisY);
        Game game = this.gameService.findGameByRoomId(roomId);
        Axis[] coordinate = new Axis[2];
        coordinate[0] = axisX;
        coordinate[1] = axisY;
        System.out.println(coordinate);
        ArrayList<SquareGETDTO> availableMovements = new ArrayList<>();
        for(Square square: this.gameService.getAvailableMovingOptions(game, coordinate)) {
            availableMovements.add(DTOMapper.INSTANCE.convertSquareToSquareGETDTO(square));
        }
        return availableMovements;
    }

}
