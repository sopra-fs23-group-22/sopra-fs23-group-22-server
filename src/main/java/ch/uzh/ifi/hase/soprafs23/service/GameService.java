package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.game.Game;
import ch.uzh.ifi.hase.soprafs23.game.Lobby;
import ch.uzh.ifi.hase.soprafs23.game.Player;
import ch.uzh.ifi.hase.soprafs23.game.Room;
import ch.uzh.ifi.hase.soprafs23.game.army.ArmyType;
import ch.uzh.ifi.hase.soprafs23.game.board.Axis;
import ch.uzh.ifi.hase.soprafs23.game.board.Board;
import ch.uzh.ifi.hase.soprafs23.game.board.Square;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;
import ch.uzh.ifi.hase.soprafs23.game.piece.PieceType;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.ResignPutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.SocketMessageDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.SquareGETDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class GameService {

    private final Logger log = LoggerFactory.getLogger(GameService.class);
    private final UserRepository userRepository;

    @Autowired
    public GameService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setInitialBoard(Game game, Piece[] configuration) {
        game.placePieces(configuration);
    }

    public Game findGameByRoomId(int roomId) {
        String baseErrorMessage = "Room %s provided is not found!";
        try {
            Room room = Lobby.getInstance().getRoomByRoomId(roomId);
            return room.getGame();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage, roomId));
        }

    }

    //this is a test method
    public List<SquareGETDTO> operatePiece(int roomId, Axis[][] coordinates) {
        Game game = findGameByRoomId(roomId);
        try {
            game.operate(coordinates[0], coordinates[1]);
            Board board = game.getBoard();
            List<SquareGETDTO> squares = new ArrayList<SquareGETDTO>();
            for(int i = 0; i<10; i++) {
                for(int j=0; j<10; j++) {
                    squares.add(DTOMapper.INSTANCE.convertSquareToSquareGETDTO(board.getSquare(i,j)));
                }
            }
            return squares;
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public Board findBoardByRoomId(int roomId) {
        Game game = findGameByRoomId(roomId);
        return game.getBoard();
    }

    // enter game does not mean start game, here we have a game with no piece on board, game status change to PRE_PLAY
    public void enterGame(int roomId) {
        Room room = Lobby.getInstance().getRoomByRoomId(roomId);
        room.enterGame();
    }

    // start game after all pieces have been placed on board, game status change from PRE_PLAY to IN_PROGRESS
    public void startGame(int roomId) {
        Room room = Lobby.getInstance().getRoomByRoomId(roomId);
        Game game = room.getGame();
        game.start();
    }

    public Player getOperatingPlayer(Game game) {
        return game.getOperatingPlayer();
    }

    // converting required info (current board, player and winner info) to DTO
    public SocketMessageDTO getMessage(List<SquareGETDTO> board, Game game) {
        SocketMessageDTO socketMessageDTO = new SocketMessageDTO();
        socketMessageDTO.setBoard(board);
        socketMessageDTO.setCurrentPlayerId(game.getOperatingPlayer().getUserId());
        if (game.getWinner() != null)
            socketMessageDTO.setWinnerId(game.getWinner().getUserId());
        else socketMessageDTO.setWinnerId(-1L);
        return socketMessageDTO;
    }

    public void resign(Game game, ResignPutDTO resignPutDTO) {
        Player playerResigned = game.getPlayerByUserId(resignPutDTO.getPlayerIdResigned());
        game.resign(playerResigned);
    }

    public ArrayList<Square> getAvailableMovingOptions(Game game, Axis[] sourceSquareCoordinates) {
        return game.getAvailableTargets(sourceSquareCoordinates);
    }
}
