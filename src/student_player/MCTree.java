package student_player;

import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;

import java.util.ArrayList;
import java.util.List;

public class MCTree {
    private static MCTnode root;

/*
Evluate the move by:
        1. Build monto carlo tree and run as manmy as possible times on next possible moves
        2. Evaluate next possible moves with evaluation function
        3. Select the highest score move.
 */
public static int getbestmove(long timeout, List<Integer> codedmoves, PentagoBoardState boardState){
    long startTime = System.currentTimeMillis();
    root = new MCTnode(-1,null);
    root.addchildren(codedmoves);
    //System.out.print("This is size\n");

    while(System.currentTimeMillis()-startTime<timeout){
        MCTnode expectednode=findbestnode();
        runthetree(expectednode,boardState);
    };
    float max=-1;
    MCTnode bestNode = null;
    for(MCTnode i: root.getChildren()){
        if (i.getWinRate()+geteval(i, boardState) >max){
            max=i.getWinRate()+geteval(i, boardState);
            bestNode=i;
        }
    }
    //System.out.println("Eval of this move: " + geteval(bestNode,boardState));
    //System.out.println("Winrate of this mvoe: " + bestNode.getWinRate());

    return bestNode.getmove();
}
    //The method to run the tree and expand the node.
    private static void runthetree(MCTnode node, PentagoBoardState boardState) {


        PentagoBoardState nodestate = node.getstate(boardState);
        //run default policy
        node.addchildren(MoveCoder.CodeMoveArrayList(nodestate.getAllLegalMoves()));
        node=node.getRandomChild();
        PentagoBoardState nodestate2 = node.getstate(boardState);
        while(!nodestate2.gameOver()){
            nodestate2.processMove((PentagoMove) nodestate2.getRandomMove());
        }
        node.feedback(nodestate2.getWinner());
    }

    //Find the best node to run.
    private static MCTnode findbestnode() {
        MCTnode bestnode=root;
        while(bestnode.haschildren()){
            bestnode=bestnode.getMaxQChild();
        }
        return bestnode;
    }
    //Get evaluation score
    public static float geteval(MCTnode node, PentagoBoardState boardState) {
        return Evaluation.eval(boardState,node);
    }

}