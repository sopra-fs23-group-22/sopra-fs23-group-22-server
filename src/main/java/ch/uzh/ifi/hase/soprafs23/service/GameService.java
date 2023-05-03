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

    private Game game;

    @Autowired
    public GameService(UserRepository userRepository) {
        this.userRepository = userRepository;
        setUpGame();
    }

    void setUpGame() {
        game = new Game();
        ArrayList<Long> players = new ArrayList<Long>();
        players.add(1L);
        players.add(2L);
        game.setup(players);

        Piece[] red = new Piece[40];
        Piece[] blue = new Piece[40];
        List<PieceType> reddy = new ArrayList<>(List.of(
                PieceType.MARSHAL,
                PieceType.GENERAL,
                PieceType.COLONEL, PieceType.COLONEL,
                PieceType.MAJOR, PieceType.MAJOR, PieceType.MAJOR,
                PieceType.CAPTAIN, PieceType.CAPTAIN, PieceType.CAPTAIN, PieceType.CAPTAIN,
                PieceType.LIEUTENANT, PieceType.LIEUTENANT, PieceType.LIEUTENANT, PieceType.LIEUTENANT,
                PieceType.SERGEANT, PieceType.SERGEANT, PieceType.SERGEANT, PieceType.SERGEANT,
                PieceType.MINER, PieceType.MINER, PieceType.MINER, PieceType.MINER, PieceType.MINER,
                PieceType.SCOUT, PieceType.SCOUT, PieceType.SCOUT, PieceType.SCOUT, PieceType.SCOUT, PieceType.SCOUT, PieceType.SCOUT, PieceType.SCOUT,
                PieceType.SPY,
                PieceType.BOMB, PieceType.BOMB, PieceType.BOMB, PieceType.BOMB, PieceType.BOMB, PieceType.BOMB,
                PieceType.FLAG
                ));

        for(int i=0; i<40; i++) {
            red[i] = new Piece(reddy.get(i), ArmyType.RED);
            blue[i] = new Piece(reddy.get(39-i), ArmyType.BLUE);
        }
        game.placePieces(red);
        game.placePieces(blue);
        game.start();
    }


    public void setInitialBoard(Game game, Piece[] configuration) {
        game.placePieces(configuration);
    }

    public Game findGameByRoomId(int roomId) {
        Room room = Lobby.getInstance().getRoomByRoomId(roomId);
        room.addUser(1L);
        room.addUser(2L);
        room.enterGame();
        return room.getGame();
    }

    public void createRoom(){
        Lobby.getInstance().createRoom();
    }

    //this is a test method
    public List<SquareGETDTO> operatePiece(Axis[][] coordinates) {

        game.operate(coordinates[0], coordinates[1]);
        Board board = game.getBoard();
        List<SquareGETDTO> squares = new ArrayList<SquareGETDTO>();
        for(int i = 0; i<10; i++) {
            for(int j=0; j<10; j++) {
//                squares[i][j] = convertSquareToSquareGETDTO(board.getSquare(i, j));
                squares.add(DTOMapper.INSTANCE.convertSquareToSquareGETDTO(board.getSquare(i,j)));
            }
        }
        return squares;
    }

    public Board getBoard() {
        return game.getBoard();
    }

    public Player getOperatingPlayer() {
        return game.getOperatingPlayer();
    }

    public SocketMessageDTO getMessage(List<SquareGETDTO> board) {
        SocketMessageDTO socketMessageDTO = new SocketMessageDTO();
        socketMessageDTO.setBoard(board);
        socketMessageDTO.setPlayer("anqi");
        return socketMessageDTO;
    }
}
