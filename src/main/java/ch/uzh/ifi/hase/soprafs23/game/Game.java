package ch.uzh.ifi.hase.soprafs23.game;

import ch.uzh.ifi.hase.soprafs23.game.army.Army;
import ch.uzh.ifi.hase.soprafs23.game.board.Axis;
import ch.uzh.ifi.hase.soprafs23.game.board.Board;
import ch.uzh.ifi.hase.soprafs23.game.board.Square;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;
import ch.uzh.ifi.hase.soprafs23.game.piece.PieceType;
import ch.uzh.ifi.hase.soprafs23.game.piece.attackstrategies.AttackResult;
import ch.uzh.ifi.hase.soprafs23.game.piece.movestrategies.MoveResult;
import ch.uzh.ifi.hase.soprafs23.game.states.GameState;

import java.util.ArrayList;

import static ch.uzh.ifi.hase.soprafs23.game.army.ArmyType.BLUE;
import static ch.uzh.ifi.hase.soprafs23.game.army.ArmyType.RED;
import static ch.uzh.ifi.hase.soprafs23.game.board.SquareType.LAKE;
import static ch.uzh.ifi.hase.soprafs23.game.states.AliveState.DOWN;
import static ch.uzh.ifi.hase.soprafs23.game.states.GameState.*;

public class Game {
    private int gameId;
    private ArrayList<Player> players = new ArrayList<Player>();
    private Board board;
    private Player operatingPlayer;
    private GameState gameState;
    private Player winner;

    public void setup(ArrayList<Long> userIds){
        if (this.board == null) board = new Board();
        else board.clear();
        players.clear();
        players.add(new Player(userIds.get(0), new Army(RED)));
        players.add(new Player(userIds.get(1), new Army(BLUE)));
        gameState = PRE_PLAY;
    }

    public void placePieces(Piece[] pieceArray){
        //convert the array with pieces to actual positions on the board
        Player redPlayer = players.get(0);
        Player bluePlayer = players.get(1);
        if(pieceArray[0].getArmyType() == RED){
            for(int i=0; i<10; i++){
                for(int j=6; j<10; j++){
                    board.setPiece(i,j,pieceArray[(j-6)*10+i]);
                }
            }
            redPlayer.getArmy().setArmyPieces(pieceArray);
        }else{
            for(int i=0; i<10; i++){
                for(int j=0; j<4; j++){
                    board.setPiece(i,j,pieceArray[j*10 + i]);
                }
            }
            bluePlayer.getArmy().setArmyPieces(pieceArray);
        }

    }
    // The function to place a piece for the initial board
    // (not necessarily using this approach, may implement placement at frontend and put them in backend at once)
    // (should write with the frontend team to decide)
    /*
    public void placePieceForInitialBoard(Piece piece, Axis[] targetAxis) {
        // check if the game is in PRE_PLAY
        if (gameState != PRE_PLAY)
            throw new IllegalStateException("The game state is not PRE_PLAY!");
        // check if the target square has been occupied
        if (board.getSquareViaAxis(targetAxis).getContent() != null)
            throw new IllegalStateException("The chosen square has been occupied by another piece!");
        board.place(piece, targetAxis);
    }
     */

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
        if (sourceAxis == targetAxis) return; // ... does nothing if the source and target are the same
        if (board.getSquareViaAxis(sourceAxis).getContent() == null)
            throw new IllegalStateException("The source square has no piece to move!");
        // if the target square has been occupied, then it is an attack
        //  ... if source piece is a Scout, the path it moves over should not be LAKE and should have no piece
        if (board.getSquareViaAxis(sourceAxis).getContent().getPieceType() == PieceType.SCOUT) {
            // ... get squares along the path
            Square[] path = board.getPath(sourceAxis, targetAxis);
            // ... check if the path has piece or LAKE
            for (Square square : path) {
                if (square.getContent() != null)
                    throw new IllegalStateException("The path has been blocked by another piece!");
                    //return; // (just for now, should throw exception)
                if (square.getType() == LAKE)
                    throw new IllegalStateException("The path has been blocked by a lake!");
                    //return; // (just for now, should throw exception)
            }
        }
        // ... if source piece is a bomb or flag, it cannot move, this operation is invalid
        if (board.getSquareViaAxis(sourceAxis).getContent().getPieceType() == PieceType.BOMB ||
                board.getSquareViaAxis(sourceAxis).getContent().getPieceType() == PieceType.FLAG)
            throw new IllegalStateException("The bomb and flag piece cannot move!");
            //return; // (just for now, should throw exception)
        if (board.getSquareViaAxis(targetAxis).getContent() != null) {
            // if the target square has a piece of the same army, then it is an invalid move
            if (board.getSquareViaAxis(sourceAxis).getContent().getArmyType() ==
                    board.getSquareViaAxis(targetAxis).getContent().getArmyType())
                throw new IllegalStateException("You cannot attack your own army!");
                //return; // (just for now, should throw exception)
            AttackResult result = board.attackPiece(sourceAxis, targetAxis);
            if (result == AttackResult.ILLEGAL_MOVE)
                throw new IllegalStateException("Illegal move! Normal pieces other than scout can only move one square and not diagonally!");
            else switchTurn();
        } else {
            // if the target square has not been occupied, then it is a placement
            MoveResult result = board.movePiece(sourceAxis, targetAxis);
            if (result == MoveResult.SUCCESSFUL) switchTurn();
            if (result == MoveResult.FAILED)
                throw new IllegalStateException("Illegal move! Normal pieces other than scout can only move one square and not diagonally!");
        }
        // check if there is a winner
        if (hasWinner()) gameState = WAITING;
    }

    public boolean hasWinner(){
        // Two scenarios if there is a winner:
        //  1. if one's Flag is captured, then the other wins
        for (Player player : players) {
            for (Piece piece : player.getArmy().getPieces()) {
                // ... print out whether flag's state is DOWN
                // code below!
                //System.out.println(piece.getPieceType() + " " + piece.getAliveState());
                if (piece.getPieceType() == PieceType.FLAG && piece.getAliveState() == DOWN) {
                    System.out.println("The flag of " + player.getUserId() + " is captured!");
                    this.winner = (player == players.get(0)) ? players.get(1) : players.get(0);
                    gameState = WAITING;
                    return true;
                }
            }
        }
        //  2. if all the pieces of one's army are captured, then the other wins
        for (Player player : players) {
            // if all the pieces (except FLAG and BOMB) of one's army are captured (aliveState == DOWN)
            //if (player.getArmy().getPieces().stream().allMatch(piece -> piece.getAliveState() == DOWN)) {
            if (player.getArmy().getPieces().stream().filter(piece -> piece.getPieceType() != PieceType.FLAG &&
                    piece.getPieceType() != PieceType.BOMB).allMatch(piece -> piece.getAliveState() == DOWN)) {
                this.winner = (player == players.get(0)) ? players.get(1) : players.get(0);
                gameState = WAITING;
                return true;
            }
        }
        return false;
    }

    public Player getOperatingPlayer(){ return this.operatingPlayer; }
    public GameState getGameState(){ return this.gameState; }

    //public GameController getGameController(){}
    public void resign(Player playerResigned){
        // the player resigned loses the game, another wins
        winner = (playerResigned == players.get(0)) ? players.get(1) : players.get(0);
        // ... change the game state to WAITING
        gameState = WAITING;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public Board getBoard() {
        return board;
    }

    public Player getWinner() { return winner; }
}
