import java.util.Random;

public class ShipPlacementGenerator {

    public boolean freedom(int x, int y, Cell[][] field) {
        int dx, dy;

        if ((x >= 0) && (x < 10) && (y >= 0) && (y < 10) && (field[x][y].getState() == Model.HIDE)) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    dx = x + i;
                    dy = y + j;
                    if ((dx >= 0) && (dx < 10) && (dy >= 0) && (dy < 10) && (field[dx][dy].getState() == Model.SHIP)) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    // Method for calculating the density of ships in a given area
    private int calculateDensity(int x, int y, Cell[][] field) {
        int density = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int dx = x + i;
                int dy = y + j;
                if ((dx >= 0) && (dx < 10) && (dy >= 0) && (dy < 10) && (field[dx][dy].getState() == Model.SHIP)) {
                    density++;
                }
            }
        }

        return density;
    }

    public Cell[][] randomPlacementShips(Cell[][] field) {
        clearGameField(field);
        int x, y, kx, ky;

        Random random = new Random();
        for (int N = 3; N >= 0; N--) {
            for (int M = 0; M <= 3 - N; M++) {
                boolean freePlace;
                int maxAttempts = 100;
                int attempts = 0;

                do {
                   x = random.nextInt(10);
                    y = random.nextInt(10);
                    kx = random.nextInt(2);
                    if (kx == 0) {
                        ky = 1;
                    } else {
                        ky = 0;
                    }

                    int density = calculateDensity(x, y, field);

                    if (density <= 2) {
                        freePlace = true;
                        for (int j = 0; j <= N; j++) {
                            if (!freedom(x + kx * j, y + ky * j, field)) {
                                freePlace = false;
                                break;
                            }
                        }

                        if (freePlace) {
                            for (int k = 0; k <= N; k++) {
                                field[x + kx * k][y + ky * k].setState(Model.SHIP);
                            }
                        }
                    } else {
                        freePlace = false;
                    }

                    attempts++;
                } while (!freePlace && attempts < maxAttempts);
            }
        }
        return field;
    }

    public void clearGameField(Cell[][] field){
        for (int y = 0; y < field.length; y++) {
            for (int x = 0; x < field.length; x++) {
                field[y][x].setState(Model.HIDE);
                field[y][x].setNeedRepaint(true);
            }
        }
    }
}
