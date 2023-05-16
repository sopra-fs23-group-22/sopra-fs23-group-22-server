package ch.uzh.ifi.hase.soprafs23.game.board;


import ch.uzh.ifi.hase.soprafs23.game.Player;
import ch.uzh.ifi.hase.soprafs23.game.army.ArmyType;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;
import ch.uzh.ifi.hase.soprafs23.game.piece.attackstrategies.AttackResult;
import ch.uzh.ifi.hase.soprafs23.game.piece.movestrategies.MoveResult;
import ch.uzh.ifi.hase.soprafs23.game.states.AliveState;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;

import static ch.uzh.ifi.hase.soprafs23.game.board.SquareType.LAKE;
import static ch.uzh.ifi.hase.soprafs23.game.piece.attackstrategies.AttackResult.*;
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

    public AttackResult attackPiece(Axis[] sourceAxis, Axis[] targetAxis) {
        Piece attacker = getPieceViaAxis(sourceAxis);
        Square targetSquare = getSquareViaAxis(targetAxis);
        Square sourceSquare = getSquareViaAxis(sourceAxis);
        if ( attacker.move(sourceSquare, targetSquare) == FAILED ) { return ILLEGAL_MOVE; }
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
            targetSquare.getContent().setRevealed(true);
            sourceSquare.clear();
        }
        // 2. If attack result is DEFEATED, the attacker is captured.
        else if (result == DEFEATED) {
            System.out.println("attack fail");
            attacker.setAliveState(DOWN);
            targetSquare.getContent().setRevealed(true);
            square[sourceAxis[0].getInt()][sourceAxis[1].getInt()].clear();
        }
        //  3. If result is BOTH_DEFEATED, both attacker and target are captured.
        else {
            attacker.setAliveState(DOWN);
            targetSquare.getContent().setAliveState(DOWN);
            square[sourceAxis[0].getInt()][sourceAxis[1].getInt()].clear();
            targetSquare.clear();
        }
        return result;
    }
    //TODO: getSquare and setPiece use inverted Axis, not sure which one is correct but one of both
    //TODO: methods needs to be refactored
    public Square getSquare(int axisX, int axisY) {
        return square[axisX][axisY];
    }
    public void setPiece(int axisX, int axisY, Piece piece){
        this.square[axisY][axisX].setContent(piece);
    }
    public Square[] getPath(Axis[] sourceAxis, Axis[] targetAxis) {
        // to get the squares along the path from source to target
        // the source and target is not included
        Square[] path = new Square[0];
        int x1 = sourceAxis[0].getInt();
        int y1 = sourceAxis[1].getInt();
        int x2 = targetAxis[0].getInt();
        int y2 = targetAxis[1].getInt();
        int deltaX = x2 - x1;
        int deltaY = y2 - y1;
        int xStep = 0;
        int yStep = 0;
        if(deltaX != 0 && deltaY != 0){
            throw new IllegalArgumentException("The move can't be diagonal");
        }else if(deltaX == 0 && deltaY == 0){
            throw new IllegalArgumentException("Source and target square is equal");
        }
        if (deltaX != 0) xStep = deltaX / Math.abs(deltaX);
        if (deltaY != 0) yStep = deltaY / Math.abs(deltaY);
        int x = x1 + xStep;
        int y = y1 + yStep;
        while (x != x2 || y != y2) {
            path = Arrays.copyOf(path, path.length+1);
            path[path.length-1] = square[x][y];
            x += xStep;
            y += yStep;
        }
        return path;
    }

    public ArrayList<Square> getAvailableTargets(Axis[] sourceAxis) {
        // This is the method to get the available moving options for a piece
        ArrayList<Square> availableTargets = new ArrayList<>();
        Piece piece = getPieceViaAxis(sourceAxis);
        if (piece == null) throw new IllegalStateException("No piece on the source square!");
        // ... get the available targets based on the piece type and surrounding squares
        switch (piece.getPieceType()) {
            // ... Bomb and Flag cannot move
            case BOMB:
            case FLAG:
                break;
            // ... Scout can move any number of squares in a straight line, until it reaches the edge of the board, another piece or LAKE
            case SCOUT:
                // ... get the path from source to the edge of the board
                ArrayList<Square> squaresAlongFourDirections = getSquareAlongFourDirectionsViaAxis(sourceAxis);
                // ... add the squares along the path to the available targets
                for (Square square : squaresAlongFourDirections) {
                    availableTargets.add(square);
                }
                break;
            // ... for other pieces, they can only move one square in any direction, except LAKE, another piece or edge of the board
            default:
                // ... get the surrounding squares
                ArrayList<Square> surroundingSquares = getSurroundingSquaresViaAxis(sourceAxis);
                // ... add the squares to the available targets if they are empty
                for (Square square : surroundingSquares) {
                    if (square.getType() == LAKE) continue;
                    if (square.getContent() == null) availableTargets.add(square);
                    if (square.getContent() != null && square.getContent().getArmyType() != piece.getArmyType())
                        availableTargets.add(square);
                }
                break;
        }
        return availableTargets;
    }

    private ArrayList<Square> getSurroundingSquaresViaAxis(Axis[] sourceAxis) {
        // This is the method to get the surrounding squares of a square
        ArrayList<Square> surroundingSquares = new ArrayList<>();
        int x = sourceAxis[0].getInt();
        int y = sourceAxis[1].getInt();
        // ... get the surrounding squares
        if (x == 0) {
            if (y == 0) {
                surroundingSquares.add(square[x+1][y]);
                surroundingSquares.add(square[x][y+1]);
            } else if (y == 9) {
                surroundingSquares.add(square[x+1][y]);
                surroundingSquares.add(square[x][y-1]);
            } else {
                surroundingSquares.add(square[x+1][y]);
                surroundingSquares.add(square[x][y-1]);
                surroundingSquares.add(square[x][y+1]);
            }
        } else if (x == 9) {
            if (y == 0) {
                surroundingSquares.add(square[x-1][y]);
                surroundingSquares.add(square[x][y+1]);
            } else if (y == 9) {
                surroundingSquares.add(square[x-1][y]);
                surroundingSquares.add(square[x][y-1]);
            } else {
                surroundingSquares.add(square[x-1][y]);
                surroundingSquares.add(square[x][y-1]);
                surroundingSquares.add(square[x][y+1]);
            }
        } else {
            if (y == 0) {
                surroundingSquares.add(square[x-1][y]);
                surroundingSquares.add(square[x+1][y]);
                surroundingSquares.add(square[x][y+1]);
            } else if (y == 9) {
                surroundingSquares.add(square[x-1][y]);
                surroundingSquares.add(square[x+1][y]);
                surroundingSquares.add(square[x][y-1]);
            } else {
                surroundingSquares.add(square[x-1][y]);
                surroundingSquares.add(square[x+1][y]);
                surroundingSquares.add(square[x][y-1]);
                surroundingSquares.add(square[x][y+1]);
            }
        }
        return surroundingSquares;
    }

    private ArrayList<Square> getSquareAlongFourDirectionsViaAxis(Axis[] sourceAxis) {
        // This is the method to get the squares along the four directions of a square
        ArrayList<Square> squaresAlongFourDirections = new ArrayList<>();
        int x = sourceAxis[0].getInt();
        int y = sourceAxis[1].getInt();
        // ... get the squares along the four directions
        boolean continueLeft = true;
        boolean continueRight = true;
        boolean continueUp = true;
        boolean continueDown = true;
        ArmyType armyType = square[x][y].getContent().getArmyType();
        for (int i = 1; i < 10; i++) {
            if (continueLeft) {
                if (x - i < 0) {
                    continueLeft = false;
                } else {
                    if (square[x-i][y].getContent() == null && square[x-i][y].getType() != LAKE) {
                        squaresAlongFourDirections.add(square[x-i][y]);
                    } else if (square[x-i][y].getContent() != null && square[x-i][y].getContent().getArmyType() != armyType) {
                        squaresAlongFourDirections.add(square[x-i][y]);
                        continueLeft = false;
                    } else {
                        continueLeft = false;
                    }
                }
            }
            if (continueRight) {
                if (x + i > 9) {
                    continueRight = false;
                } else {
                    if (square[x+i][y].getContent() == null && square[x+i][y].getType() != LAKE) {
                        squaresAlongFourDirections.add(square[x+i][y]);
                    } else if (square[x+i][y].getContent() != null && square[x+i][y].getContent().getArmyType() != armyType) {
                        squaresAlongFourDirections.add(square[x+i][y]);
                        continueRight = false;
                    } else {
                        continueRight = false;
                    }
                }
            }
            if (continueUp) {
                if (y - i < 0) {
                    continueUp = false;
                } else {
                    if (square[x][y-i].getContent() == null && square[x][y-i].getType() != LAKE) {
                        squaresAlongFourDirections.add(square[x][y-i]);
                    } else if (square[x][y-i].getContent() != null && square[x][y-i].getContent().getArmyType() != armyType) {
                        squaresAlongFourDirections.add(square[x][y-i]);
                        continueUp = false;
                    } else {
                        continueUp = false;
                    }
                }
            }
            if (continueDown) {
                if (y + i > 9) {
                    continueDown = false;
                } else {
                    if (square[x][y+i].getContent() == null && square[x][y+i].getType() != LAKE) {
                        squaresAlongFourDirections.add(square[x][y+i]);
                    } else if (square[x][y+i].getContent() != null && square[x][y+i].getContent().getArmyType() != armyType) {
                        squaresAlongFourDirections.add(square[x][y+i]);
                        continueDown = false;
                    } else {
                        continueDown = false;
                    }
                }
            }
        }
        return squaresAlongFourDirections;
    }
}
