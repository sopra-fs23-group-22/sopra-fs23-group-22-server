package ch.uzh.ifi.hase.soprafs23.game.piece.attackstrategies;

import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;

public interface AttackStrategy {
    void attack(Piece attacker, Piece opponent);
}
