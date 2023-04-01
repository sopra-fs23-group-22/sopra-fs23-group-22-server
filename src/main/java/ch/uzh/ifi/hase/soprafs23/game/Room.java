package ch.uzh.ifi.hase.soprafs23.game;

import java.util.ArrayList;

public class Room {
    private int roomId;
    private ArrayList<Integer> users = new ArrayList<Integer>();
    private Game game;

    public Room(int roomId) { this.roomId = roomId; }

    public void addUser(int userId){ this.users.add(userId); }
    public void removeUser(int userId){ this.users.remove(users.indexOf(userId)); }

    public void startGame(){
        if (this.game == null) this.game = new Game();
        game.setup(users);
        game.start();
    }

    public ArrayList<Integer> getUsersInRoom(){ return this.users; }
}
