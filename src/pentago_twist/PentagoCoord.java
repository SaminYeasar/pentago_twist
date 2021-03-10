package pentago_twist;

/**
 * Simple class for holding a (x,y)-coordinate.
 * @author mgrenander
 */
public class PentagoCoord {
    private int x;
    private int y;

    public PentagoCoord(int x, int y) throws IllegalArgumentException {
        if (!isValidCoord(x, y)) {
            throw new IllegalArgumentException("Invalid Coordinates: (" + x + ", " + y + ")");
        }

        this.x = x;
        this.y = y;
    }

    public int getX() { return this.x; }
    public int getY() { return this.y; }

    private static boolean isValidCoord(int x, int y) {
        return x < PentagoBoardState.BOARD_SIZE && y < PentagoBoardState.BOARD_SIZE && x >= 0 && y >= 0;
    }
}
