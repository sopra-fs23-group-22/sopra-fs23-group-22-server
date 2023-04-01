package ch.uzh.ifi.hase.soprafs23.game.piece;

import static ch.uzh.ifi.hase.soprafs23.game.piece.Rank.*;

public enum PieceType {
    BOMB(B, "Bomb"),
    MARSHAL(_10, "Marshal"),
    GENERAL(_9, "General"),
    COLONEL(_8, "Colonel"),
    MAJOR(_7, "Major"),
    CAPTAIN(_6, "Captain"),
    LIEUTENANT(_5, "Lieutenant"),
    SERGEANT(_4, "Sergeant"),
    MINER(_3, "Miner"),
    SCOUT(_2, "Scout"),
    SPY(_1, "Spy"),
    FLAG(F, "Flag");

    private final Rank rank;
    private final String name;

    PieceType(Rank rank, String name){
        this.rank = rank;
        this.name = name;
    }

    public Rank getRank(){
        return this.rank;
    }
    public String getName(){
        return this.name;
    }
}
