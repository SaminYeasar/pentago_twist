package pentago_twist;

import boardgame.Board;
import boardgame.BoardPanel;
import boardgame.BoardState;
import boardgame.Move;

/**
 * @author mgrenander
 */
public class PentagoBoard extends Board {
    private PentagoBoardState boardState;

    public PentagoBoard() {
        super();
        boardState = new PentagoBoardState();
    }

    @Override
    public int getWinner() {
        return boardState.getWinner(); }

    @Override
    public void forceWinner(int win) {
        boardState.setWinner(win); }

    @Override
    public int getTurnPlayer() {
        return boardState.getTurnPlayer(); }

    @Override
    public int getTurnNumber() {
        return boardState.getTurnNumber(); }

    @Override
    public void move(Move m) throws IllegalArgumentException {
        boardState.processMove((PentagoMove) m); }

    @Override
    public BoardState getBoardState() {
        return boardState; }

    @Override
    public BoardPanel createBoardPanel() {
        return new PentagoBoardPanel(); }

    @Override
    public String getNameForID(int p) {
        return String.format("Player-%d", p); }

    @Override
    public int getIDForName(String s) {
        return Integer.valueOf(s.split("-")[1]); }

    @Override
    public int getNumberOfPlayers() {
        return 2; }

    @Override
    public Move parseMove(String str) throws IllegalArgumentException {
        return new PentagoMove(str);
    }

    @Override
    public Object clone() {
        PentagoBoard board = new PentagoBoard();
        board.boardState = (PentagoBoardState) boardState.clone();
        return board;
    }

    @Override
    public Move getRandomMove() { return boardState.getRandomMove(); }
}
