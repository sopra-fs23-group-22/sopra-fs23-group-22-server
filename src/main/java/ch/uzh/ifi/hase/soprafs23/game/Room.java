package ch.uzh.ifi.hase.soprafs23.game;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;

public class Room {
    private int roomId;
    private ArrayList<Integer> userIds = new ArrayList<Integer>();
    private Game game;
    private int currenGameId;
    public Room(int roomId) { this.roomId = roomId; }

    public void addUser(int userId){ if (this.userIds.size() < 2) this.userIds.add(userId); }
    public void removeUser(int userId){ this.userIds.remove(userIds.indexOf(userId)); }

    public void enterGame(){
        if (this.userIds.size() != 2) throw new IllegalStateException("Not enough players in the room!");
        if (this.game == null) {
            this.game = new Game();
            currenGameId = game.getGameId();
        }
        game.setup(userIds);
    }

    public ArrayList<Integer> getUsersInRoom(){ return this.userIds; }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getCurrenGameId() {
        return currenGameId;
    }

    public void setCurrenGameId(int currenGameId) {
        this.currenGameId = currenGameId;
    }

//    public void setUsers(ArrayList<Integer> users) {
//        this.userIds = users;
//    }
}
