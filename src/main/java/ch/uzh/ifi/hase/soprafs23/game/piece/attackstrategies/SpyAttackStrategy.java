package ch.uzh.ifi.hase.soprafs23.game.piece.attackstrategies;

import ch.uzh.ifi.hase.soprafs23.game.board.Square;

import static ch.uzh.ifi.hase.soprafs23.game.piece.PieceType.FLAG;
import static ch.uzh.ifi.hase.soprafs23.game.piece.PieceType.MARSHAL;

public class SpyAttackStrategy implements AttackStrategy {
    @Override
    public AttackResult attack(Square sourceSquare, Square targetSquare) {
        // can only attack Marshal or Flag. Otherwise defeated
        if (targetSquare.getContent().getPieceType() == MARSHAL || targetSquare.getContent().getPieceType() == FLAG)
            return AttackResult.SUCCESSFUL;
        else return AttackResult.DEFEATED;
    }
}
