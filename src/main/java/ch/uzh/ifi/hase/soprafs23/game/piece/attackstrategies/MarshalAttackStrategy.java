package ch.uzh.ifi.hase.soprafs23.game.piece.attackstrategies;

import ch.uzh.ifi.hase.soprafs23.game.board.Axis;
import ch.uzh.ifi.hase.soprafs23.game.board.Square;
import ch.uzh.ifi.hase.soprafs23.game.piece.PieceType;
import ch.uzh.ifi.hase.soprafs23.game.piece.Rank;

import static ch.uzh.ifi.hase.soprafs23.game.piece.PieceType.BOMB;

public class MarshalAttackStrategy implements AttackStrategy {

    @Override
    public void attack(Square targetSquare) {
        Rank rank = PieceType.MARSHAL.getRank();
        // if target is Bomb, then attacker itself is captured
        if (targetSquare.getContent().getPieceType() == BOMB) {
            // add a enum class named AttackResult to represent the result of attack:
            // 1. attacker wins -> attacker moves to target square
            // 2. attacker loses -> attacker is captured
        }
    }
}
