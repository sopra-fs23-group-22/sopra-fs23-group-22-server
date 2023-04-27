package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.game.Lobby;
import ch.uzh.ifi.hase.soprafs23.game.Room;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoomGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoomPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class RoomController {
    @PostMapping("/rooms")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public RoomGetDTO createRoom(@RequestBody User user) {
        Room createdRoom = Lobby.getInstance().createRoom();
        long userId = user.getId();
        createdRoom.addUser(userId);
        return DTOMapper.INSTANCE.convertEntityToRoomGetDTO(createdRoom);
    }
    @PutMapping("/rooms/add/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void addAUser(@RequestBody User user, @PathVariable int roomId) {
        Room room = Lobby.getInstance().getRoomByRoomId(roomId);
        long userId = user.getId();
        room.addUser(userId);
    }
    @PutMapping("/rooms/remove/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void removeUser(@RequestBody User user, @PathVariable int roomId) {
        Room room = Lobby.getInstance().getRoomByRoomId(roomId);
        long userId = user.getId();
        room.removeUser(userId);
    }
    @GetMapping("/rooms/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public RoomGetDTO getRoom(@PathVariable int roomId) {
        Room room = Lobby.getInstance().getRoomByRoomId(roomId);
        return DTOMapper.INSTANCE.convertEntityToRoomGetDTO(room);
    }
    @GetMapping("/rooms")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<RoomGetDTO> getAllRooms() {
        // fetch all users in the internal representation
        HashMap<Integer, Room> rooms = Lobby.getInstance().getRooms();
        List<RoomGetDTO> roomGetDTOs = new ArrayList<>();

        // ... send list of rooms back (with JSON format)
        for (HashMap.Entry<Integer, Room> room : rooms.entrySet()) {
            roomGetDTOs.add(DTOMapper.INSTANCE.convertEntityToRoomGetDTO(room.getValue()));
        }
        return roomGetDTOs;
    }
//    @PutMapping("/rooms/{roomId}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @ResponseBody
//    public void updateInfo(@PathVariable long roomId, @RequestBody RoomPostDTO roomPostDTO) {
//        Room roomInput = DTOMapper.INSTANCE.convertRoomPostDTOtoEntity(roomPostDTO);
//        if(roomInput.getCurrentGameId()!=0) {
//            Lobby.getInstance().getRoomByRoomId(roomId).setCurrentGameId(roomInput.getCurrentGameId());
//        }
//        if (roomInput.getUserIds()!=null) {
//            Lobby.getInstance().getRoomByRoomId(roomId).setUserIds(roomInput.getUserIds());
//        }
//    }
}
