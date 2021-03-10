package pentago_twist;

import boardgame.Board;
import boardgame.BoardState;
import boardgame.Move;
import boardgame.Player;

/**
 * @author mgrenander
 */
public abstract class PentagoPlayer extends Player {
    public PentagoPlayer(String name) { super(name); }
    public PentagoPlayer() { super("Player"); }

    @Override
    final public Board createBoard() { return new PentagoBoard(); }

    @Override
    final public Move chooseMove(BoardState boardState) { return chooseMove((PentagoBoardState) boardState); }

    public abstract Move chooseMove(PentagoBoardState boardState);
}
