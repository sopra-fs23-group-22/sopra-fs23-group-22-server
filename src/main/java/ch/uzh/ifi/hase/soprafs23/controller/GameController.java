package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.game.Game;
import ch.uzh.ifi.hase.soprafs23.game.board.Axis;
import ch.uzh.ifi.hase.soprafs23.game.board.Board;
import ch.uzh.ifi.hase.soprafs23.game.board.Square;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;
import ch.uzh.ifi.hase.soprafs23.rest.dto.MovingDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PiecePUTDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.SquareGETDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/boards")
//    @GetMapping("/rooms/{roomId}/game")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<SquareGETDTO> getBoard(){
        // just to test if the endpoints works, need to be modified when we have the rest code
        Board board = gameService.getBoard();
        System.out.println(gameService.getOperatingPlayer().getArmy().getType());
        List<SquareGETDTO> squares = new ArrayList<SquareGETDTO>();
        for(int i = 0; i<10; i++) {
            for(int j=0; j<10; j++) {
//                squares[i][j] = convertSquareToSquareGETDTO(board.getSquare(i, j));
                squares.add(DTOMapper.INSTANCE.convertSquareToSquareGETDTO(board.getSquare(i,j)));
            }
        }
        return squares;
    }

    @PutMapping("/rooms/{roomId}/setBoard")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void setConfiguration(@PathVariable int roomId, @RequestBody PiecePUTDTO[] configuration) {
        // just to test if the endpoints works, need to be modified when we have the rest code
        gameService.createRoom();
        Game game = gameService.findGameByRoomId(roomId);
        Piece[] pieces = DTOMapper.INSTANCE.convertConfigurationToInitialBoard(configuration);
        gameService.setInitialBoard(game, pieces);
    }

//    @PutMapping("/boards")
////    @PutMapping("/rooms/{roomId}/ongoingGame/players/{userId}/operation")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @ResponseBody
//    public void operatePiece(@RequestBody MovingDTO movingDTO) {
//        Axis[][] coordinates = DTOMapper.INSTANCE.convertMovingDTOtoCoordinates(movingDTO);
//        gameService.operatePiece(coordinates);
//    }

}
