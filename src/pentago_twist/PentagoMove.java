package pentago_twist;

import boardgame.Move;

/**
 * @author mgrenander
 */
public class PentagoMove extends Move {
    private int playerId;
    private int xMove;
    private int yMove;
    private int aSwap;
    private int bSwap;
    private boolean fromBoard;

    public PentagoMove(PentagoCoord coord, int aSwap, int bSwap, int playerId) {
        this(coord.getX(), coord.getY(), aSwap, bSwap, playerId);
    }

    public PentagoMove(int x, int y, int aSwap, int bSwap, int playerId) {
        this.playerId = playerId;
        this.xMove = x;
        this.yMove = y;
        this.aSwap = aSwap;
        this.bSwap = bSwap;
        this.fromBoard = false;
    }

    public PentagoMove(String formatString) {
        String[] components = formatString.split(" ");
        try {
            this.xMove = Integer.parseInt(components[0]);
            this.yMove = Integer.parseInt(components[1]);
            this.aSwap = Integer.parseInt(components[2]);
            this.bSwap = Integer.parseInt(components[3]);
            this.playerId = Integer.parseInt(components[4]);
            this.fromBoard = false;
        } catch(NumberFormatException e) {
            throw new IllegalArgumentException("Received an uninterpretable string format for a TablutMove.");
        }
    }

    // Getters
    public PentagoCoord getMoveCoord() {
        return new PentagoCoord(this.xMove, this.yMove); }
    public int getASwap() {
        return this.aSwap; }
    public int getBSwap() {
        return this.bSwap; }

    // Fetch player's name
    public String getPlayerName(int player) {
        if (playerId != PentagoBoardState.BLACK && playerId != PentagoBoardState.WHITE) {
            return "Illegal";
        }
        return player == PentagoBoardState.WHITE ? "White" : "Black";
    }

    // Fetch the current player name
    public String getPlayerName() {
        return getPlayerName(this.playerId);
    }

    // Server methods
    @Override
    public int getPlayerID() {
        return this.playerId; }

    @Override
    public void setPlayerID(int playerId) {
        this.playerId = playerId; }

    @Override
    public void setFromBoard(boolean fromBoard) {
        this.fromBoard = fromBoard; }

    @Override
    public boolean doLog() {
        return true; }

    @Override
    public String toPrettyString() {

        return String.format("Player %d, Move: (%d, %d), R/F: (%d, %d)", playerId, xMove, yMove, aSwap, bSwap);
        //return String.format("Player %d, Move: (%d, %d), Swap: (%d, %d)", playerId, xMove, yMove, aSwap, bSwap);
    }

    @Override
    public String toTransportable() {
        return String.format("%d %d %d %d %d", xMove, yMove, aSwap, bSwap, playerId);
    }
}
