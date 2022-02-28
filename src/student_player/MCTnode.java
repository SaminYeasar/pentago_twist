package student_player;

import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

import static boardgame.Board.DRAW;
/*
This is Node Class of Monte Carlo search tree with UCT
Each node stores:
                1.score: the score used to calculate Q value using UCT. Each win+=2, draw+=1,lose+=0;
                2.times: Number of times the algorithm runs through this node, each run times+=2;
                3.codedmove: An int represented the PentagoMove object
                4.playerid:
                5.children:An arraylist of MCTnode which is the next step
                6.parent
 */

public class MCTnode {
    private static final ThreadLocalRandom rand = ThreadLocalRandom.current();
    private int score;
    private int times;
    private final int codedmove;
    private final int player_id;
    private List<MCTnode> children = new ArrayList<>();
    private final MCTnode parent;

    public MCTnode(int codedmove, MCTnode parent) {
        this.codedmove = codedmove;
        this.parent = parent;
        this.score = 0;
        this.times = 0;
        this.player_id = codedmove / 10000;
    }

    //Once we get the result, we feed back the parents of the node, update times and score of the nodes above the result.
    void feedback(int winner_id) {
        MCTnode thisnode = this;
        while (thisnode != null) {
            thisnode.times += 2;
            if (winner_id == DRAW) {
                thisnode.score++;
            } else if (player_id == winner_id) {
                thisnode.score += 2;
            }
            thisnode = thisnode.parent;
        }
    }

    //UCT Q value calculation function
    Float getQvalue() {
        //use float to save memory
        if (this.times == 0) {
            return Float.MAX_VALUE;
        }
        return (float) ((this.score * 1.0 / (this.times * 1.0)) + Math.sqrt(2) * Math.sqrt(Math.log(this.parent.times) / this.times));
    }

    //get one of the children with maximum q value
    MCTnode getMaxQChild() {
        float max = -1;
        MCTnode temp = null;
        for (MCTnode i : this.children) {
            if (i.getQvalue() > max) {
                max = i.getQvalue();
                temp = i;
            }
        }
        return temp;
    }

    //add list of moves to the node as children
    void addchildren(List<Integer> codedmoves) {
        children = new ArrayList<>(codedmoves.size());
        for (int i : codedmoves) {
            children.add(new MCTnode(i, this));
        }

    }

    //Get the state of result of excution of result.
    PentagoBoardState getstate(PentagoBoardState start) {
        if (this.codedmove == -1) return start;
        Stack<Integer> movestack = new Stack<>();
        MCTnode thisnode = this;
        while (thisnode != null && thisnode.codedmove != -1) {
            movestack.push(thisnode.codedmove);
            thisnode = thisnode.parent;
            //System.out.print(MoveCoder.DeCodeMove(thisnode.codedmove).toPrettyString());
            //System.out.print("\n");
        }
        PentagoBoardState end = (PentagoBoardState) start.clone();
        while (!movestack.isEmpty()) {
            PentagoMove toprint = MoveCoder.DeCodeMove(movestack.pop());
            //System.out.print(toprint.toPrettyString());
            //System.out.print("hiiiii\n");
            end.processMove(toprint);
        }
        return end;
    }

    List<MCTnode> getChildren() {
        return children;
    }

    public int gettimes() {
        return this.times;
    }

    int getmove() {
        return this.codedmove;
    }

    float getWinRate() {
        if (times == 0) {
            return 0;
        }
        return (float) (score * 1.0 / times);
    }

    public boolean haschildren() {
        return !(this.children == null || this.children.size() == 0);
    }

    public int getplayer() {
        return this.player_id;
    }

    public MCTnode getRandomChild() {
        return children.get(rand.nextInt(children.size()));
    }
}
