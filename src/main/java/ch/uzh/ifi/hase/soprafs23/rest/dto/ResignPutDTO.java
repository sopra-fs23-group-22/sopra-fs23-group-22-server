package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.game.Player;

public class ResignPutDTO {
    private long playerIdResigned;
    public long getPlayerIdResigned() { return playerIdResigned; }
    public void setPlayerIdResigned(long playerIdResigned) { this.playerIdResigned = playerIdResigned; }
}
