package ch.uzh.ifi.hase.soprafs23.rest.dto;

import java.util.ArrayList;

public class RoomGetDTO {

    private int roomId;
    private int currentGameId;
    private ArrayList<Integer> userIds = new ArrayList<>();
//    private ArrayList<UserGetDTO> users = new ArrayList<>();

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int id) {
        this.roomId = id;
    }

    public int getCurrentGameId() {
        return currentGameId;
    }

    public void setCurrentGameId(int currenGameId) {
        this.currentGameId = currenGameId;
    }

    public ArrayList<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(ArrayList<Integer> userIds) {
//        for(Integer id: userIds) {
//            this.userIds.add(id);
//        }
        this.userIds = userIds;
    }

//    public void setUsers(ArrayList<UserGetDTO> users) {
//        for(UserGetDTO user: users) {
//            this.users.add(user);
//        }
//    }

}