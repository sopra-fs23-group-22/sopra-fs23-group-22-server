package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.game.states.GameState;

import java.util.ArrayList;

public class RoomPostDTO {

    private int roomId;
    private int currentGameId;
    private GameState gameState;

    public int getRoomId() {
        return roomId;
    }

    private ArrayList<Long> userIds = new ArrayList<Long>();

    public ArrayList<Long> getUserIds() {
        return userIds;
    }

    public void setRoomId(int id) {
        this.roomId = id;
    }

}
