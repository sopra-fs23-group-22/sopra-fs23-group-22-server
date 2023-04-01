package ch.uzh.ifi.hase.soprafs23.game;

import ch.uzh.ifi.hase.soprafs23.game.army.Army;
import ch.uzh.ifi.hase.soprafs23.game.board.Axis;
import ch.uzh.ifi.hase.soprafs23.game.board.Board;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;
import ch.uzh.ifi.hase.soprafs23.game.piece.PieceType;
import ch.uzh.ifi.hase.soprafs23.game.states.GameState;

import java.util.ArrayList;

import static ch.uzh.ifi.hase.soprafs23.game.army.ArmyType.BLUE;
import static ch.uzh.ifi.hase.soprafs23.game.army.ArmyType.RED;
import static ch.uzh.ifi.hase.soprafs23.game.states.GameState.IN_PROGRESS;
import static ch.uzh.ifi.hase.soprafs23.game.states.GameState.PRE_PLAY;

public class Game {
    private ArrayList<Player> players;
    private Board board;
    private Player operatingPlayer;
    private GameState gameState;
    //private int turnNum;
    private Player winner;
    //private GameController controller;

    public void setup(ArrayList<Integer> userIds){
        if (this.board == null) board = new Board();
        else board.clear();
        players.clear();
        players.add(new Player(userIds.get(0), new Army(RED)));
        players.add(new Player(userIds.get(1), new Army(BLUE)));
        gameState = PRE_PLAY;
    }

    // The function to place a piece for the initial board
    // (not necessarily using this approach, may implement placement at frontend and put them in backend at once)
    public void placePieceForInitialBoard(Piece piece, Axis[] targetAxis) {
        // check if the game is in PRE_PLAY
        if (gameState != PRE_PLAY)
            throw new IllegalStateException("The game state is not PRE_PLAY!");
        // check if the target square has been occupied
        if (board.getSquareViaAxis(targetAxis).getContent() != null)
            throw new IllegalStateException("The chosen square has been occupied by another piece!");
        board.place(piece, targetAxis);
    }

    public void switchTurn(){

    }
    public boolean hasWinner(){

    }

    public Player getOperatingPlayer(){
        return this.operatingPlayer;
    }
    public GameState getGameState(){
        return this.gameState;
    }

    //public GameController getGameController(){}
    public void resign(Player playerResigned){

    }
}
