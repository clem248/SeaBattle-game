import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class BotShooting {
    private final Cell[][] board;
    private final Random random;
    private final Set<String> shotCoordinates;
    private int[] lastHit;
    private boolean isDestructing;

    public BotShooting(Cell[][] board) {
        this.board = board;
        this.random = new Random();
        this.shotCoordinates = new HashSet<>();
        this.isDestructing = false;
    }

    public boolean makeMove() {
        int[] move;
        int moveState;

        if (isDestructing) {
            move = getNextDestructMove();
            moveState = board[move[0]][move[1]].getState();
        } else {
            do {
                move = getRandomMove();
                moveState = board[move[0]][move[1]].getState();

            } while (shotCoordinates.contains(moveToString(move)));
        }

        int row = move[0];
        int col = move[1];

        shotCoordinates.add(moveToString(move));

        if (moveState == Model.SHIP) {
            System.out.println("Hit! Shot was made by those coordinates: (" + row + ", " + col + ")");
            lastHit = move;
            board[row][col].setState(Model.SUNK);
            markSurroundingCellsAsMiss(row,col);
            isDestructing = true;
        } else {
            System.out.println("Miss! Shot was made by those coordinates: (" + row + ", " + col + ")");
            board[row][col].setState(Model.MISS);
            isDestructing = false;
        }
        return isDestructing;
    }

    private int[] getNextDestructMove() {
        int row = lastHit[0];
        int col = lastHit[1];

        // Лево, Право, Вверх, Вниз
        if (isValidMove(row, col - 1) && board[row][col - 1].getState() == Model.SHIP) {
            return new int[]{row, col - 1};
        } else if (isValidMove(row, col + 1) && board[row][col + 1].getState() ==  Model.SHIP) {
            return new int[]{row, col + 1};
        } else if (isValidMove(row - 1, col) && board[row - 1][col].getState() ==  Model.SHIP) {
            return new int[]{row - 1, col};
        } else if (isValidMove(row + 1, col) && board[row + 1][col].getState() ==  Model.SHIP) {
            return new int[]{row + 1, col};
        } else {
            isDestructing = false;
            lastHit = null;
            return getRandomMove();
        }
    }

    private void markSurroundingCellsAsMiss(int row, int col) {
        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1},            {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (isValidMove(newRow, newCol) && board[newRow][newCol].getState() == Model.HIDE) {
                board[newRow][newCol].setState((byte)4);
                shotCoordinates.add(moveToString(new int[]{newRow, newCol}));
            }
        }
    }

    private boolean isValidMove(int row, int col) {
        return row >= 0 && row < board.length && col >= 0 && col < board[row].length
                && !shotCoordinates.contains(moveToString(new int[]{row, col}));
    }

    private int[] getRandomMove() {
        int row = random.nextInt(board.length);
        int col = random.nextInt(board[row].length);
        return new int[]{row, col};
    }

    private String moveToString(int[] move) {
        return move[0] + "," + move[1];
    }

    public boolean hasRemainingShips() {
        for (Cell[] row : board) {
            for (Cell cell : row) {
                if (cell.getState() == Model.SHIP) {
                    return true;
                }
            }
        }
        return false;
    }


    /*  FOR FASTER TESTING ONLY  */

    // public static void main(String[] args) {
    //     // Assuming you have a Model class and a Cell class defined appropriately
    //     int rows = 10;
    //     int cols = 10;
    
    //     Cell[][] board = new Cell[rows][cols];
    
    //     // Initialize the board with empty cells
    //     for (int i = 0; i < rows; i++) {
    //         for (int j = 0; j < cols; j++) {
    //             board[i][j] = new Cell(Model.HIDE);
    //         }
    //     }
    
    //     // Place ships based on the provided "computer" array
    //     byte[][] computer = {
    //             {2, 0, 0, 0, 0, 0, 0, 2, 0, 0},
    //             {0, 0, 0, 0, 0, 0, 0, 2, 0, 0},
    //             {0, 0, 2, 0, 0, 0, 0, 2, 0, 2},
    //             {0, 0, 0, 0, 0, 0, 0, 0, 0, 2},
    //             {0, 0, 0, 0, 0, 0, 0, 2, 0, 2},
    //             {0, 0, 2, 0, 0, 0, 0, 2, 0, 0},
    //             {0, 0, 0, 0, 0, 0, 0, 0, 0, 2},
    //             {0, 0, 0, 0, 0, 0, 0, 2, 0, 2},
    //             {0, 0, 0, 0, 0, 2, 0, 2, 0, 2},
    //             {2, 0, 0, 0, 0, 2, 0, 0, 0, 2}
    //     };
    
    //     for (int i = 0; i < rows; i++) {
    //         for (int j = 0; j < cols; j++) {
    //             if (computer[i][j] == 2) {
    //                 board[i][j].setState(Model.SHIP);
    //             }
    //         }
    //     }
    
    //     // Create an instance of BotShooting
    //     BotShooting botShooting = new BotShooting(board);
    
    //     // Keep making moves until there are no remaining ships
    //     int moveCounter = 0;
    //     while (botShooting.hasRemainingShips()) {
    //         botShooting.makeMove();
    //         moveCounter++;
    //     }
    
    //     System.out.println("Game over! Total moves: " + moveCounter);
    // }
}
