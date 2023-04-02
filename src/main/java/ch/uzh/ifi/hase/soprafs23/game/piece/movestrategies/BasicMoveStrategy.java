package ch.uzh.ifi.hase.soprafs23.game.piece.movestrategies;

import ch.uzh.ifi.hase.soprafs23.game.board.Axis;
import ch.uzh.ifi.hase.soprafs23.game.board.Square;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;

import static ch.uzh.ifi.hase.soprafs23.game.board.SquareType.LAKE;
import static ch.uzh.ifi.hase.soprafs23.game.piece.movestrategies.MoveResult.FAILED;
import static ch.uzh.ifi.hase.soprafs23.game.piece.movestrategies.MoveResult.SUCCESSFUL;

public class BasicMoveStrategy implements MoveStrategy {
    @Override
    public MoveResult move(Square currentSquare, Square targetSquare) {
        // can only move to adjacent squares
        if (targetSquare.getType() == LAKE)
            throw new IllegalArgumentException("Cannot move to a lake");
        if (targetSquare.getContent() != null)
            throw new IllegalArgumentException("Cannot move to a square that is already occupied");
        // calculate the distance between the two squares
        if (currentSquare.calculateDistanceTo(targetSquare) > 1) return FAILED;
        // move the piece
        return SUCCESSFUL;
    }
}
