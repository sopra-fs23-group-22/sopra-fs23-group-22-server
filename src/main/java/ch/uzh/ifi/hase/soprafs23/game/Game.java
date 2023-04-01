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
        //gameState = PRE_PLAY;
    }

    public void prePlay() {
        if (gameState != PRE_PLAY) throw new IllegalStateException("The game state is not PRE_PLAY!");
        this.gameState = PRE_PLAY;
    }

    public void placePiece(Piece piece, Axis[] targetAxis) { board.place(piece, targetAxis); }

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
