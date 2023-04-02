package ch.uzh.ifi.hase.soprafs23.game.piece.movestrategies;

import ch.uzh.ifi.hase.soprafs23.game.board.Axis;
import ch.uzh.ifi.hase.soprafs23.game.board.Square;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;

public interface MoveStrategy {
    public MoveResult move(Square currentSquare, Square targetSquare);
}
