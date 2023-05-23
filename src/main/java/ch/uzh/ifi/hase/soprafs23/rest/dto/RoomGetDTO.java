package ch.uzh.ifi.hase.soprafs23.rest.dto;

import java.util.ArrayList;

public class RoomGetDTO {

    private int roomId;
    private ArrayList<Long> userIds = new ArrayList<>();

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int id) {
        this.roomId = id;
    }

    public ArrayList<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(ArrayList<Long> userIds) {
        this.userIds = userIds;
    }


}