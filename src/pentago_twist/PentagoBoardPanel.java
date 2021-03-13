package pentago_twist;

import pentago_twist.PentagoBoardState.Piece;

import java.awt.event.ComponentEvent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import boardgame.BoardPanel;
import java.util.ArrayList;

/**
 * @author mgrenander
 */
public class PentagoBoardPanel extends BoardPanel implements MouseListener, MouseMotionListener, ComponentListener {
    private static final Color BACKGROUND_COLOR = Color.GRAY;
    private static final Color LINE_COLOR = Color.BLACK;
    private static final Color HIGHLIGHT_COLOR = new Color(204, 255, 0, 113);
    private static final Color BOARD_COLOR2 = new Color(245, 222, 179); // a subtle "wheat" color for the board...
    private static final Color BOARD_COLOR1 = new Color(244, 164, 96); // complemented with a tasteful "sandybrown".
    private static final Color WHITE_COL = Color.WHITE;
    private static final Color BLACK_COL = Color.BLACK;

    private static final int BOARD_DIM = PentagoBoardState.BOARD_SIZE;
    private static final int PIECE_SIZE = 75;
    private static final int SQUARE_SIZE = (int) (PIECE_SIZE * 1.25); // Squares 25% bigger than pieces.

    final class GUIPiece {
        private Piece pieceType;
        int xPos;
        int yPos;
        PentagoCoord coord;

        // Construct a piece!
        GUIPiece(Piece pieceType, int xPos, int yPos, PentagoCoord coord) {
            this.pieceType = pieceType;
            this.xPos = xPos;
            this.yPos = yPos;
            this.coord = coord;
        }

        void draw(Graphics g) {
            draw(g, xPos, yPos);
        }

        void draw(Graphics g, int cx, int cy) {
            int x = cx - PIECE_SIZE / 2;
            int y = cy - PIECE_SIZE / 2;

            g.setColor(pieceType == Piece.BLACK ? BLACK_COL : WHITE_COL);

            // Paint piece.
            g.fillOval(x, y, PIECE_SIZE, PIECE_SIZE);
            if (pieceType != Piece.BLACK) {// draw a border around whites
                g.setColor(Color.BLACK);
                g.drawOval(x, y, PIECE_SIZE, PIECE_SIZE);
            }
        }
    }

    // Stores all board pieces.
    private ArrayList<GUIPiece> boardPieces;
    private BoardPanelListener listener;
    private boolean isPieceSelected;
    private PentagoCoord pieceSelection;
    private boolean isQuadSelected;
    private Integer quadSelection;

    // Constructing with this as the listener for everything.
    PentagoBoardPanel() {
        addMouseListener(this);
        addMouseMotionListener(this);
        addComponentListener(this);

        isPieceSelected = false;
        isQuadSelected = false;
    }

    // Overriding BoardPanel methods to help with listener functionality.
    @Override
    protected void requestMove(BoardPanelListener l) {
        listener = l;
        System.out.println("REQUESTED.");
    }

    @Override
    protected void cancelMoveRequest() {
        listener = null;
    }

    // Drawing a board.
    @Override
    public void drawBoard(Graphics g) {
        super.drawBoard(g); // Paints background and other
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //Makes pretty

        //Paint board
        for (int i = 0; i < PentagoBoardState.BOARD_SIZE; i++) {
            for (int j = 0; j < PentagoBoardState.BOARD_SIZE; j++) {
                Color currColor = (i + j) % 2 == 0 ? BOARD_COLOR1 : BOARD_COLOR2;
                g2.setColor(currColor);
                g2.fillRect(i*SQUARE_SIZE, j*SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
        g2.setStroke(new BasicStroke(3));
        g2.setColor(LINE_COLOR);

        int endPos = BOARD_DIM * SQUARE_SIZE;
        g2.drawLine(0, 0, endPos, 0);
        g2.drawLine(0,0, 0, endPos);
        g2.drawLine(0, endPos, endPos, endPos);
        g2.drawLine(endPos, 0, endPos, endPos);

        int midPos = endPos / 2;
        g2.drawLine(0, midPos, endPos, midPos);
        g2.drawLine(midPos, 0, midPos, endPos);
        g2.setStroke(new BasicStroke(1));

        boardPieces = new ArrayList<>();
        updateBoardPieces();
        for (GUIPiece gp : boardPieces) {
            gp.draw(g2);
        }

        if (isQuadSelected) {
            g2.setColor(HIGHLIGHT_COLOR);
            switch (quadSelection){
                case 0:
                    g2.fillRect(0, 0, midPos, midPos);
                    break;
                case 1:
                    g2.fillRect(midPos, 0, midPos, midPos);
                    break;
                case 2:
                    g2.fillRect(0, midPos, midPos, midPos);
                    break;
                case 3:
                    g2.fillRect(midPos, midPos, midPos, midPos);
                    break;
                default:
                    throw new IllegalStateException("Unknown error when repainting quad selection");
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    // A bit sketchy, but has to be done to highlight possible moves for human.
    private void humanRepaint() {
        bufferDirty = true;
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (listener == null) { return; }

        if (!isPieceSelected) { // Player wants to click on a piece
            processPlacePiece(e);
        } else if (!isQuadSelected) { // Player wants to click on a quadrant
            processQuadClick(e);
        } else { // The second quandrant was pressed
            completeMove(e);
        }
    }

    private void resetSelection() {
        isPieceSelected = false;
        isQuadSelected = false;
        quadSelection = null;
        pieceSelection = null;
    }

    private void processPlacePiece(MouseEvent e) {
        int clickX = e.getX();
        int clickY = e.getY();

        // Check if we clicked on an occupied square. If so this is not a real move
        for (GUIPiece gp : boardPieces) {
            if (clickInSquare(clickX, clickY, gp.xPos, gp.yPos)) {
                return;
            }
        }
        PentagoBoardState pbs = (PentagoBoardState) getCurrentBoard().getBoardState();
        PentagoCoord dest = null;
        outer:for (int i = 0; i < PentagoBoardState.BOARD_SIZE; i++) {
            for (int j = 0; j < PentagoBoardState.BOARD_SIZE; j++) {
                if (pbs.getPieceAt(i, j) == Piece.EMPTY) {
                    int xPos = j * SQUARE_SIZE + SQUARE_SIZE / 2;
                    int yPos = i * SQUARE_SIZE + SQUARE_SIZE / 2;
                    if(clickInSquare(clickX, clickY, xPos, yPos)) {
                        dest = new PentagoCoord(i, j);
                        break outer;
                    }
                }
            }
        }
        if (dest == null) { return; }
        if (pbs.isPlaceLegal(dest)) {
            isPieceSelected = true;
            pieceSelection = new PentagoCoord(dest.getX(), dest.getY());
            pbs.getBoard()[dest.getX()][dest.getY()] = pbs.getTurnPlayer() == PentagoBoardState.WHITE ? Piece.WHITE : Piece.BLACK;
            humanRepaint();
            System.out.println("PIECE PLACED");
        }
    }

    private void processQuadClick(MouseEvent e) {
        quadSelection = findQuadSelection(e);
        if (quadSelection == null) { return; }
        isQuadSelected = true;
        humanRepaint();
        System.out.println("QUAD SELECTED");
    }

    private void completeMove(MouseEvent e) {
        Integer secondQuad = findQuadSelection(e);
        if (secondQuad == null) { return; }
        PentagoBoardState pbs = (PentagoBoardState) getCurrentBoard().getBoardState();
        PentagoMove move = new PentagoMove(pieceSelection, quadSelection, secondQuad, pbs.getTurnPlayer());
        listener.moveEntered(move);
        cancelMoveRequest();
        resetSelection(); // Reset the selection variables
        System.out.println("MOVE COMPLETED");
    }

    private Integer findQuadSelection(MouseEvent e) {
        int clickX = e.getX();
        int clickY = e.getY();
        for (int i = 0; i < PentagoBoardState.BOARD_SIZE; i++) {
            for (int j = 0; j < PentagoBoardState.BOARD_SIZE; j++) {
                int xPos = j * SQUARE_SIZE + SQUARE_SIZE / 2;
                int yPos = i * SQUARE_SIZE + SQUARE_SIZE / 2;
                if(clickInSquare(clickX, clickY, xPos, yPos)) {
                    if (i < 3 && j < 3) { return 0; }
                    else if (i < 3 && j >= 3) { return 1; }
                    else if (j < 3) { return 2; }
                    else { return 3; }
                }
            }
        }
        return null; // Was not a valid quad selection
    }

    private void updateBoardPieces() {
        PentagoBoardState pbs = (PentagoBoardState) getCurrentBoard().getBoardState();
        boardPieces = new ArrayList<>();
        for (int i = 0; i < PentagoBoardState.BOARD_SIZE; i++) {
            for (int j = 0; j < PentagoBoardState.BOARD_SIZE; j++) {
                Piece p = pbs.getPieceAt(i, j);
                if (p != Piece.EMPTY) {
                    int xPos = j * SQUARE_SIZE + SQUARE_SIZE / 2;
                    int yPos = i * SQUARE_SIZE + SQUARE_SIZE / 2;
                    GUIPiece gp = new GUIPiece(p, xPos, yPos, new PentagoCoord(i, j));
                    boardPieces.add(gp);
                }
            }
        }
    }

    // Helpers.
    @Override
    public Color getBackground() {
        return BACKGROUND_COLOR;
    }

    private static boolean clickInSquare(int x, int y, int cx, int cy) {
        return Math.abs(x - cx) < SQUARE_SIZE / 2 && Math.abs(y - cy) < SQUARE_SIZE / 2;
    }

    /* Don't use these interface methods */
    public void mouseDragged(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void componentResized(ComponentEvent arg0) {
    }

    public void mouseClicked(MouseEvent arg0) {
    }

    public void mouseEntered(MouseEvent arg0) {
    }

    public void mouseExited(MouseEvent arg0) {
    }

    public void mouseMoved(MouseEvent arg0) {
    }

    public void componentMoved(ComponentEvent arg0) {
    }

    public void componentShown(ComponentEvent arg0) {
    }

    public void componentHidden(ComponentEvent arg0) {
    }
}
