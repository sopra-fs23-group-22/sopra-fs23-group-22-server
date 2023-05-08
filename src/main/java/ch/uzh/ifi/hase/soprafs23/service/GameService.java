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
import ch.uzh.ifi.hase.soprafs23.rest.dto.SocketMessageDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.SquareGETDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        Room room = Lobby.getInstance().getRoomByRoomId(roomId);
        return room.getGame();
    }

    public void createRoom(){
        Lobby.getInstance().createRoom();
    }

    //this is a test method
    public List<SquareGETDTO> operatePiece(int roomId, Axis[][] coordinates) {
        Game game = findGameByRoomId(roomId);
        game.operate(coordinates[0], coordinates[1]);
        Board board = game.getBoard();
        List<SquareGETDTO> squares = new ArrayList<SquareGETDTO>();
        for(int i = 0; i<10; i++) {
            for(int j=0; j<10; j++) {
                squares.add(DTOMapper.INSTANCE.convertSquareToSquareGETDTO(board.getSquare(i,j)));
            }
        }
        return squares;
    }

    public Board findBoardByRoomId(int roomId) {
        Game game = findGameByRoomId(roomId);
        return game.getBoard();
    }

    public void enterGame(int roomId) {
        Room room = Lobby.getInstance().getRoomByRoomId(roomId);
//        Game game = room.getGame();
        room.enterGame();
//        game.setup(room.getUserIds());
    }

    public void startGame(int roomId) {
        Room room = Lobby.getInstance().getRoomByRoomId(roomId);
        Game game = room.getGame();
//        game.setup(room.getUserIds());
        game.start();
    }

    public Player getOperatingPlayer(Game game) {
        return game.getOperatingPlayer();
    }

    public SocketMessageDTO getMessage(List<SquareGETDTO> board) {
        SocketMessageDTO socketMessageDTO = new SocketMessageDTO();
        socketMessageDTO.setBoard(board);
//        socketMessageDTO.setCurrentPlayerId(game.getOperatingPlayer().getUserId());
        return socketMessageDTO;
    }
}
