package ch.uzh.ifi.hase.soprafs23.game;

import java.util.ArrayList;

import static ch.uzh.ifi.hase.soprafs23.game.states.GameState.WAITING;

public class Room {
    private Integer roomId;
    private ArrayList<Long> userIds = new ArrayList<Long>();
    private Game game;

    public Room(int roomId) {
        this.roomId = roomId;
    }

    public void addUser(long userId) {
        if (this.userIds.size() < 2) this.userIds.add(userId);
    }

    public void removeUser(long userId) {
        this.userIds.remove(userIds.indexOf(userId));
    }

    public void enterGame() {
        if (this.userIds.size() != 2) throw new IllegalStateException("Not enough players in the room!");
        if (this.game == null) {
            this.game = new Game();
        }
        if (this.game.getGameState() != WAITING) throw new IllegalStateException(
                "Game is not ready to start! Since your opponent is still in result page.");
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

    public Game getGame() {
        return game;
    }

}
