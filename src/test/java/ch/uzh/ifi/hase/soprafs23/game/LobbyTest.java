package ch.uzh.ifi.hase.soprafs23.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class LobbyTest {

    //
    //Usually this class would consist of multiple methods to test the Lobby functionality
    //Since the lobby class is a singleton, it doesn't really make sense to use multiple
    //functions as the tests would be dependent on each other anyway.
    //Therefore the tests are separated within the method by comments
    //

    @Test
    public void testCreateRoom_thenRemoveRoom_thenReuseOfRoomID() {
        //create Lobby
        Lobby lobby = Lobby.getInstance();

        //test if Room with ID 1 is created
        Room room = lobby.createRoom();
        //verify the correct IDs
        assertEquals(1, room.getRoomId());
        assertFalse(lobby.getRooms().isEmpty());
        //test if removing the room works
        lobby.removeRoom(1);

        //verify that roomMap is empty
        assert (lobby.getRooms().isEmpty());

        //test if roomIDs are reused
        Room reusedRoom = lobby.createRoom();

        assertEquals(1, reusedRoom.getRoomId());
        //test if next ID is used when there are no IDs to reuse
        Room room2 = lobby.createRoom();

        assertEquals(2, room2.getRoomId());

    }

}