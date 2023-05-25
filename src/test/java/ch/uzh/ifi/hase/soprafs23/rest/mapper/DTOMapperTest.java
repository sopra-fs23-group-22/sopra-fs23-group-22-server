package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.game.Room;
import ch.uzh.ifi.hase.soprafs23.game.army.ArmyType;
import ch.uzh.ifi.hase.soprafs23.game.board.Axis;
import ch.uzh.ifi.hase.soprafs23.game.board.Square;
import ch.uzh.ifi.hase.soprafs23.game.board.SquareType;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;
import ch.uzh.ifi.hase.soprafs23.game.piece.PieceType;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */
public class DTOMapperTest {
    @Test
    public void testCreateUser_fromUserPostDTO_toUser_success() {
        // create UserPostDTO
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("name");
        userPostDTO.setUsername("username");

        // MAP -> Create user
        User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // check content
        assertEquals(userPostDTO.getPassword(), user.getPassword());
        assertEquals(userPostDTO.getUsername(), user.getUsername());
    }

    @Test
    public void testGetUser_fromUser_toUserGetDTO_success() {
        // create User
        User user = new User();
        user.setPassword("Firstname Lastname");
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setToken("1");

        // MAP -> Create UserGetDTO
        UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

        // check content
        assertEquals(user.getPassword(), userGetDTO.getPassword());
        assertEquals(user.getUsername(), userGetDTO.getUsername());
        assertEquals(user.getStatus(), userGetDTO.getStatus());
    }

    @Test
    public void testconvertPieceToPieceGetDTO() {
        //create piece
        Piece piece = new Piece(PieceType.BOMB, ArmyType.BLUE);

        PieceGETDTO pieceGetDTO = DTOMapper.INSTANCE.convertPieceToPieceGETDTO(piece);

        assertEquals(pieceGetDTO.getPieceType(), piece.getPieceType());
    }

    @Test
    public void testconvertPiecePUTDTOtoPiece() {
        //create pieceputdto
        PiecePUTDTO piecePUTDTO = new PiecePUTDTO();
        piecePUTDTO.setPieceType(PieceType.BOMB);
        piecePUTDTO.setArmyType(ArmyType.RED);

        Piece piece = DTOMapper.INSTANCE.convertPiecePUTDTOtoPiece(piecePUTDTO);

        assertEquals(piecePUTDTO.getPieceType(), piece.getPieceType());
        assertEquals(piecePUTDTO.getArmyType(), piece.getArmyType());
    }

    @Test
    public void testConvertSquareToSquareGETDTO() {
        //create Square
        Axis axisX = Axis._0;
        Axis axisY = Axis._6;
        Square square = new Square(axisX, axisY);

        //create piece to put on square
        Piece piece = new Piece(PieceType.GENERAL, ArmyType.RED);

        square.setContent(piece);

        SquareGETDTO squareGETDTO = DTOMapper.INSTANCE.convertSquareToSquareGETDTO(square);

        assertEquals(squareGETDTO.getAxisX(), axisX);
        assertEquals(squareGETDTO.getAxisY(), axisY);
        assertEquals(squareGETDTO.getContent().getPieceType(), piece.getPieceType());
        assertEquals(squareGETDTO.getContent().getArmyType(), piece.getArmyType());
        assertEquals(squareGETDTO.getType(), SquareType.BATTLE_FIELD);
    }

    @Test
    public void testConvertEntityToRoomGetDTO() {
        //create room
        Room room = new Room(12);
        room.addUser(5L);
        room.addUser(10L);


        RoomGetDTO roomGetDTO = DTOMapper.INSTANCE.convertEntityToRoomGetDTO(room);

        assertEquals(room.getRoomId(), roomGetDTO.getRoomId());
        assertEquals(room.getUserIds(), roomGetDTO.getUserIds());
    }

    @Test
    public void testConvertMovingDTOtoCoordinates() {
        //create movingDTO
        MovingDTO movingDTO = new MovingDTO();
        Axis[] source = {Axis._2, Axis._4};
        Axis[] target = {Axis._1, Axis._4};
        movingDTO.setSource(source);
        movingDTO.setTarget(target);
        Axis[][] expected = {source, target};
        Axis[][] coordinates = DTOMapper.INSTANCE.convertMovingDTOtoCoordinates(movingDTO);

        assertEquals(expected[0][0], coordinates[0][0]);
        assertEquals(expected[1][0], coordinates[1][0]);
        assertEquals(expected[0][1], coordinates[0][1]);
        assertEquals(expected[1][1], coordinates[1][1]);
    }
}
