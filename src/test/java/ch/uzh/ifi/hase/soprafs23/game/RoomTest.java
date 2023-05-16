package ch.uzh.ifi.hase.soprafs23.game;

import ch.uzh.ifi.hase.soprafs23.game.states.GameState;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class RoomTest {


    @Test
    public void testEnterGame(){
        //create room
        int id = 4;
        Room room = new Room(id);
        ArrayList<Long> userIds = new ArrayList<>();
        userIds.add(1L);
        userIds.add(5L);
        room.setUserIds(userIds);


        room.enterGame();

        assertEquals(room.getGame().getGameState(), GameState.PRE_PLAY);
    }

    @Test
    public void testEnterGameException(){
        int id = 3;
        Room room = new Room(id);
        ArrayList<Long> userIds = new ArrayList<>();
        userIds.add(2L);
        room.setUserIds(userIds);

        assertThrows(IllegalStateException.class , room::enterGame);
    }

}
