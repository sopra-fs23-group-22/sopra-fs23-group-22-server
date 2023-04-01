package ch.uzh.ifi.hase.soprafs23.game.piece;

import ch.uzh.ifi.hase.soprafs23.game.piece.attackstrategies.ComparisonResult;

public enum Rank {
    _1,
    _2,
    _3,
    _4,
    _5,
    _6,
    _7,
    _8,
    _9,
    _10,
    B,
    F;

    public ComparisonResult compareToRank(Rank another){
        if (this == B || another == F) throw new IllegalArgumentException("Bombs and Flags cannot be compared.");
        if (this.ordinal() > another.ordinal()){
            return ComparisonResult.STRONGER;
        }
        else if (this.ordinal() < another.ordinal()){
            return ComparisonResult.WEAKER;
        }
        else {
            return ComparisonResult.SAME;
        }
    }
}
