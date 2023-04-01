package ch.uzh.ifi.hase.soprafs23.game.board;


import ch.uzh.ifi.hase.soprafs23.game.Player;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;

import java.util.ArrayList;

import static ch.uzh.ifi.hase.soprafs23.game.board.SquareType.LAKE;

public class Board {
    private Square[][] square;

    public Board() {
        // Initialisation of the board. Each grid is a square object.
        for (int i=0; i<10; i++) {
            for (int j=0; j<10; j++) {
                this.square[i][j] = new Square(Axis.values()[i], Axis.values()[j]);
            }
        }
        // Specifying the lake areas
        square[2][4].setType(LAKE);
        square[2][5].setType(LAKE);
        square[3][4].setType(LAKE);
        square[2][5].setType(LAKE);
        square[6][4].setType(LAKE);
        square[6][5].setType(LAKE);
        square[7][4].setType(LAKE);
        square[7][5].setType(LAKE);
    }

    public Square getSquareViaAxis(Axis[] axis) { return this.square[axis[0].getInt()][axis[1].getInt()]; }
    public Piece getPieceViaAxis(Axis[] axis){
        return this.square[axis[0].getInt()][axis[1].getInt()].getContent();
    }

    public void place(Piece piece, Axis[] targetAxis) {
        /*
        if (square[targetAxis[0].getInt()][targetAxis[1].getInt()].getContent() != null)
            throw new IllegalStateException("Target square has been occupied!");
         */
        square[targetAxis[0].getInt()][targetAxis[1].getInt()].setContent(piece);
    }

    public boolean isPlayerPiecesPlaced(Player player) {
        int count = 0;
        for (int i = 0; i < square.length; i++) {
            for (int j = 0; j < square[i].length; j++) {
                if (square[i][j].getContent().getArmyType() == player.getArmy().getType()) {
                    count++;
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

    public void movePiece(Axis[] sourceAxis, Axis[] targetAxis) {
        if (square[targetAxis[0].getInt()][targetAxis[1].getInt()].getType() == LAKE)
            throw new IllegalStateException("Target square is a lake!");
        Piece piece = getPieceViaAxis(sourceAxis);
        place(piece, targetAxis);
        square[sourceAxis[0].getInt()][sourceAxis[1].getInt()].clear();
    }

    public void attackPiece(Axis[] sourceAxis, Axis[] targetAxis) {
        Piece piece = getPieceViaAxis(sourceAxis);
        piece.attack(targetAxis);
    }
}
