package ch.uzh.ifi.hase.soprafs23.game.piece.attackstrategies;

import ch.uzh.ifi.hase.soprafs23.game.board.Axis;
import ch.uzh.ifi.hase.soprafs23.game.board.Square;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;

public interface AttackStrategy {
    public AttackResult attack(Square sourceSquare, Square targetSquare);
}
