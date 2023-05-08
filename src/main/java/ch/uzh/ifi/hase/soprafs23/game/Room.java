package ch.uzh.ifi.hase.soprafs23.game;
import java.util.ArrayList;

public class Room {
    private int roomId;
    private ArrayList<Long> userIds = new ArrayList<Long>();
    private Game game;
    public Room(int roomId) { this.roomId = roomId; }

    public void addUser(long userId){ if (this.userIds.size() < 2) this.userIds.add(userId); }
    public void removeUser(long userId){ this.userIds.remove(userIds.indexOf(userId)); }

    public void enterGame(){
        if (this.userIds.size() != 2) throw new IllegalStateException("Not enough players in the room!");
        if (this.game == null) {
            this.game = new Game();
        }
        game.setup(userIds);
    }


    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public void setUserIds(ArrayList<Long> userIds) {
        this.userIds = userIds;
    }


    public ArrayList<Long> getUserIds() {
        return userIds;
    }

    public Game getGame() { return game; }
}
