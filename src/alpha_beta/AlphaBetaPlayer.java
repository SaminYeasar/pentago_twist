package alpha_beta;

import boardgame.Move;
import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;
import pentago_twist.PentagoPlayer;

import java.util.ArrayList;

/**
 * @author mgrenander
 */
public class AlphaBetaPlayer extends PentagoPlayer {
    public AlphaBetaPlayer() { super("AlphaBetaPlayer"); }
    public AlphaBetaPlayer(String name) { super(name); }
    private static final int depth = 3;

    @Override
    public Move chooseMove(PentagoBoardState pbs) {
        if (pbs.getTurnNumber() == 0) { return pbs.getRandomMove(); } // Branching factor is too large at beginning

        Node bestMove = alphaBeta(pbs, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, player_id);
        return bestMove.getMove();
    }

    private Node alphaBeta(PentagoBoardState pbs, int depth, int alpha, int beta, int playerId) {
        if (depth == 0) { return Evaluate.getBestMove(pbs, playerId); } // We are finished recursing

        ArrayList<Node> potentialMoves = Evaluate.getBestMoves(pbs, playerId);
        if (potentialMoves.isEmpty()) { return Evaluate.getBestMove(pbs, playerId); }

        Node bestMove = null;
        if (playerId == this.player_id) { // Maximizing case
            int value = Integer.MIN_VALUE;
            for (Node potentialMove: potentialMoves) {
                PentagoBoardState pbsClone = (PentagoBoardState) pbs.clone();
                pbsClone.processMove(potentialMove.getMove());

                Node recursedMove = alphaBeta(pbsClone, depth-1, alpha, beta, 1 - playerId);
                int rValue = recursedMove.getMoveValue();
                if (rValue > value) {
                    value = rValue;
                    bestMove = potentialMove;
                }

                alpha = Math.max(value, alpha); // Update alpha
                if (alpha >= beta) { break; } // Can prune rest of branches
            }
        } else { // Minimizing case
            int value = Integer.MAX_VALUE;
            for (Node potentialMove: potentialMoves) {
                PentagoBoardState pbsClone = (PentagoBoardState) pbs.clone();
                pbsClone.processMove(potentialMove.getMove());

                Node recursedMove = alphaBeta(pbsClone, depth-1, alpha, beta, 1 - playerId);
                int rValue = recursedMove.getMoveValue();
                if (rValue < value) {
                    value = rValue;
                    bestMove = potentialMove;
                }

                beta = Math.min(value, beta); // Update alpha
                if (alpha >= beta) { break; } // Can prune rest of branches
            }
        }
        return bestMove != null ? bestMove : new Node((PentagoMove) pbs.getRandomMove(), 1);
    }
}
