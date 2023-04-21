package ch.uzh.ifi.hase.soprafs23.game.piece.attackstrategies;

import ch.uzh.ifi.hase.soprafs23.game.board.Square;
import ch.uzh.ifi.hase.soprafs23.game.piece.PieceType;
import ch.uzh.ifi.hase.soprafs23.game.piece.Rank;

import static ch.uzh.ifi.hase.soprafs23.game.piece.PieceType.BOMB;
import static ch.uzh.ifi.hase.soprafs23.game.piece.PieceType.FLAG;
import static ch.uzh.ifi.hase.soprafs23.game.piece.attackstrategies.AttackResult.*;
import static ch.uzh.ifi.hase.soprafs23.game.piece.attackstrategies.AttackResult.DEFEATED;

public class BasicAttackStrategy implements AttackStrategy {
    Rank rank;
    public BasicAttackStrategy(Rank rank) { this.rank = rank; }

    @Override
    public AttackResult attack(Square sourceSquare, Square targetSquare) {
        // if target is Bomb, then attacker itself is captured
        if (targetSquare.getContent().getPieceType() == BOMB) return DEFEATED;
        // if target is Flag, then attacker wins
        if (targetSquare.getContent().getPieceType() == FLAG) return SUCCESSFUL;
        // if target is not Bomb, compare the rank of attacker and target
        if (rank.compareTo(targetSquare.getContent().getPieceType().getRank()) > 0) return SUCCESSFUL;
        else if (rank.compareTo(targetSquare.getContent().getPieceType().getRank()) == 0) return BOTH_DEFEATED;
        else return DEFEATED;
    }
}
