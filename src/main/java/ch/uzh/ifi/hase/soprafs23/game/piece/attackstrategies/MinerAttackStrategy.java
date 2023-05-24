package ch.uzh.ifi.hase.soprafs23.game.piece.attackstrategies;

import ch.uzh.ifi.hase.soprafs23.game.board.Square;
import ch.uzh.ifi.hase.soprafs23.game.piece.Rank;

import static ch.uzh.ifi.hase.soprafs23.game.piece.PieceType.*;

public class MinerAttackStrategy implements AttackStrategy {
    Rank rank;
    public MinerAttackStrategy(Rank rank) {this.rank = rank;}
    @Override
    public AttackResult attack(Square sourceSquare, Square targetSquare) {
        // only attacks bomb, otherwise defeated.
        if (targetSquare.getContent().getPieceType() == BOMB || rank.compareTo(targetSquare.getContent().getPieceType().getRank()) > 0 || targetSquare.getContent().getPieceType() == FLAG)
            return AttackResult.SUCCESSFUL;
        else if(targetSquare.getContent().getPieceType() == MINER){
            return AttackResult.BOTH_DEFEATED;
        }
        else return AttackResult.DEFEATED;
    }
}
