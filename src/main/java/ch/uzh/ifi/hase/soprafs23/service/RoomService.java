package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.game.Lobby;
import ch.uzh.ifi.hase.soprafs23.game.Room;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoomGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class RoomService {
    private final Logger log = LoggerFactory.getLogger(RoomService.class);
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public RoomService(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }
    public Room findRoomById(int roomId){
        String baseErrorMessage = "Room %s is not found!";
        Room room = Lobby.getInstance().getRoomByRoomId(roomId);
        if(room!=null) {
            return room;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage, roomId));
        }
    }
    public List<RoomGetDTO> getAllRooms(){

        HashMap<Integer, Room> rooms = Lobby.getInstance().getRooms();
        List<RoomGetDTO> roomGetDTOs = new ArrayList<>();

        for (HashMap.Entry<Integer, Room> room : rooms.entrySet()) {
            roomGetDTOs.add(DTOMapper.INSTANCE.convertEntityToRoomGetDTO(room.getValue()));
        }
        return roomGetDTOs;
    }
    public List<UserGetDTO> getUserInRoom(int roomId){
        Room room = findRoomById(roomId);
        List<UserGetDTO> userGetDTOS = new ArrayList<UserGetDTO>();
        for(long id: room.getUserIds()){
            User user1 = userService.findUserById(id);
            userGetDTOS.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user1));
        }
        return userGetDTOS;
    }

    public RoomGetDTO createRoom(long userId) {
        Room createdRoom = Lobby.getInstance().createRoom();
        createdRoom.addUser(userId);
        userService.updateRoomId(createdRoom.getRoomId(), userId);
        return DTOMapper.INSTANCE.convertEntityToRoomGetDTO(createdRoom);
    }

    public void addAUserToRoom(int roomId, long userId) {
        Room room = findRoomById(roomId);
        room.addUser(userId);
        userService.updateRoomId(room.getRoomId(),userId);
    }

    public void removeAUserFromRoom(int roomId, long userId) {
        Room room = findRoomById(roomId);
        room.removeUser(userId);
        userService.updateRoomId(null, userId);
    }

    public void removeRoomFromLobby(int roomId) {
        Lobby.getInstance().removeRoom(roomId);
    }

}

