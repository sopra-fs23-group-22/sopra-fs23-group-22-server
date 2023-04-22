package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.game.Game;
import ch.uzh.ifi.hase.soprafs23.game.board.Board;
import ch.uzh.ifi.hase.soprafs23.game.board.Square;
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
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<SquareGETDTO> getBoard(){
        Board board = gameService.createBoard();
        List<SquareGETDTO> squares = new ArrayList<SquareGETDTO>();
        for(int i = 0; i<10; i++) {
            for(int j=0; j<10; j++) {
//                squares[i][j] = convertSquareToSquareGETDTO(board.getSquare(i, j));
                squares.add(DTOMapper.INSTANCE.convertSquareToSquareGETDTO(board.getSquare(i,j)));
            }
        }
        return squares;
    }

}
