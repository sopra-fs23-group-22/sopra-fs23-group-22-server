package ch.uzh.ifi.hase.soprafs23.rest.dto;

import java.util.ArrayList;

public class RoomPostDTO {

    private int roomId;

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

}
