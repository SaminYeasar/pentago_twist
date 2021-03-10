package alpha_beta;

import pentago_twist.PentagoMove;

import java.util.ArrayList;

/**
 * @author mgrenander
 */
public class Node {
    private PentagoMove move;
    private int moveValue;

    Node(PentagoMove move, int moveValue) {
        this.move = move;
        this.moveValue = moveValue;
    }

    public PentagoMove getMove() { return move; }
    public int getMoveValue() { return moveValue; }
}
