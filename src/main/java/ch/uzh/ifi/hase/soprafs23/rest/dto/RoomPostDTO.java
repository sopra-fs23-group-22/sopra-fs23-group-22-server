package ch.uzh.ifi.hase.soprafs23.rest.dto;

import java.util.ArrayList;

public class RoomPostDTO {

    private int roomId;
    private int currentGameId;

    public long getRoomId() {
        return roomId;
    }
    private ArrayList<Long> userIds = new ArrayList<Long>();

    public ArrayList<Long> getUserIds() {
        return userIds;
    }

    public void setRoomId(int id) {
        this.roomId = id;
    }

    public int getCurrentGameId() {
        return currentGameId;
    }

    public void setCurrentGameId(int currentGameId) {
        this.currentGameId = currentGameId;
    }
}
