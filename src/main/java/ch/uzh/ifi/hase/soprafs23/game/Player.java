package ch.uzh.ifi.hase.soprafs23.game;

import ch.uzh.ifi.hase.soprafs23.game.army.Army;

public class Player {
    long userId;
    Army army;

    public Player(Long userId, Army army) {
        this.userId = userId;
        this.army = army;
    }

    public long getUserId() {
        return this.userId;
    }

    public Army getArmy() {
        return this.army;
    }
}
