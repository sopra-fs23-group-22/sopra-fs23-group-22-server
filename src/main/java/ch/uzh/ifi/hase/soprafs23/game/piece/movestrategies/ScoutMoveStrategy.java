package ch.uzh.ifi.hase.soprafs23.game.piece.movestrategies;

import ch.uzh.ifi.hase.soprafs23.game.board.Square;

import static ch.uzh.ifi.hase.soprafs23.game.board.SquareType.LAKE;

public class ScoutMoveStrategy implements MoveStrategy {
    @Override
    public MoveResult move(Square currentSquare, Square targetSquare) {
        //- may move any number of spaces
        //- the squares of the track should have no content & should not be lake (implemented in upper class)

        //- not diagonally
        if (currentSquare.calculateDistanceTo(targetSquare) == -1) return MoveResult.FAILED;

        if (targetSquare.getType() == LAKE)
            throw new IllegalArgumentException("Cannot move to a lake");
        return MoveResult.SUCCESSFUL;
    }
}
