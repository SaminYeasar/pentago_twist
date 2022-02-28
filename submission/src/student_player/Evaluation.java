package student_player;

import pentago_twist.PentagoBoardState;

/*
Evaluate function:
Since the result of Monte Carlo function is not very ideal, almost every node has child that has 1.0 win rate.
Evaluation function could make the move to be "potentially good":
    1. If there is step that leads to win, select this step
    2. Prefer to putting piece on the edge of each quadrant.
    3. If there are 3 opponent's pieces are connected either vertically, horizontally or diagonally, block it on both head if it's possible.
 */
public class Evaluation {
    public static float eval(PentagoBoardState state, MCTnode node) {
        //initial value is 0
        float value = 0;
        PentagoBoardState.Piece[][] arry;
        arry = state.getBoard();
        int x = node.getmove() / 10 % 10;
        int y = node.getmove() % 10;
        PentagoBoardState.Piece opponent_id;
        PentagoBoardState.Piece player_id;
        //If next move can lead to win, return a hugn number to ensure to follow this step
        if (node.getstate(state).getWinner() == node.getplayer()) {
            return 1000;
        }
        //get the color of opponent's color
        switch (node.getplayer()) {
            case 0:
                opponent_id = PentagoBoardState.Piece.BLACK;
                break;
            case 1:
                opponent_id = PentagoBoardState.Piece.WHITE;
                break;
            default:
                throw new IllegalStateException("Unexpected value: player");
        }

        //Let AI prefer to putting piece on edges of each qudrant
        if (x == 2 || x == 3) {
            value = (float) (value + 0.01);
        }
        if (y == 2 || y == 3) {
            value = (float) (value + 0.01);
        }

       /*
       Check if there are 3 opponent's piece connect either on vertical, horizontal or diagonal direction.
       If situation detected, block it first.
        */
        //horzontal check
        int count = 0;
        for (int i = 0; i < 6; i++) {
            if (arry[x][i] == opponent_id)
                count++;
            else
                count = 0;

            if (count >= 3)
                return (float) 0.05;
        }

        //Vertical check
        for (int i = 0; i < 6; i++) {
            if (arry[i][y] == opponent_id)
                count++;
            else
                count = 0;

            if (count >= 3)
                return (float) 0.05;
        }

        // diagonal top-left to bottom-right check
        int maxx = -1;
        int maxy = -1;
        int maxcount = -1;
        for (int rowStart = 0; rowStart < 4; rowStart++) {
            count = 0;
            maxx = -1;
            maxy = -1;
            int row, col;
            for (row = rowStart, col = 0; row < 6 && col < 6; row++, col++) {
                if (arry[row][col] == opponent_id) {
                    count++;
                    if (count > maxcount) {
                        maxcount = count;
                        maxx = row;
                        maxy = col;
                    }
                    if ((count == 3) && (x == row - 3) && (y == col - 3)) return (float) 0.05;

                } else {
                    count = 0;
                }
            }
            if ((maxcount >= 3) && (x == maxx + 1) && (y == maxy + 1)) return (float) 0.05;
        }

        // diagonal top-left to bottom-right check
        for (int colStart = 1; colStart < 4; colStart++) {
            count = 0;
            maxx = -1;
            maxy = -1;
            maxcount = -1;
            int row, col;
            for (row = 0, col = colStart; row < 6 && col < 6; row++, col++) {
                if (arry[row][col] == opponent_id) {
                    count++;
                    if (count > maxcount) {
                        maxcount = count;
                        maxx = row;
                        maxy = col;
                    }
                    if ((count == 3) && (x == row - 3) && (y == col - 3)) return (float) 0.05;

                } else {
                    count = 0;
                }
            }
            if ((count >= 3) && (x == maxx + 1) && (y == maxy + 1)) return (float) 0.05;
        }

        // diagonal top-left to bottom-right check
        for (int rowStart = 0; rowStart < 4; rowStart++) {
            maxx = -1;
            maxy = -1;
            maxcount = -1;
            count = 0;
            int row, col;
            for (row = rowStart, col = 5; row < 6 && col >= 0; row++, col--) {
                if (arry[row][col] == opponent_id) {
                    count++;
                    if (count > maxcount) {
                        maxcount = count;
                        maxx = row;
                        maxy = col;
                    }
                    if ((count == 3) && (x == row - 3) && (y == col + 3)) return (float) 0.05;

                } else {
                    count = 0;
                }
            }
            if ((maxcount >= 3) && (x == maxx + 1) && (y == maxy - 1)) return (float) 0.05;
        }

        // diagonal top-left to bottom-right check
        for (int colStart = 4; colStart > 0; colStart--) {
            count = 0;
            maxx = -1;
            maxy = -1;
            maxcount = -1;
            int row, col;
            for (row = 0, col = colStart; row < 6 && col >= 0; row++, col--) {
                if (arry[row][col] == opponent_id) {
                    count++;
                    if (count > maxcount) {
                        maxcount = count;
                        maxx = row;
                        maxy = col;
                    }
                    if ((count == 3) && (x == row - 3) && (y == col + 3)) return (float) 0.05;

                } else {
                    count = 0;
                }
            }
            if ((count >= 3) && (x == maxx + 1) && (y == maxy - 1)) return (float) 0.05;
        }
        return value;
    }
}
