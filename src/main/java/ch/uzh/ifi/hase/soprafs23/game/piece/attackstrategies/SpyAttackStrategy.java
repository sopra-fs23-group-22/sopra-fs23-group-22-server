package ch.uzh.ifi.hase.soprafs23.game.piece.attackstrategies;

import ch.uzh.ifi.hase.soprafs23.game.board.Square;

import static ch.uzh.ifi.hase.soprafs23.game.piece.PieceType.*;

public class SpyAttackStrategy implements AttackStrategy {
    @Override
    public AttackResult attack(Square sourceSquare, Square targetSquare) {
        // can only attack Marshal or Flag. Otherwise defeated
        if (targetSquare.getContent().getPieceType() == MARSHAL || targetSquare.getContent().getPieceType() == FLAG)
            return AttackResult.SUCCESSFUL;
        else if (targetSquare.getContent().getPieceType() == SPY) {
            return AttackResult.BOTH_DEFEATED;
        }
        else return AttackResult.DEFEATED;
    }
}
