package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.game.Lobby;
import ch.uzh.ifi.hase.soprafs23.game.Room;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoomGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.RoomService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class RoomController {

    @Autowired
    SimpMessagingTemplate template;
    private final UserService userService;
    private final RoomService roomService;

    public RoomController(UserService userService, RoomService roomService) {
        this.userService = userService;
        this.roomService = roomService;
    }

    @PostMapping("/rooms")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public RoomGetDTO createRoom(@RequestBody User user) {

        Room createdRoom = Lobby.getInstance().createRoom();
        long userId = user.getId();
        createdRoom.addUser(userId);
        userService.updateRoomId(createdRoom.getRoomId(),userId);
        List<RoomGetDTO> roomGetDTOs = roomService.getAllRooms();
        template.convertAndSend("/topic/rooms", roomGetDTOs);
        return DTOMapper.INSTANCE.convertEntityToRoomGetDTO(createdRoom);
    }
    @PutMapping("/rooms/{roomId}/add")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void addAUser(@RequestBody User user, @PathVariable int roomId) {

        Room room = Lobby.getInstance().getRoomByRoomId(roomId);
        long userId = user.getId();
        room.addUser(userId);
        userService.updateRoomId(room.getRoomId(),userId);
        List<UserGetDTO> userGetDTOS = roomService.getUserInRoom(room);
        List<RoomGetDTO> roomGetDTOs = roomService.getAllRooms();

        template.convertAndSend("/topic/rooms", roomGetDTOs); // send list of rooms to all clients
        template.convertAndSend("/topic/room", userGetDTOS); // send list of users in room
    }
    @PutMapping("/rooms/{roomId}/remove")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void removeUser(@RequestBody User user, @PathVariable int roomId) {

        Room room = Lobby.getInstance().getRoomByRoomId(roomId);
        long userId = user.getId();
        room.removeUser(userId);
        userService.updateRoomId(null,userId);
        // ... if room is empty, delete room
        if (room.getUserIds().isEmpty()) {
            Lobby.getInstance().removeRoom(roomId);
        }
        else {
            List<UserGetDTO> userGetDTOS = roomService.getUserInRoom(room);
            template.convertAndSend("/topic/room", userGetDTOS);
        }
        List<RoomGetDTO> roomGetDTOs = roomService.getAllRooms();
        template.convertAndSend("/topic/rooms", roomGetDTOs);
    }
    @GetMapping("/rooms/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public RoomGetDTO getRoom(@PathVariable int roomId) {

        RoomGetDTO roomGetDTO = roomService.findRoomById(roomId);
        return roomGetDTO;
    }

    @GetMapping("/rooms/{roomId}/players")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getPlayers(@PathVariable int roomId) {

        Room room = Lobby.getInstance().getRoomByRoomId(roomId);
        List<UserGetDTO> userGetDTOS = roomService.getUserInRoom(room);
        return userGetDTOS;

    }
    @GetMapping("/rooms")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<RoomGetDTO> getAllRooms() {

        List<RoomGetDTO> roomGetDTOs = roomService.getAllRooms();
        template.convertAndSend("/topic/rooms", roomGetDTOs);
        return roomGetDTOs;
    }

}
