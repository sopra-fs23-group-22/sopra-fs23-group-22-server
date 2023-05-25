package ch.uzh.ifi.hase.soprafs23.game.piece;

import ch.uzh.ifi.hase.soprafs23.game.piece.attackstrategies.AttackStrategy;
import ch.uzh.ifi.hase.soprafs23.game.piece.attackstrategies.BasicAttackStrategy;
import ch.uzh.ifi.hase.soprafs23.game.piece.attackstrategies.MinerAttackStrategy;
import ch.uzh.ifi.hase.soprafs23.game.piece.attackstrategies.SpyAttackStrategy;
import ch.uzh.ifi.hase.soprafs23.game.piece.movestrategies.BasicMoveStrategy;
import ch.uzh.ifi.hase.soprafs23.game.piece.movestrategies.MoveStrategy;
import ch.uzh.ifi.hase.soprafs23.game.piece.movestrategies.NonMoveStrategy;
import ch.uzh.ifi.hase.soprafs23.game.piece.movestrategies.ScoutMoveStrategy;

import static ch.uzh.ifi.hase.soprafs23.game.piece.Rank.*;

public enum PieceType {
    BOMB(B, "Bomb", new NonMoveStrategy(), new BasicAttackStrategy(B)),
    MARSHAL(_10, "Marshal", new BasicMoveStrategy(), new BasicAttackStrategy(_10)),
    GENERAL(_9, "General", new BasicMoveStrategy(), new BasicAttackStrategy(_9)),
    COLONEL(_8, "Colonel", new BasicMoveStrategy(), new BasicAttackStrategy(_8)),
    MAJOR(_7, "Major", new BasicMoveStrategy(), new BasicAttackStrategy(_7)),
    CAPTAIN(_6, "Captain", new BasicMoveStrategy(), new BasicAttackStrategy(_6)),
    LIEUTENANT(_5, "Lieutenant", new BasicMoveStrategy(), new BasicAttackStrategy(_5)),
    SERGEANT(_4, "Sergeant", new BasicMoveStrategy(), new BasicAttackStrategy(_4)),
    MINER(_3, "Miner", new BasicMoveStrategy(), new MinerAttackStrategy(_3)),
    SCOUT(_2, "Scout", new ScoutMoveStrategy(), new BasicAttackStrategy(_2)),
    SPY(_1, "Spy", new BasicMoveStrategy(), new SpyAttackStrategy()),
    FLAG(F, "Flag", new NonMoveStrategy(), new BasicAttackStrategy(F));

    private final Rank rank;
    private final String name;
    private final MoveStrategy moveStrategy;
    private final AttackStrategy attackStrategy;

    PieceType(Rank rank, String name, MoveStrategy moveStrategy, AttackStrategy attackStrategy) {
        this.rank = rank;
        this.name = name;
        this.moveStrategy = moveStrategy;
        this.attackStrategy = attackStrategy;
    }

    public Rank getRank() {
        return this.rank;
    }

    public String getName() {
        return this.name;
    }

    public MoveStrategy getMoveStrategy() {
        return this.moveStrategy;
    }

    public AttackStrategy getAttackStrategy() {
        return this.attackStrategy;
    }
}
