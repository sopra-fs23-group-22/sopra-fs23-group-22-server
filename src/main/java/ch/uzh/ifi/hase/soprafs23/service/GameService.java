package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.game.Game;
import ch.uzh.ifi.hase.soprafs23.game.Lobby;
import ch.uzh.ifi.hase.soprafs23.game.Player;
import ch.uzh.ifi.hase.soprafs23.game.Room;
import ch.uzh.ifi.hase.soprafs23.game.board.Axis;
import ch.uzh.ifi.hase.soprafs23.game.board.Board;
import ch.uzh.ifi.hase.soprafs23.game.board.Square;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;
import ch.uzh.ifi.hase.soprafs23.game.states.GameState;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.ResignPutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.SocketMessageDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.SquareGETDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class GameService {

    private final Logger log = LoggerFactory.getLogger(GameService.class);
    private final UserRepository userRepository;
    private final UserService userService;
    private final RoomService roomService;

    @Autowired
    public GameService(UserRepository userRepository, UserService userService, RoomService roomService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.roomService = roomService;
    }

    public void setInitialBoard(int roomId, Piece[] configuration) {
        Game game = findGameByRoomId(roomId);
        game.placePieces(configuration);
    }

    public Game findGameByRoomId(int roomId) {
        String baseErrorMessage = "Room %s provided is not found!";
        try {
//            Room room = Lobby.getInstance().getRoomByRoomId(roomId);
            Room room = roomService.findRoomById(roomId);
            return room.getGame();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage, roomId));
        }

    }

    //this is a test method
//    public List<SquareGETDTO> operatePiece(int roomId, Axis[][] coordinates) {
//        Game game = findGameByRoomId(roomId);
//        try {
//            game.operate(coordinates[0], coordinates[1]);
//            Board board = game.getBoard();
////            List<SquareGETDTO> squares = new ArrayList<SquareGETDTO>();
////            for(int i = 0; i<10; i++) {
////                for(int j=0; j<10; j++) {
////                    squares.add(DTOMapper.INSTANCE.convertSquareToSquareGETDTO(board.getSquare(i,j)));
////                }
////            }
//            return convertBoardToSquareGETDTOList(board);
//        } catch (IllegalStateException e) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
//        }
//    }

    //this is a test method
    public void operatePiece(int roomId, Axis[][] coordinates) {
        Game game = findGameByRoomId(roomId);
        try {
            game.operate(coordinates[0], coordinates[1]);
//            Board board = game.getBoard();
//            List<SquareGETDTO> squares = new ArrayList<SquareGETDTO>();
//            for(int i = 0; i<10; i++) {
//                for(int j=0; j<10; j++) {
//                    squares.add(DTOMapper.INSTANCE.convertSquareToSquareGETDTO(board.getSquare(i,j)));
//                }
//            }
//            return convertBoardToSquareGETDTOList(board);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public Board findBoardByRoomId(int roomId) {
        Game game = findGameByRoomId(roomId);
        return game.getBoard();
    }

    public GameState findGameStateByRoomId(int roomId) {
         Game game = findGameByRoomId(roomId);
         if (game == null) return GameState.WAITING;
         return game.getGameState();
    }

    // enter game does not mean start game, here we have a game with no piece on board, game status change to PRE_PLAY
    public void enterGame(int roomId) {
        try {
            Room room = Lobby.getInstance().getRoomByRoomId(roomId);
            room.enterGame();
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // start game after all pieces have been placed on board, game status change from PRE_PLAY to IN_PROGRESS
    public void startGame(int roomId) {
//        Room room = Lobby.getInstance().getRoomByRoomId(roomId);
//        Game game = room.getGame();
        Game game = findGameByRoomId(roomId);
        try {
            game.start();
        } catch (IllegalStateException e) {
            // do nothing,
        }
    }

    public long getOperatingPlayerId(int roomId) {
        return findGameByRoomId(roomId).getOperatingPlayer().getUserId();
    }

    // converting required info (current board, player and winner info) to DTO
//    public SocketMessageDTO getMessage(List<SquareGETDTO> board, int roomId) {
    public SocketMessageDTO getGameInfo(int roomId) {
        Game game = findGameByRoomId(roomId);
        List<SquareGETDTO> board = convertBoardToSquareGETDTOList(game.getBoard());
        SocketMessageDTO socketMessageDTO = new SocketMessageDTO();
        socketMessageDTO.setBoard(board);
        socketMessageDTO.setCurrentPlayerId(game.getOperatingPlayer().getUserId());
        if (game.getWinner() != null) {
            socketMessageDTO.setWinnerId(game.getWinner().getUserId());
            // UPDATE: update user stats when one player wins.
            userService.updateStatistics(game.getWinner().getUserId(), game.getLoser().getUserId());
        }
        else socketMessageDTO.setWinnerId(-1L);
        return socketMessageDTO;
    }

    public void resign(int roomId, ResignPutDTO resignPutDTO) {
        Game game = findGameByRoomId(roomId);
        Player playerResigned = game.getPlayerByUserId(resignPutDTO.getPlayerIdResigned());
        game.resign(playerResigned);
    }

    public ArrayList<SquareGETDTO> getAvailableMovingOptions(int roomId, Axis axisX, Axis axisY) {
        Game game = findGameByRoomId(roomId);
        Axis[] coordinate = new Axis[2];
        coordinate[0] = axisX;
        coordinate[1] = axisY;
        ArrayList<Square> availableMovements = game.getAvailableTargets(coordinate);
        return convertSquareListToSquareGETDTOList(availableMovements);
    }

    public void decrementPendingPlayersConfirmationByRoomId(int roomId) {
        findGameByRoomId(roomId).decrementPendingPlayersConfirmation();
    }

    // helper method
    private List<SquareGETDTO> convertBoardToSquareGETDTOList(Board board) {
        List<SquareGETDTO> boardInSquares = new ArrayList<SquareGETDTO>();
        for(int i = 0; i<10; i++) {
            for(int j=0; j<10; j++) {
                boardInSquares.add(DTOMapper.INSTANCE.convertSquareToSquareGETDTO(board.getSquare(i,j)));
            }
        }
        return boardInSquares;
    }

    private ArrayList<SquareGETDTO> convertSquareListToSquareGETDTOList(ArrayList<Square> squares) {
        ArrayList<SquareGETDTO> squareGETDTOS = new ArrayList<>();
        for(Square square: squares) {
            squareGETDTOS.add(DTOMapper.INSTANCE.convertSquareToSquareGETDTO(square));
        }
        return squareGETDTOS;
    }

}
