package ch.uzh.ifi.hase.soprafs23.game.piece.movestrategies;

import ch.uzh.ifi.hase.soprafs23.game.board.Square;

public class BombMoveStrategy implements MoveStrategy {
    @Override
    public MoveResult move(Square currentSquare, Square targetSquare) {
        return MoveResult.FAILED;
    }
}
