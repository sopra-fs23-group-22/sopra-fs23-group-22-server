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
        RoomGetDTO createdRoom = roomService.createRoom(user.getId());

        User userInRoom = userService.findUserById(user.getId());

        List<RoomGetDTO> roomGetDTOs = roomService.getAllRooms();
        template.convertAndSend("/topic/rooms", roomGetDTOs);
        template.convertAndSend("/topic/users/"+user.getId(), DTOMapper.INSTANCE.convertEntityToUserGetDTO(userInRoom));
        return createdRoom;
    }

    @PutMapping("/rooms/{roomId}/add")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void addAUser(@RequestBody User user, @PathVariable int roomId) {
        roomService.addAUserToRoom(roomId, user.getId());
        List<UserGetDTO> userGetDTOS = roomService.getUserInRoom(roomId);
        List<RoomGetDTO> roomGetDTOs = roomService.getAllRooms();

        User userInRoom = userService.findUserById(user.getId());

        template.convertAndSend("/topic/rooms", roomGetDTOs); // send list of rooms to all clients
        template.convertAndSend("/topic/room/"+roomId, userGetDTOS); // send list of users in room
        template.convertAndSend("/topic/users/"+user.getId(), DTOMapper.INSTANCE.convertEntityToUserGetDTO(userInRoom));
    }

    @PutMapping("/rooms/{roomId}/remove")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void removeUser(@RequestBody User user, @PathVariable int roomId) {

        roomService.removeAUserFromRoom(roomId, user.getId());

        User userInRoom = userService.findUserById(user.getId());
        List<UserGetDTO> usersInRoom = roomService.getUserInRoom(roomId);
        if(usersInRoom.isEmpty()) {
            roomService.removeRoomFromLobby(roomId);
        }
        else {
            template.convertAndSend("/topic/room/"+roomId, usersInRoom);
        }
        List<RoomGetDTO> roomGetDTOs = roomService.getAllRooms();
        template.convertAndSend("/topic/rooms", roomGetDTOs);
        template.convertAndSend("/topic/users/"+user.getId(), DTOMapper.INSTANCE.convertEntityToUserGetDTO(userInRoom));
    }
    @GetMapping("/rooms/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public RoomGetDTO getRoom(@PathVariable int roomId) {
        Room room = roomService.findRoomById(roomId);
        return DTOMapper.INSTANCE.convertEntityToRoomGetDTO(room);
    }

    @GetMapping("/rooms/{roomId}/players")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getPlayers(@PathVariable int roomId) {
        return roomService.getUserInRoom(roomId);

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
