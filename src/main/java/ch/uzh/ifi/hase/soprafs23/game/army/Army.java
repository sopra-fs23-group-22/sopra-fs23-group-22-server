package ch.uzh.ifi.hase.soprafs23.game.army;

import ch.uzh.ifi.hase.soprafs23.game.states.AliveState;

public class Army {
    private ArmyType type;
    //private Pieces[] pieces;

    public Army(ArmyType type) { this.type = type; }

    public ArmyType getType(){ return this.type; }
    /*public Piece getPieceViaAxis(Axis[] axis){

    }*/
    public AliveState isAlive() {

    }

}
