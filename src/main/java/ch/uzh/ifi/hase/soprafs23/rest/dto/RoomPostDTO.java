package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;

public class RoomPostDTO {

    private Long roomId;
    private Long currenGameId;

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long id) {
        this.roomId = id;
    }

    public Long getCurrenGameId() {
        return currenGameId;
    }

    public void setCurrenGameId(Long currenGameId) {
        this.currenGameId = currenGameId;
    }
}
