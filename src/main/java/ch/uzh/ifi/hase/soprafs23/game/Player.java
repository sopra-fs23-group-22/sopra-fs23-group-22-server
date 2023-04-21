package ch.uzh.ifi.hase.soprafs23.game;

import ch.uzh.ifi.hase.soprafs23.game.army.Army;

public class Player {
    int userId;
    Army army;

    public Player(int userId, Army army) {
        this.userId = userId;
        this.army = army;
    }

    public int getUserId() { return this.userId; }
    public Army getArmy() { return this.army; }
}
