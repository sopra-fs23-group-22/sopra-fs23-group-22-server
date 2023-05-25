package ch.uzh.ifi.hase.soprafs23.game.piece.movestrategies;

import ch.uzh.ifi.hase.soprafs23.game.board.Square;

public interface MoveStrategy {
    public MoveResult move(Square currentSquare, Square targetSquare);
}
