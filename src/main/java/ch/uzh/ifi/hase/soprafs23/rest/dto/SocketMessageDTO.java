package ch.uzh.ifi.hase.soprafs23.rest.dto;

import java.util.List;

public class SocketMessageDTO {

    List<SquareGETDTO> board;
    Long currentPlayerId;
    Long winnerId = -1L; // ... the id of the winner, -1 by default means no winner yet
    Long playerIdResigned = -1L; // ... the id of the player who resigned, -1 by default means no one resigned yet


    public List<SquareGETDTO> getBoard() {
        return board;
    }

    public void setBoard(List<SquareGETDTO> board) {
        this.board = board;
    }

    public Long getCurrentPlayerId() {
        return currentPlayerId;
    }

    public void setCurrentPlayerId(Long playerId) {
        this.currentPlayerId = playerId;
    }

    public Long getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Long winnerId) {
        this.winnerId = winnerId;
    }

    public Long getPlayerIdResigned() {
        return playerIdResigned;
    }

    public void setPlayerIdResigned(Long playerIdResigned) {
        this.playerIdResigned = playerIdResigned;
    }
}
