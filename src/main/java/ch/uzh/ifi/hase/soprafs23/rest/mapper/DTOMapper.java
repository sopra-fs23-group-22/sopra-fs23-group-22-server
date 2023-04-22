package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.game.Room;
import ch.uzh.ifi.hase.soprafs23.game.board.Board;
import ch.uzh.ifi.hase.soprafs23.game.board.Square;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import com.sun.istack.NotNull;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;

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
    public abstract UserGetDTO convertEntityToUserGetDTO(User user);

    @Mapping(source = "pieceType", target = "pieceType")
    public abstract PieceGETDTO convertPieceToPieceGETDTO(Piece piece);

    @Mapping(source = "axisX", target = "axisX")
    @Mapping(source = "axisY", target = "axisY")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "content", target = "content")
    public abstract SquareGETDTO convertSquareToSquareGETDTO(Square square);

    @Mapping(source = "roomId", target = "roomId")
    @Mapping(source = "currentGameId", target = "currentGameId")
    @Mapping(source = "userIds", target = "userIds")
    public abstract RoomGetDTO convertEntityToRoomGetDTO(Room room);

    SquareGETDTO[][] convertBoardToSquareGETDTOList(@NotNull Board board) {
        SquareGETDTO[][] squares = new SquareGETDTO[10][10];
        for(int i = 0; i<10; i++) {
            for(int j=0; j<10; j++) {
                squares[i][j] = convertSquareToSquareGETDTO(board.getSquare(i, j));
            }
        }
        return squares;
    }


}
