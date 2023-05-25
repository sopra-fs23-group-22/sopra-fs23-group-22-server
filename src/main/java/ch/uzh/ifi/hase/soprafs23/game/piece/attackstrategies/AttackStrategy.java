package ch.uzh.ifi.hase.soprafs23.game.piece.attackstrategies;

import ch.uzh.ifi.hase.soprafs23.game.board.Square;

public interface AttackStrategy {
    public AttackResult attack(Square sourceSquare, Square targetSquare);
}
