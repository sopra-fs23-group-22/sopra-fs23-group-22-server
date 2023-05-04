package ch.uzh.ifi.hase.soprafs23.rest.dto;

import java.util.List;

public class SocketMessageDTO {

    List<SquareGETDTO> board;
    String player;


    public List<SquareGETDTO> getBoard() { return board; }

    public void setBoard(List<SquareGETDTO> board) {
        this.board = board;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }
}
