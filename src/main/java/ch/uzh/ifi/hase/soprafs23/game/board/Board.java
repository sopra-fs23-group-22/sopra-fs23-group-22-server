package ch.uzh.ifi.hase.soprafs23.game.board;


import ch.uzh.ifi.hase.soprafs23.game.Player;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;
import ch.uzh.ifi.hase.soprafs23.game.piece.attackstrategies.AttackResult;
import ch.uzh.ifi.hase.soprafs23.game.piece.movestrategies.MoveResult;
import ch.uzh.ifi.hase.soprafs23.game.states.AliveState;

import java.sql.SQLOutput;
import java.util.ArrayList;

import static ch.uzh.ifi.hase.soprafs23.game.board.SquareType.LAKE;
import static ch.uzh.ifi.hase.soprafs23.game.piece.attackstrategies.AttackResult.DEFEATED;
import static ch.uzh.ifi.hase.soprafs23.game.piece.attackstrategies.AttackResult.SUCCESSFUL;
import static ch.uzh.ifi.hase.soprafs23.game.piece.movestrategies.MoveResult.FAILED;
import static ch.uzh.ifi.hase.soprafs23.game.states.AliveState.DOWN;

public class Board {
    private Square[][] square = new Square[10][10];

    public Board() {
        // Initialisation of the board. Each grid is a square object.
        for (int i=0; i<10; i++) {
            for (int j=0; j<10; j++) {
                this.square[i][j] = new Square(Axis.values()[i], Axis.values()[j]);
            }
        }
        // Specifying the lake areas
        square[4][2].setType(LAKE);
        square[5][2].setType(LAKE);
        square[4][3].setType(LAKE);
        square[5][3].setType(LAKE);
        square[4][6].setType(LAKE);
        square[5][6].setType(LAKE);
        square[4][7].setType(LAKE);
        square[5][7].setType(LAKE);
    }

    public Square getSquareViaAxis(Axis[] axis) { return this.square[axis[0].getInt()][axis[1].getInt()]; }
    public Piece getPieceViaAxis(Axis[] axis){
        return this.square[axis[0].getInt()][axis[1].getInt()].getContent();
    }

    public void place(Piece piece, Square targetSquare) {
        /*
        if (square[targetAxis[0].getInt()][targetAxis[1].getInt()].getContent() != null)
            throw new IllegalStateException("Target square has been occupied!");
         */
        targetSquare.setContent(piece);
    }

    public boolean isPlayerPiecesPlaced(Player player) {
        int count = 0;
        for (int i = 0; i < square.length; i++) {
            for (int j = 0; j < square[i].length; j++) {
                if (square[i][j].getContent() != null && square[i][j].getType() != LAKE) {
                    if (square[i][j].getContent().getArmyType() == player.getArmy().getType()) {
                        count++;
                    }
                }
            }
        }
        if (count == 40) return true;
        return false;
    }

    public void clear() {
        for (int i = 0; i < square.length; i++) {
            for (int j = 0; j < square[i].length; j++) {
                square[i][j].clear();
            }
        }
    }

    public MoveResult movePiece(Axis[] sourceAxis, Axis[] targetAxis) {
        // the target square must be empty and not a LAKE
        if (square[targetAxis[0].getInt()][targetAxis[1].getInt()].getType() == LAKE)
            throw new IllegalStateException("Target square is a lake!");
        if (square[targetAxis[0].getInt()][targetAxis[1].getInt()].getContent() != null)
            throw new IllegalStateException("Target square has been occupied!");
        Piece piece = getPieceViaAxis(sourceAxis);
        Square sourceSquare = getSquareViaAxis(sourceAxis);
        Square targetSquare = getSquareViaAxis(targetAxis);
        MoveResult result = piece.move(sourceSquare, targetSquare);
        if (result == MoveResult.SUCCESSFUL) {
            targetSquare.setContent(piece);
            sourceSquare.clear();
        }
        return result;
    }

    public void attackPiece(Axis[] sourceAxis, Axis[] targetAxis) {
        Piece attacker = getPieceViaAxis(sourceAxis);
        Square targetSquare = getSquareViaAxis(targetAxis);
        Square sourceSquare = getSquareViaAxis(sourceAxis);
        if ( attacker.move(sourceSquare, targetSquare) == FAILED ) { return; }
        AttackResult result = attacker.attack(sourceSquare, targetSquare);
        // 1. If the attack is successful, the target square is cleared.
        //      and the attacker moves to the target square.
        if (result == SUCCESSFUL) {
            System.out.println("attack successfully");
            targetSquare.getContent().setAliveState(DOWN);
            targetSquare.clear();
            System.out.println("movePiece() after attack invoked!");
            //movePiece(sourceAxis, targetAxis);
            targetSquare.setContent(attacker);
            sourceSquare.clear();
        }
        // 2. If attack result is DEFEATED, the attacker is captured.
        else if (result == DEFEATED) {
            System.out.println("attack fail");
            attacker.setAliveState(DOWN);
            square[sourceAxis[0].getInt()][sourceAxis[1].getInt()].clear();
        }
        //  3. If result is BOTH_DEFEATED, both attacker and target are captured.
        else {
            attacker.setAliveState(DOWN);
            targetSquare.getContent().setAliveState(DOWN);
            square[sourceAxis[0].getInt()][sourceAxis[1].getInt()].clear();
            targetSquare.clear();
        }
    }

    public Square getSquare(int axisX, int axisY) {
        return square[axisX][axisY];
    }
    public void setPiece(int axisX, int axisY, Piece piece){
        this.square[axisY][axisX].setContent(piece);
    }

}
