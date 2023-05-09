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
import org.springframework.stereotype.Service;

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
    public RoomGetDTO findRoomById(int roomId){
        Room room = Lobby.getInstance().getRoomByRoomId(roomId);
        RoomGetDTO roomGetDTO = DTOMapper.INSTANCE.convertEntityToRoomGetDTO(room);
        return roomGetDTO;
    }
    public List<RoomGetDTO> getAllRooms(){

        HashMap<Integer, Room> rooms = Lobby.getInstance().getRooms();
        List<RoomGetDTO> roomGetDTOs = new ArrayList<>();

        for (HashMap.Entry<Integer, Room> room : rooms.entrySet()) {
            roomGetDTOs.add(DTOMapper.INSTANCE.convertEntityToRoomGetDTO(room.getValue()));
        }
        return roomGetDTOs;
    }
    public List<UserGetDTO> getUserInRoom(Room room){
        List<UserGetDTO> userGetDTOS = new ArrayList<UserGetDTO>();
        for(long id: room.getUserIds()){
            User user1 = userService.findUserById(id);
            userGetDTOS.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user1));
        }
        return userGetDTOS;
    }
}