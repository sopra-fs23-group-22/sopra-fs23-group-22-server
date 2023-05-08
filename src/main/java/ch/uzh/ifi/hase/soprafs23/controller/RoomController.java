package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.game.Lobby;
import ch.uzh.ifi.hase.soprafs23.game.Room;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoomGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class RoomController {
    @Autowired
    SimpMessagingTemplate template;
    private final UserService userService;

    public RoomController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/rooms")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public RoomGetDTO createRoom(@RequestBody User user) {
        Room createdRoom = Lobby.getInstance().createRoom();
        long userId = user.getId();
        createdRoom.addUser(userId);
        HashMap<Integer, Room> rooms = Lobby.getInstance().getRooms();
        List<RoomGetDTO> roomGetDTOs = new ArrayList<>();

        // ... send list of rooms back (with JSON format)
        for (HashMap.Entry<Integer, Room> room : rooms.entrySet()) {
            roomGetDTOs.add(DTOMapper.INSTANCE.convertEntityToRoomGetDTO(room.getValue()));
        }
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

        List<UserGetDTO> userGetDTOS = new ArrayList<UserGetDTO>();
        for(long id: room.getUserIds()){
            User user1 = userService.findUserById(id);
            userGetDTOS.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user1));
        }

        HashMap<Integer, Room> rooms = Lobby.getInstance().getRooms();
        List<RoomGetDTO> roomGetDTOs = new ArrayList<>();

        // ... send list of rooms back (with JSON format)
        for (HashMap.Entry<Integer, Room> room1 : rooms.entrySet()) {
            roomGetDTOs.add(DTOMapper.INSTANCE.convertEntityToRoomGetDTO(room1.getValue()));
        }

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
        List<UserGetDTO> userGetDTOS = new ArrayList<UserGetDTO>();
        for(long id: room.getUserIds()){
            User user1 = userService.findUserById(id);
            userGetDTOS.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user1));
        }

        HashMap<Integer, Room> rooms = Lobby.getInstance().getRooms();
        List<RoomGetDTO> roomGetDTOs = new ArrayList<>();

        // ... send list of rooms back (with JSON format)
        for (HashMap.Entry<Integer, Room> room1 : rooms.entrySet()) {
            roomGetDTOs.add(DTOMapper.INSTANCE.convertEntityToRoomGetDTO(room1.getValue()));
        }
        template.convertAndSend("/topic/rooms", roomGetDTOs);
        template.convertAndSend("/topic/room", userGetDTOS);
    }
    @GetMapping("/rooms/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public RoomGetDTO getRoom(@PathVariable int roomId) {
        Room room = Lobby.getInstance().getRoomByRoomId(roomId);
        RoomGetDTO roomGetDTO = DTOMapper.INSTANCE.convertEntityToRoomGetDTO(room);
        return roomGetDTO;
    }

    @GetMapping("/rooms/{roomId}/players")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getPlayers(@PathVariable int roomId) {
        Room room = Lobby.getInstance().getRoomByRoomId(roomId);
        List<UserGetDTO> userGetDTOS = new ArrayList<UserGetDTO>();
        for(long id: room.getUserIds()){
            User user = userService.findUserById(id);
            userGetDTOS.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }
        return userGetDTOS;
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
        template.convertAndSend("/topic/rooms", roomGetDTOs);
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
