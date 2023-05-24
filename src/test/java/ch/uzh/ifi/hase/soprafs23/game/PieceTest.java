package ch.uzh.ifi.hase.soprafs23.game;

import ch.uzh.ifi.hase.soprafs23.game.army.ArmyType;
import ch.uzh.ifi.hase.soprafs23.game.board.Square;
import ch.uzh.ifi.hase.soprafs23.game.board.SquareType;
import ch.uzh.ifi.hase.soprafs23.game.piece.Piece;
import ch.uzh.ifi.hase.soprafs23.game.piece.PieceType;
import ch.uzh.ifi.hase.soprafs23.game.piece.attackstrategies.AttackResult;
import ch.uzh.ifi.hase.soprafs23.game.piece.movestrategies.MoveResult;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

public class PieceTest {
    /*
    This class does not necessarily test the piece itself, it's more about the attack and move strategies
    that are being tested
     */

    @Mock
    private Square sourcesquare;

    @Mock
    private Square targetsquare;

    @Mock
    private Piece piece;

    @Test
    public void testMove(){
        MockitoAnnotations.openMocks(this);
        //set up the pieces
        //We choose a spy, bomb, and scout so we have all the moveStrategies covered
        Piece spy = new Piece(PieceType.SPY, ArmyType.RED);
        Piece bomb = new Piece(PieceType.BOMB,ArmyType.RED);
        Piece scout = new Piece(PieceType.SCOUT, ArmyType.RED);

        given(sourcesquare.calculateDistanceTo(targetsquare)).willReturn(1);
        given(targetsquare.getType()).willReturn(SquareType.BATTLE_FIELD);

        //distance 1 targetsquare is battlefield
        assertEquals(spy.move(sourcesquare, targetsquare), MoveResult.SUCCESSFUL);
        assertEquals(bomb.move(sourcesquare, targetsquare), MoveResult.FAILED);
        assertEquals(scout.move(sourcesquare, targetsquare), MoveResult.SUCCESSFUL);

        //distance 1 targetsquare is LAKE
        given(targetsquare.getType()).willReturn(SquareType.LAKE);
        assertThrows(IllegalArgumentException.class, () -> spy.move(sourcesquare, targetsquare));
        //bomb doesn't throw since it can't move anyway
        assertEquals(bomb.move(sourcesquare, targetsquare), MoveResult.FAILED);
        assertThrows(IllegalArgumentException.class, () -> scout.move(sourcesquare, targetsquare));

        //change distance to 2, targetsquare is battlefield
        given(sourcesquare.calculateDistanceTo(targetsquare)).willReturn(2);
        given(targetsquare.getType()).willReturn(SquareType.BATTLE_FIELD);
        assertEquals(spy.move(sourcesquare, targetsquare), MoveResult.FAILED);
        assertEquals(bomb.move(sourcesquare, targetsquare), MoveResult.FAILED);
        assertEquals(scout.move(sourcesquare, targetsquare), MoveResult.SUCCESSFUL);

        //moving diagonally - calculateDistance returns -1
        given(sourcesquare.calculateDistanceTo(targetsquare)).willReturn(-1);
        assertEquals(spy.move(sourcesquare, targetsquare), MoveResult.FAILED);
        assertEquals(bomb.move(sourcesquare, targetsquare), MoveResult.FAILED);
        assertEquals(scout.move(sourcesquare, targetsquare), MoveResult.FAILED);
    }

    @Test
    public void testAttack(){
        MockitoAnnotations.openMocks(this);
        //Since attacking is one of the core mechanics of the game we will test all attacks against all pieces
        ArrayList<Piece> pieces = new ArrayList<>();
        //setup all pieces of blue team
        pieces.add(new Piece(PieceType.SPY, ArmyType.BLUE));
        pieces.add(new Piece(PieceType.SCOUT, ArmyType.BLUE));
        pieces.add(new Piece(PieceType.MINER, ArmyType.BLUE));
        pieces.add(new Piece(PieceType.SERGEANT, ArmyType.BLUE));
        pieces.add(new Piece(PieceType.LIEUTENANT, ArmyType.BLUE));
        pieces.add(new Piece(PieceType.CAPTAIN, ArmyType.BLUE));
        pieces.add(new Piece(PieceType.MAJOR, ArmyType.BLUE));
        pieces.add(new Piece(PieceType.COLONEL, ArmyType.BLUE));
        pieces.add(new Piece(PieceType.GENERAL, ArmyType.BLUE));
        pieces.add(new Piece(PieceType.MARSHAL, ArmyType.BLUE));

        //enemy pieces
        ArrayList<Piece> enemies = new ArrayList<>();
        enemies.add(new Piece(PieceType.SPY, ArmyType.RED));
        enemies.add(new Piece(PieceType.SCOUT, ArmyType.RED));
        enemies.add(new Piece(PieceType.MINER, ArmyType.RED));
        enemies.add(new Piece(PieceType.SERGEANT, ArmyType.RED));
        enemies.add(new Piece(PieceType.LIEUTENANT, ArmyType.RED));
        enemies.add(new Piece(PieceType.CAPTAIN, ArmyType.RED));
        enemies.add(new Piece(PieceType.MAJOR, ArmyType.RED));
        enemies.add(new Piece(PieceType.COLONEL, ArmyType.RED));
        enemies.add(new Piece(PieceType.GENERAL, ArmyType.RED));
        enemies.add(new Piece(PieceType.MARSHAL, ArmyType.RED));
        enemies.add(new Piece(PieceType.BOMB, ArmyType.RED));
        enemies.add(new Piece(PieceType.FLAG, ArmyType.RED));

        given(targetsquare.getContent()).willReturn(piece);

        //iterate through all enemy pieces
        for(Piece enemy : enemies){
            given(piece.getPieceType()).willReturn(enemy.getPieceType());
            //iterate through all attacking pieces
            for (Piece attacker : pieces) {
                given(sourcesquare.getContent()).willReturn(attacker);
                if(enemy.getPieceType() == PieceType.FLAG){
                    assertEquals(AttackResult.SUCCESSFUL, attacker.attack(sourcesquare, targetsquare));
                }else if(enemy.getPieceType() == PieceType.BOMB){
                    if(attacker.getPieceType() == PieceType.MINER){
                        assertEquals(AttackResult.SUCCESSFUL, attacker.attack(sourcesquare, targetsquare));
                    }else{
                        assertEquals(AttackResult.DEFEATED, attacker.attack(sourcesquare, targetsquare));
                    }
                }else if(attacker.getPieceType().getRank().ordinal() > enemy.getPieceType().getRank().ordinal() || attacker.getPieceType() == PieceType.SPY && enemy.getPieceType() == PieceType.MARSHAL){
                    assertEquals(AttackResult.SUCCESSFUL, attacker.attack(sourcesquare, targetsquare));
                }else if(attacker.getPieceType().equals(enemy.getPieceType())){
                    assertEquals(AttackResult.BOTH_DEFEATED, attacker.attack(sourcesquare, targetsquare));
                }else{
                    assertEquals(AttackResult.DEFEATED, attacker.attack(sourcesquare, targetsquare));
                }
            }
        }

    }
}
