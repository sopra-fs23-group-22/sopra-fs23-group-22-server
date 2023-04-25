package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.game.Game;
import ch.uzh.ifi.hase.soprafs23.game.Lobby;
import ch.uzh.ifi.hase.soprafs23.game.Room;
import ch.uzh.ifi.hase.soprafs23.game.army.ArmyType;
import ch.uzh.ifi.hase.soprafs23.game.board.Axis;
import ch.uzh.ifi.hase.soprafs23.game.board.Board;
import ch.uzh.ifi.hase.soprafs23.game.board.Square;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;
import ch.uzh.ifi.hase.soprafs23.game.piece.PieceType;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
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

//    private Game game;

    @Autowired
    public GameService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public Board createBoard(){
        Board board = new Board();
        Piece redpiece = new Piece(PieceType.BOMB, ArmyType.RED);
        Piece bludpiece = new Piece(PieceType.SCOUT, ArmyType.BLUE);
        for(int i=0; i<4; i++) {
            for(int j=0; j<10; j++){
                board.place(bludpiece, board.getSquare(i,j));
            }
        }
        for(int i=6; i<10; i++) {
            for(int j=0; j<10; j++){
                board.place(redpiece, board.getSquare(i,j));
            }
        }
        return board;
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


}