package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class RoomPostDTO {

    private int roomId;
    private int currenGameId;

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int id) {
        this.roomId = id;
    }

    public int getCurrenGameId() {
        return currenGameId;
    }

    public void setCurrenGameId(int currenGameId) {
        this.currenGameId = currenGameId;
    }
}
