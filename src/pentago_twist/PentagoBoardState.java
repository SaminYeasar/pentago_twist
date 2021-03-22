package pentago_twist;
import boardgame.Board;
import boardgame.BoardState;
import boardgame.Move;

import java.util.ArrayList;
import java.util.function.UnaryOperator;
import java.util.Random;

import java.util.Scanner;

/**
 *
 * Note: First player white, second player black!!
 * @author mgrenander
 */
public class PentagoBoardState extends BoardState {
    public static final int BOARD_SIZE = 6;
    private static final int QUAD_SIZE = 3;
    private static final int NUM_QUADS = 4;
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    public static final int MAX_TURNS = 18;
    private static final int ILLEGAL = -1;
    public enum Piece {
        BLACK, WHITE, EMPTY;

        public String toString() {
            return this == EMPTY ? " " : String.valueOf(name().charAt(0)).toLowerCase();
        }
    }

    private static final UnaryOperator<PentagoCoord> getNextHorizontal = c -> new PentagoCoord(c.getX(), c.getY()+1);
    private static final UnaryOperator<PentagoCoord> getNextVertical = c -> new PentagoCoord(c.getX()+1, c.getY());
    private static final UnaryOperator<PentagoCoord> getNextDiagRight = c -> new PentagoCoord(c.getX()+1, c.getY()+1);
    private static final UnaryOperator<PentagoCoord> getNextDiagLeft = c -> new PentagoCoord(c.getX()+1, c.getY()-1);
    private static int FIRST_PLAYER = 0;

    private Piece[][] board;
    private Piece[][][] quadrants;
    private int turnPlayer;
    private int turnNumber;
    private int winner;
    private Random rand;

    PentagoBoardState() {
        super();
        this.board = new Piece[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                this.board[i][j] = Piece.EMPTY;
            }
        }
        this.quadrants = new Piece[NUM_QUADS][QUAD_SIZE][QUAD_SIZE];
        for (int i = 0; i < NUM_QUADS; i++) {
            for (int j = 0; j < QUAD_SIZE; j++) {
                for (int k = 0; k < QUAD_SIZE; k++) {
                    this.quadrants[i][j][k] = Piece.EMPTY;
                }
            }
        }

        rand = new Random(2019);
        winner = Board.NOBODY;
        turnPlayer = FIRST_PLAYER;
        turnNumber = 0;
    }

    // For cloning
    private PentagoBoardState(PentagoBoardState pbs) {
        super();
        this.board = new Piece[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.arraycopy(pbs.board[i], 0, this.board[i], 0, BOARD_SIZE);
        }
        this.quadrants = new Piece[NUM_QUADS][QUAD_SIZE][QUAD_SIZE];
        for (int i = 0; i < NUM_QUADS; i++) {
            for (int j = 0; j < QUAD_SIZE; j++) {
                System.arraycopy(pbs.quadrants[i][j], 0, this.quadrants[i][j], 0, QUAD_SIZE);
            }
        }

        rand = new Random(2019);
        this.winner = pbs.winner;
        this.turnPlayer = pbs.turnPlayer;
        this.turnNumber = pbs.turnNumber;
    }

    public Piece[][] getBoard() { return this.board; }

    @Override
    public Object clone() {
        return new PentagoBoardState(this);
    }

    @Override
    public int getWinner() { return winner; }

    @Override
    public void setWinner(int win) { winner = win; }

    @Override
    public int getTurnPlayer() { return turnPlayer; }

    @Override
    public int getTurnNumber() { return turnNumber; }

    @Override
    public boolean isInitialized() { return board != null; }

    @Override
    public int firstPlayer() { return FIRST_PLAYER; }

    @Override
    public Move getRandomMove() {
        ArrayList<PentagoMove> moves = getAllLegalMoves();
        return moves.get(rand.nextInt(moves.size()));
    }

    public Piece getPieceAt(int xPos, int yPos) {
        if (xPos < 0 || xPos >= BOARD_SIZE || yPos < 0 || yPos >= BOARD_SIZE) {
            throw new IllegalArgumentException("Out of range");
        }
        return board[xPos][yPos];
    }

    public Piece getPieceAt(PentagoCoord coord) {
        return getPieceAt(coord.getX(), coord.getY());
    }

    public ArrayList<PentagoMove> getAllLegalMoves() {
        ArrayList<PentagoMove> legalMoves = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) { //Iterate through positions on board
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == Piece.EMPTY) {
//                    for (int k = 0; k < NUM_QUADS - 1; k++) { // Iterate through valid swaps
//                        for (int l = k+1; l < NUM_QUADS; l++) {
//                            legalMoves.add(new PentagoMove(i, j, k, l, turnPlayer));
//                        }
//                    }
                    for (int k = 0; k < NUM_QUADS; k++) { // Iterate through valid moves for rotate/flip
                        for (int l = 0; l < 2; l++) {
                            legalMoves.add(new PentagoMove(i, j, k, l, turnPlayer));
                        }
                    }
                }
            }
        }
        return legalMoves;
    }

    public boolean isLegal(PentagoMove m) {
        // for swap
        //if (m.getASwap() < 0 || m.getASwap() >= NUM_QUADS || m.getBSwap() < 0 || m.getBSwap() >= NUM_QUADS) { return false; }
        //if (m.getASwap() == m.getBSwap()) { return false; } // Cannot swap same tile

        // update for rotate90 and flip
        if (m.getASwap() < 0 || m.getASwap() >= NUM_QUADS || m.getBSwap() < 0 || m.getBSwap() >= 2) { return false; }

        PentagoCoord c = m.getMoveCoord();
        if (c.getX() >= BOARD_SIZE || c.getX() < 0 || c.getY() < 0 || c.getY() >= BOARD_SIZE) { return false; }
        if (turnPlayer != m.getPlayerID() || m.getPlayerID() == ILLEGAL) { return false; } //Check right player
        return board[c.getX()][c.getY()] == Piece.EMPTY;
    }

    /**
     * Check if placing a piece here is legal, without regards to the swap or player ID
     * @param c
     * @return
     */
    public boolean isPlaceLegal(PentagoCoord c) {
        if (c.getX() >= BOARD_SIZE || c.getX() < 0 || c.getY() < 0 || c.getY() >= BOARD_SIZE) { return false; }
        return board[c.getX()][c.getY()] == Piece.EMPTY;
    }

    public void processMove(PentagoMove m) throws IllegalArgumentException {
        if (!isLegal(m)) { throw new IllegalArgumentException("Invalid move. Move: " + m.toPrettyString()); }
        updateQuadrants(m);
        updateWinner();
        if (turnPlayer != FIRST_PLAYER) { turnNumber += 1; } // Update the turn number if needed
        turnPlayer = 1 - turnPlayer; // Swap player
    }

    /**
     * Updates the appropriate quandrant based on the location of the move m
     * @param m: Pentago move
     */
    private void updateQuadrants(PentagoMove m) {
        Piece turnPiece = turnPlayer == WHITE ? Piece.WHITE : Piece.BLACK;
        int x = m.getMoveCoord().getX();
        int y = m.getMoveCoord().getY();
        boolean isLeftQuadMove = y / 3 == 0;
        boolean isTopQuadMove = x / 3 == 0;
        if (isLeftQuadMove && isTopQuadMove) { //Top left quadrant
            quadrants[0][x][y] = turnPiece;
        } else if (!isLeftQuadMove && isTopQuadMove) { //Top right quadrant
            quadrants[1][x][y % QUAD_SIZE] = turnPiece;
        } else if (isLeftQuadMove) { //Bottom left quadrant
            quadrants[2][x % QUAD_SIZE][y] = turnPiece;
        } else { //Bottom right quadrant
            quadrants[3][x % QUAD_SIZE][y % QUAD_SIZE] = turnPiece;
        }

        //Swapping mechanism
        int a = m.getASwap();
        int b = m.getBSwap();
        Piece[][] tmp = quadrants[a];
        //quadrants[a] = quadrants[b];
        //quadrants[b] = tmp;
        Piece[][] tmp2 = new Piece [quadrants[a].length][quadrants[a].length];


        int N = tmp.length;
        // check
//        for (int j = 0; j < N; j++)
//        {
//            for (int i = 0; i <N; i++){
//                System.out.print(tmp[j][i]);
//            }
//        }
        switch (b){
            case 0:
                // rotate 90 right
                //System.out.println("Rotate");
                for (int j = 0; j < N; j++)
                {
                    for (int i = N - 1; i >= 0; i--) {
                        //System.arraycopy(quadrants[a][j],  N - 1 - i, tmp[i], j, 1);
                        System.arraycopy(tmp[i], j, tmp2[j],  N - 1 - i, 1);
                    }
                }
                break;
            case 1:
               // flip a quadrant
                //System.out.println("Flip");
                for (int j = 0; j < N; j++)
                {
                    for (int i = N - 1; i >= 0; i--) {
                        //System.arraycopy(quadrants[a][j],  N - 1 - i, tmp[i], j, 1);
                        System.arraycopy(tmp[j], i, tmp2[j],  N - 1 - i, 1);
                    }
                }
                break;

        }


        quadrants[a] = tmp2;
        buildBoardFromQuadrants();
    }



    private void buildBoardFromQuadrants() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            int quadrantRow = i < 3 ? i : i - 3;
            int leftQuad = i < 3 ? 0 : 2;
            int rightQuad = i < 3 ? 1 : 3;
            System.arraycopy(quadrants[leftQuad][quadrantRow], 0, board[i], 0, 3);
            System.arraycopy(quadrants[rightQuad][quadrantRow], 0, board[i], 3, 3);
        }
    }

    private void updateWinner() {
        boolean playerWin = checkVerticalWin(turnPlayer) || checkHorizontalWin(turnPlayer) || checkDiagRightWin(turnPlayer) || checkDiagLeftWin(turnPlayer);
        int otherPlayer = 1 - turnPlayer;
        boolean otherWin = checkVerticalWin(otherPlayer) || checkHorizontalWin(otherPlayer) || checkDiagRightWin(otherPlayer) || checkDiagLeftWin(otherPlayer);
        if (playerWin) { // Current player has won
            winner = otherWin ? Board.DRAW : turnPlayer;
        } else if (otherWin) { // Player's move caused the opponent to win
            winner = otherPlayer;
        } else if (gameOver()) {
            winner = Board.DRAW;
        }
    }

    @Override
    public boolean gameOver() {
        return ((turnNumber >= MAX_TURNS - 1) && turnPlayer == BLACK) || winner != Board.NOBODY;
    }

    private boolean checkVerticalWin(int player) {
        return checkWinRange(player, 0, 2, 0, BOARD_SIZE, getNextVertical);
    }

    private boolean checkHorizontalWin(int player) {
        return checkWinRange(player, 0, BOARD_SIZE, 0, 2, getNextHorizontal);
    }

    private boolean checkDiagRightWin(int player) {
        return checkWinRange(player, 0, 2, 0, 2, getNextDiagRight);
    }

    private boolean checkDiagLeftWin(int player) {
        return checkWinRange(player, 0 ,2, BOARD_SIZE - 2, BOARD_SIZE, getNextDiagLeft);
    }

    private boolean checkWinRange(int player, int xStart, int xEnd, int yStart, int yEnd, UnaryOperator<PentagoCoord> direction) {
        boolean win = false;
        for (int i = xStart; i < xEnd; i++) {
            for (int j = yStart; j < yEnd; j++) {
                win |= checkWin(player, new PentagoCoord(i, j), direction);
                if (win) { return true; }
            }
        }
        return false;
    }

    private boolean checkWin(int player, PentagoCoord start, UnaryOperator<PentagoCoord> direction) {
        int winCounter = 0;
        Piece currColour = player == 0 ? Piece.WHITE : Piece.BLACK;
        PentagoCoord current = start;
        while(true) {
            try {
                if (currColour == this.board[current.getX()][current.getY()]) {
                    winCounter++;
                    current = direction.apply(current);
                } else {
                    break;
                }
            } catch (IllegalArgumentException e) { //We have run off the board
                break;
            }
        }
        return winCounter >= 5;
    }

    public void printBoard() {
        System.out.println(this.toString());
    }

    @Override
    public String toString() {
        StringBuilder boardString = new StringBuilder();
        String rowMarker = "--------------------------\n";
        boardString.append(rowMarker);
        for (int i = 0; i < BOARD_SIZE; i++) {
            boardString.append("|");
            for (int j = 0; j < BOARD_SIZE; j++) {
                boardString.append(" ");
                boardString.append(board[i][j].toString());
                boardString.append(" |");
                if (j == QUAD_SIZE - 1) {
                    boardString.append("|");
                }
            }
            boardString.append("\n");
            if (i == QUAD_SIZE - 1) {
                boardString.append(rowMarker);
            }
        }
        boardString.append(rowMarker);
        return boardString.toString();
    }

    public static void main(String[] args) {
        PentagoBoardState pbs = new PentagoBoardState();

        Scanner scanner = new Scanner(System.in);
        int id = FIRST_PLAYER;
        while(pbs.winner == Board.NOBODY) {
            System.out.print("Enter move (x y a b): ");
            String moveStr = scanner.nextLine();
            PentagoMove m = new PentagoMove(moveStr + " " + id);
            if (!pbs.isLegal(m)) {
                System.out.println("Invalid move: " + m.toPrettyString());
                continue;
            }
            pbs.processMove(m);
            pbs.printBoard();
            id = 1 - id;
        }

        switch(pbs.winner) {
            case WHITE:
                System.out.println("White wins.");
                break;
            case BLACK:
                System.out.println("Black wins.");
                break;
            case Board.DRAW:
                System.out.println("Draw.");
                break;
            case Board.NOBODY:
                System.out.println("Nobody has won.");
                break;
            default:
                System.out.println("Unknown error.");
        }
    }
}
