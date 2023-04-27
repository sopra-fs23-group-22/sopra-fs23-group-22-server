package ch.uzh.ifi.hase.soprafs23.rest.dto;

import java.util.List;

public class SocketMessageDTO {

    List<SquareGETDTO> board;
    long currentPlayerId;
    long winner = -1; // -1 = no winner

    public List<SquareGETDTO> getBoard() {
        return board;
    }

    public void setBoard(List<SquareGETDTO> board) {
        this.board = board;
    }

    public long getCurrentPlayerId() {
        return currentPlayerId;
    }

    public void setCurrentPlayerId(long currentPlayerId) {
        this.currentPlayerId = currentPlayerId;
    }

    public long getWinner() {
        return winner;
    }

    public void setWinner(long winner) {
        this.winner = winner;
    }
}
