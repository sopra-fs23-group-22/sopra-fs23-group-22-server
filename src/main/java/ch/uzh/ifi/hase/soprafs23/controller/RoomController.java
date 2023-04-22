package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.game.Lobby;
import ch.uzh.ifi.hase.soprafs23.game.Room;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoomGetDTO;
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
    public RoomGetDTO createRoom() {
        // convert API user to internal representation
        // create user
        Room createdRoom = Lobby.getInstance().createRoom();
        createdRoom.addUser(1);
        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToRoomGetDTO(createdRoom);
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
}
