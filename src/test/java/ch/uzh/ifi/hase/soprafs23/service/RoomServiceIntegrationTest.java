package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.game.Lobby;
import ch.uzh.ifi.hase.soprafs23.game.LobbyTest;
import ch.uzh.ifi.hase.soprafs23.game.Room;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoomGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
public class RoomServiceIntegrationTest {

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;
    User testUser;
    ArrayList<Long> testPlayerIds;

    @BeforeEach
    public void setup() {
        testUser = createUserForTest(1L);

        testPlayerIds = new ArrayList<Long>();
        testPlayerIds.add(1L);

        clearLobby();
    }

    @AfterEach
    void tearDown() {
        // make sure the rooms created in this test class won't mess up other test classes
        // (since lobby is a singleton)
        clearLobby();
    }

    @Test
    public void userCreateARoom_thenLeave() {
        // check if there's no room in lobby yet
        assertTrue(Lobby.getInstance().getRooms().isEmpty());

        // one room created by test user1 -> one user in the room
        RoomGetDTO createdRoomGetDTO = roomService.createRoom(testUser.getId());
        assertEquals(1, createdRoomGetDTO.getRoomId());
        assertEquals(testPlayerIds, createdRoomGetDTO.getUserIds());

        Room createdRoom = roomService.findRoomById(createdRoomGetDTO.getRoomId());
        assertEquals(createdRoomGetDTO.getRoomId(), createdRoom.getRoomId());
        assertEquals(createdRoomGetDTO.getUserIds(), createdRoom.getUserIds());

        // check if there's only one room in lobby
        List<RoomGetDTO> roomsInLobby = roomService.getAllRooms();
        assertEquals(1, roomsInLobby.size());

        // check if the users are in
        List<UserGetDTO> usersInRoom = roomService.getUserInRoom(createdRoom.getRoomId());
        assertEquals(1, usersInRoom.size());
        assertEquals(testUser.getId(), usersInRoom.get(0).getId());
        assertEquals(testUser.getUsername(), usersInRoom.get(0).getUsername());
        assertEquals(testUser.getStatus(), usersInRoom.get(0).getStatus());

        // user1 leaves the room -> no user left in the room
        roomService.removeAUserFromRoom(createdRoomGetDTO.getRoomId(), testUser.getId());
        assertTrue(createdRoom.getUserIds().isEmpty());
    }

    @Test
    public void userEnterARoom_thenLeave() {
        assertTrue(Lobby.getInstance().getRooms().isEmpty());

        // one room created by test user1
        RoomGetDTO createdRoomGetDTO = roomService.createRoom(testUser.getId());
        Room createdRoom = roomService.findRoomById(createdRoomGetDTO.getRoomId());
        List<UserGetDTO> usersInRoom = roomService.getUserInRoom(createdRoomGetDTO.getRoomId());

        System.out.println(usersInRoom.size());
        assertEquals(1, createdRoom.getRoomId());
        assertEquals(testPlayerIds, createdRoom.getUserIds());
        // check if there's only one user in this room
        assertEquals(1, createdRoom.getUserIds().size());
        assertEquals(testUser.getId(), usersInRoom.get(0).getId());

        // add test user2 to this room
        User testUser2 = createUserForTest(2L);
        testPlayerIds.add(2L);
        roomService.addAUserToRoom(createdRoomGetDTO.getRoomId(), testUser2.getId());
        // check if there are two users in this room, the second user is the added one
        assertEquals(2, createdRoom.getUserIds().size());
        assertEquals(testPlayerIds, createdRoom.getUserIds());

        roomService.removeAUserFromRoom(createdRoomGetDTO.getRoomId(), testUser2.getId());
        assertEquals(1, createdRoom.getUserIds().size());
    }

    @Test
    public void createARoom_then_removeARoomFromLobby() {
        assertTrue(Lobby.getInstance().getRooms().isEmpty());

        assertTrue(roomService.getAllRooms().isEmpty());
        RoomGetDTO createdRoomGetDTO = roomService.createRoom(testUser.getId());
        assertEquals(1, roomService.getAllRooms().size());

        roomService.removeRoomFromLobby(createdRoomGetDTO.getRoomId());
        assertTrue(roomService.getAllRooms().isEmpty());
    }


    private User createUserForTest(long userId) {
        User newUser = new User();
        newUser.setId(userId);
        newUser.setPassword("testPassword");
        newUser.setStatus(UserStatus.ONLINE);
        newUser.setUsername("testUser"+userId);
        newUser.setToken(UUID.randomUUID().toString());
        userRepository.saveAndFlush(newUser);
        return newUser;
    }

    private void clearLobby() {
        HashMap<Integer, Room> allRooms = Lobby.getInstance().getRooms();
        if (!allRooms.isEmpty()) {
            Iterator<Integer> iterator = allRooms.keySet().iterator();
            while (iterator.hasNext()) {
                int roomId = iterator.next();
                iterator.remove();
                Lobby.getInstance().removeRoom(roomId);
            }
        }
    }
}
