package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.game.Lobby;
import ch.uzh.ifi.hase.soprafs23.game.Room;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


class RoomServiceTest {

    @InjectMocks
    private RoomService roomService;
    @Mock
    private UserService userService;
    private Room testRoom;
    private final long TEST_USER_ID = 1L;
    private final int TEST_ROOM_ID = 1;
    private ArrayList<Long> userIds;
    private Lobby lobby = Lobby.getInstance();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userIds = new ArrayList<Long>();
        userIds.add(TEST_USER_ID);

        // we can't really mock lobby since it's singleton,
        // so here we just use the room created in lobby as our test room
        testRoom = lobby.createRoom();
        testRoom.setRoomId(TEST_ROOM_ID);
        testRoom.setUserIds(userIds);
    }

    @AfterEach
    void tearDown() {
        lobby.removeRoom(1);
    }


    @Test
    void test_getUserInRoom() {
        User testUser = new User();
        testUser.setId(TEST_USER_ID);
        testUser.setStatus(UserStatus.ONLINE);

        given(userService.findUserById(TEST_USER_ID)).willReturn(testUser);

        List<UserGetDTO> actual = roomService.getUserInRoom(TEST_ROOM_ID);

        assertEquals(TEST_USER_ID, actual.get(0).getId());
        assertEquals(UserStatus.ONLINE, actual.get(0).getStatus());

        verify(userService, times(1)).findUserById(TEST_USER_ID);
    }

    @Test
    void roomNotFound_throwNOTFOUND() {
        assertThrows(ResponseStatusException.class, () -> roomService.findRoomById(42));
    }

    @Test
    void addAUserToRoom() {
        doNothing().when(userService).updateRoomId(TEST_ROOM_ID, TEST_USER_ID);
        roomService.addAUserToRoom(TEST_ROOM_ID, 2L);

        ArrayList<Long> playersInRoom = new ArrayList<>();
        playersInRoom.add(TEST_USER_ID);
        playersInRoom.add(2L);

        ArrayList<Long> actual = testRoom.getUserIds();
        assertEquals(playersInRoom, actual);
    }

    @Test
    void removeAUserFromRoom() {
        doNothing().when(userService).updateRoomId(TEST_ROOM_ID, TEST_USER_ID);
        roomService.removeAUserFromRoom(TEST_ROOM_ID, 1L);

        ArrayList<Long> playersInRoom = new ArrayList<>();

        ArrayList<Long> actual = testRoom.getUserIds();
        assertEquals(playersInRoom, actual);
    }

}