package student_player;

import boardgame.Move;

import pentago_twist.PentagoMove;
import pentago_twist.PentagoPlayer;
import pentago_twist.PentagoBoardState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** A player file submitted by a student. */
public class StudentPlayer extends PentagoPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260849921");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(PentagoBoardState boardState) {
        // You probably will make separate functions in MyTools.
        // For example, maybe you'll need to load some pre-processed best opening
        // strategies...
        //MyTools.getSomething();
        long startTime = System.currentTimeMillis();
        int time=1990;
        // Is random the best you can do?
        ArrayList<PentagoMove> moves=boardState.getAllLegalMoves();
        if (moves.size()==1){
            return moves.get(0);
        }
        List<Integer> Codedmoves=MoveCoder.CodeMoveArrayList(moves);
        PentagoMove myMove=MoveCoder.DeCodeMove(MCTree.getbestmove(time - System.currentTimeMillis() + startTime,Codedmoves,boardState));
        //System.out.print(myMove.toPrettyString());
        // Return your move to be processed by the server.
        return myMove;
    }
}