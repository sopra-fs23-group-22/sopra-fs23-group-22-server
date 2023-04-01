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
import static ch.uzh.ifi.hase.soprafs23.game.states.AliveState.DOWN;
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

    public boolean isPlayerPrePlayCompleted(Player player) { return board.isPlayerPiecesPlaced(player); }
    public boolean isPrePlayCompleted() {
        return (isPlayerPrePlayCompleted(players.get(0)) && isPlayerPrePlayCompleted(players.get(1)));
    }

    public void start() {
        if (!isPrePlayCompleted()) throw new IllegalStateException("The game is not ready to start!");
        gameState = IN_PROGRESS;
        operatingPlayer = players.get(0);
    }

    public void switchTurn() {
        if (operatingPlayer == players.get(0)) operatingPlayer = players.get(1);
        else operatingPlayer = players.get(0);
    }

    /*
    // used for displaying available options for an operation
    public Axis[][] getAvailableTargets(Piece piece) {
        return board.getAvailableTargets(piece);
    }
     */

    public void operate(Axis[] sourceAxis, Axis[] targetAxis) {
        if (gameState != IN_PROGRESS) throw new IllegalStateException("The game is not in progress!");
        if (board.getSquareViaAxis(sourceAxis).getContent() == null)
            throw new IllegalStateException("The source square has no piece!");
        if (board.getSquareViaAxis(targetAxis).getContent() != null) {
            // if the target square has been occupied, then it is an attack
            board.attackPiece(sourceAxis, targetAxis);
        } else {
            // if the target square has not been occupied, then it is a placement
            board.movePiece(sourceAxis, targetAxis);
        }
    }

    public boolean hasWinner(){
        // Two approaches to check if there is a winner:
        //  1. if one's Flag is captured, then the other wins
        boolean hasWinner = false;
        for (Player player : players) {
            for (Piece piece : player.getArmy().getPieces()) {
                if (piece.getPieceType() == PieceType.FLAG && piece.getAliveState() == DOWN) {
                    hasWinner = true;
                    winner = (player == players.get(0)) ? players.get(1) : players.get(0);
                    gameState = GameState.WAITING;
                    break;
                }
            }
        }
        //  2. if all the pieces of one's army are captured, then the other wins
        for (Player player : players) {
            // if all the pieces of one's army are captured (aliveState == DOWN)
            if (player.getArmy().getPieces().stream().allMatch(piece -> piece.getAliveState() == DOWN)) {
                hasWinner = true;
                winner = (player == players.get(0)) ? players.get(1) : players.get(0);
                gameState = GameState.WAITING;
                break;
            }
        }
    }

    public Player getOperatingPlayer(){ return this.operatingPlayer; }
    public GameState getGameState(){ return this.gameState; }

    //public GameController getGameController(){}
    public void resign(Player playerResigned){
        winner = playerResigned;
        gameState = GameState.WAITING;
    }
}
