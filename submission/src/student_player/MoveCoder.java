package student_player;

import pentago_twist.PentagoMove;

import java.util.ArrayList;
import java.util.List;
/*
Since there will be thousands MCTnode saved in memory, and every node will need to have move information.
MoveCoder could code and decode PantagoMove object to and from a int to save the memory cost of MCTree
    Int structure: D for PlayerID, A for aswap, B for Bsawp, X for X coordinate, Y for Y coordinates
    PantagoMve(D,X,Y,A,B) <==> DABXY
 */
public class MoveCoder {
    public static int CodeMove(PentagoMove amove) {
        //convert xMove, yMove, aswap, bswap, playerID to PABXY
        int ret = amove.getPlayerID() * 10000 + amove.getASwap() * 1000 + amove.getBSwap() * 100 + amove.getMoveCoord().getX() * 10 + amove.getMoveCoord().getY();
        //System.out.print(amove.toPrettyString());
        //System.out.print("\n");
        //System.out.print(ret);
        //System.out.print(":\n");
        //System.out.print(DeCodeMove(ret).toPrettyString());
        return ret;
    }

    //decode the move from int to move object
    public static PentagoMove DeCodeMove(int intmove) {
        int playerID = intmove / 10000;
        int aswap = intmove / 1000 % 10;
        int bswap = intmove / 100 % 10;
        int x = intmove / 10 % 10;
        int y = intmove % 10;
        //PentagoMove hi=new PentagoMove(x,y,aswap,bswap,playerID);
        //System.out.print(hi.toPrettyString());
        //System.out.print("\n");
        return new PentagoMove(x, y, aswap, bswap, playerID);
    }

    public static List<Integer> CodeMoveArrayList(ArrayList<PentagoMove> amoves) {
        //System.out.print("hiiiiiiiiiii");
        List<Integer> list = new ArrayList<>();
        for (PentagoMove i : amoves) {
            list.add(CodeMove(i));
            //System.out.print(CodeMove(i));
            //System.out.print("\n");
        }
        return list;
    }

    public static ArrayList<PentagoMove> DeCodeMoveList(List<Integer> intmoves) {
        //System.out.print("heyyyyyyyyyy");
        ArrayList<PentagoMove> arrylist = new ArrayList<>();
        for (int i : intmoves) {
            arrylist.add(DeCodeMove(i));
        }
        return arrylist;
    }

}