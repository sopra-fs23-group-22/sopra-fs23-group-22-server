package ch.uzh.ifi.hase.soprafs23.game.piece.attackstrategies;

import ch.uzh.ifi.hase.soprafs23.game.board.Square;

import static ch.uzh.ifi.hase.soprafs23.game.piece.PieceType.BOMB;

public class MinerAttackStrategy implements AttackStrategy {
    @Override
    public AttackResult attack(Square sourceSquare, Square targetSquare) {
        // only attacks bomb, otherwise defeated.
        if (targetSquare.getContent().getPieceType() == BOMB)
            return AttackResult.SUCCESSFUL;
        else return AttackResult.DEFEATED;
    }
}
