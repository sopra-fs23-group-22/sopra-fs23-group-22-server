package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.game.Room;
import ch.uzh.ifi.hase.soprafs23.game.board.Axis;
import ch.uzh.ifi.hase.soprafs23.game.board.Board;
import ch.uzh.ifi.hase.soprafs23.game.board.Square;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import com.sun.istack.NotNull;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public abstract class DTOMapper {

    public static final DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    @Mapping(source = "password", target = "password")
    @Mapping(source = "username", target = "username")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "token", ignore = true)
    public abstract User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "wins", target = "wins")
    @Mapping(source = "loss", target = "loss")
    @Mapping(source = "roomId", target = "roomId")
    public abstract UserGetDTO convertEntityToUserGetDTO(User user);

    @Mapping(source = "pieceType", target = "pieceType")
    public abstract PieceGETDTO convertPieceToPieceGETDTO(Piece piece);

    public Piece convertPiecePUTDTOtoPiece(@NotNull PiecePUTDTO piecePUTDTO) {
        return new Piece(piecePUTDTO.getPieceType(), piecePUTDTO.getArmyType());
    }

    @Mapping(source = "axisX", target = "axisX")
    @Mapping(source = "axisY", target = "axisY")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "content", target = "content")
    public abstract SquareGETDTO convertSquareToSquareGETDTO(Square square);


    @Mapping(source = "roomId", target = "roomId")
    @Mapping(source = "userIds", target = "userIds")
    public abstract RoomGetDTO convertEntityToRoomGetDTO(Room room);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "id", target = "id")
//    @Mapping(target = "wins", ignore = true)
//    @Mapping(target = "loss", ignore = true)
//    @Mapping(target = "roomId", ignore = true)
    public abstract UserPutDTO convertEntityToUserPutDTO(User user);


    public List<SquareGETDTO> convertBoardToSquareGETDTOList(@NotNull Board board) {
        List<SquareGETDTO> squares = new ArrayList<SquareGETDTO>();
        for(int i = 0; i<10; i++) {
            for(int j=0; j<10; j++) {
                squares.add(convertSquareToSquareGETDTO(board.getSquare(i,j)));
            }
        }
        return squares;
    }



//    SquareGETDTO[][] convertBoardToSquareGETDTOList(@NotNull Board board) {
//        SquareGETDTO[][] squares = new SquareGETDTO[10][10];
//        for(int i = 0; i<10; i++) {
//            for(int j=0; j<10; j++) {
//                squares[i][j] = convertSquareToSquareGETDTO(board.getSquare(i, j));
//            }
//        }
//        return squares;
//    }

    public Piece[] convertConfigurationToInitialBoard(@NotNull PiecePUTDTO[] pieces) {
        Piece[] configuration = new Piece[40];
        for(int i=0; i<pieces.length; i++) {
            configuration[i] = convertPiecePUTDTOtoPiece(pieces[i]);
        }
        return configuration;
    }

    public Axis[][] convertMovingDTOtoCoordinates(@NotNull MovingDTO movingDTO) {
        Axis[][] coordinates = new Axis[2][1];
        // a better way maybe using a hashmap, but I don't exactly know much about hashmap...
        coordinates[0] = movingDTO.getSource();
        coordinates[1] = movingDTO.getTarget();
        System.out.println(coordinates[0][0]);
        System.out.println(coordinates[0][1]);
        System.out.println(coordinates[1][0]);
        System.out.println(coordinates[1][1]);
        return coordinates;
    }

}
