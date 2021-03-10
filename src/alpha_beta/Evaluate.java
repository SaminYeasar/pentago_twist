package alpha_beta;

import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoBoardState.Piece;
import pentago_twist.PentagoCoord;
import pentago_twist.PentagoMove;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

/**
 * @author mgrenander
 */
public class Evaluate {
    private static final UnaryOperator<PentagoCoord> getNextHorizontal = c -> new PentagoCoord(c.getX(), c.getY()+1);
    private static final UnaryOperator<PentagoCoord> getNextVertical = c -> new PentagoCoord(c.getX()+1, c.getY());
    private static final UnaryOperator<PentagoCoord> getNextDiagRight = c -> new PentagoCoord(c.getX()+1, c.getY()+1);
    private static final UnaryOperator<PentagoCoord> getNextDiagLeft = c -> new PentagoCoord(c.getX()+1, c.getY()-1);
    private static final List<UnaryOperator<PentagoCoord>> directions = Arrays.asList(getNextHorizontal, getNextVertical, getNextDiagRight, getNextDiagLeft);

    public static ArrayList<Node> getBestMoves(PentagoBoardState pbs, int playerId) {
        ArrayList<PentagoMove> moves = pbs.getAllLegalMoves(); // TODO: instead of going through legal moves, only extend existing pieces.
        ArrayList<Node> bestMoves = new ArrayList<>();

        // First find the best move
        Node bestMove = getBestMove(pbs, playerId);
        bestMoves.add(bestMove);
        for (PentagoMove move: moves) { // Find all equivalently good moves
            PentagoBoardState pbsClone = (PentagoBoardState) pbs.clone();
            pbsClone.processMove(move);
            int moveValue = getLongest(pbsClone, playerId);
            if (moveValue >= bestMove.getMoveValue()) {
                bestMoves.add(new Node(move, moveValue));
            }
        }

        return bestMoves;
    }

    public static Node getBestMove(PentagoBoardState pbs, int playerId) {
        ArrayList<PentagoMove> moves = pbs.getAllLegalMoves();
        Node bestMove = new Node((PentagoMove) pbs.getRandomMove(), 1);
        int longest = 0;
        for (PentagoMove move: moves) {
            PentagoBoardState pbsClone = (PentagoBoardState) pbs.clone();
            pbsClone.processMove(move);

            // Count the longest in a row for this move
            int moveValue = getLongest(pbsClone, playerId);
            if (moveValue > longest) {
                bestMove = new Node(move, moveValue);
                longest = moveValue;
            }
        }
        return bestMove;
    }

    /**
     * Find the length of the longest sequence of pieces in a row for this player
     * @param pbs Pentago board state
     * @param playerId The player
     * @return length of longest sequence
     */
    public static int getLongest(PentagoBoardState pbs, int playerId) {
        int longest = 0;
        Piece playerPiece = playerId == PentagoBoardState.WHITE ? Piece.WHITE : Piece.BLACK;
        for (int i = 0; i < PentagoBoardState.BOARD_SIZE; i++) {
            for (int j = 0; j < PentagoBoardState.BOARD_SIZE; j++) {
                if (pbs.getPieceAt(i, j) == playerPiece) {
                    for (UnaryOperator<PentagoCoord> direction: directions) {
                        longest = Math.max(longest, getLongestInDirection(playerPiece, pbs, new PentagoCoord(i, j), direction));
                    }
                }
            }
        }
        return longest;
    }

    private static int getLongestInDirection(Piece player, PentagoBoardState pbs, PentagoCoord start, UnaryOperator<PentagoCoord> direction) {
        PentagoCoord current = start;
        if (pbs.getPieceAt(current) != player) { return 0; }
        int longest = 1;
        while(true) {
            try {
                current = direction.apply(current);
                if (pbs.getPieceAt(current) == player) { longest++; }
                else { break; }
            } catch (IllegalArgumentException e) {
                break;
            }
        }
        return longest;
    }
}
