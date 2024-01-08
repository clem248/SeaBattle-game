public class Model {
    public final static byte HIDE = 0;
    public final static byte MISS = 1;
    public final static byte SHIP = 2;
    public final static byte SUNK = 3;
    public final static byte FULLY_SUNK = 4;
    private final Viewer viewer;
    private final Cell[][] player;
    private final Cell[][] computer;
    private final ShipPlacementGenerator placementGenerator;
    private Button btnRestart;
    private Button btnAuto;
    private Button btnExit;
    private final PlayMusic playMusic;
    private boolean isStart;

    private int turn;
    private final static int HUMAN_TURN = 0;
    private final static int PLAYER_TURN = 1;
    private final static int COMPUTER_TURN = 2;

    private Thread computerThread;
    private Thread playerThread;

    private final BotShooting botShooting;
    private Cell goal;

    public Model(Viewer viewer) {
        this.viewer = viewer;
        placementGenerator = new ShipPlacementGenerator();
        player = createCellBoard();
        computer = createCellBoard();
        restart();
        playMusic = new PlayMusic();
        playMusic.playBackground();
        botShooting = new BotShooting(player);
    }

    private void restart() {
        placementGenerator.randomPlacementShips(player);
        placementGenerator.randomPlacementShips(computer);
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                player[y][x].setVisible(true);
                computer[y][x].setVisible(false);
            }
        }

        turn = HUMAN_TURN;
        viewer.setWaitCursor(false);
        goal = null;
        if(playerThread != null && playerThread.isAlive()) {
            playerThread.interrupt();
        }
        if (computerThread != null && computerThread.isAlive()) {
            computerThread.interrupt();
        }
        playerThread = new Thread(this::playerThread);
        computerThread = new Thread(this::computerThread);
        System.out.println("Two threads are created");
    }

    private synchronized void computerThread() {
        while (isStart && !Thread.currentThread().isInterrupted()) {
            try {
                while (turn != COMPUTER_TURN) {
                    wait();
                }

                Thread.sleep(1600);

                // Computer turn
                if (botShooting.makeMove()) {
                    // success
                    viewer.resize();
                    viewer.update();
                    playMusic.playGoal();
                    Thread.sleep(1600);
                    checkComputerWin();
                } else {
                    // failed
                    turn = HUMAN_TURN;
                    viewer.setWaitCursor(false);
                    viewer.resize();
                    viewer.update();
                    playMusic.playShot();
                    Thread.sleep(1600);
                }
            } catch (InterruptedException e) {
                System.out.println("Computer thread is interrupted");
            }
        }
    }

    private synchronized void playerThread() {
        while (isStart && !Thread.currentThread().isInterrupted()) {
            try {
                while (turn != PLAYER_TURN) {
                    wait();
                }

                Thread.sleep(1600);


                if (shot(computer, goal)) {
                    // success
                    checkPlayerWin();
                    turn = HUMAN_TURN;
                    viewer.setWaitCursor(false);
                    Thread.sleep(1600);
                } else {
                    // failed
                    turn = COMPUTER_TURN;
                    viewer.setWaitCursor(true);
                    Thread.sleep(1600);
                    notify();
                }

            } catch (InterruptedException e) {
                System.out.println("Player thread is interrupted");
            }
        }
    }

    // we come here only if isStart and turn = HUMAN
    private synchronized void humanShot(int x, int y) {
        Cell cell = computer[y][x];
        if (cell.isVisible()) {
            // already shot
            return;
        }

        goal = cell;
        turn = PLAYER_TURN;
        viewer.setWaitCursor(true);
        notifyAll();
    }

    private void checkPlayerWin() {
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                if (computer[y][x].getState() == SHIP) {
                    return;
                }
            }
        }
        viewer.onPlayerWin();
    }

    private void checkComputerWin() {
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                if (player[y][x].getState() == SHIP) {
                    return;
                }
            }
        }
        viewer.onComputerWin();
    }


    private Cell[][] createCellBoard() {
        Cell[][] result = new Cell[10][10];

        for(int y = 0; y < 10; y++) {
            for(int x = 0; x < 10; x++) {
                result[y][x] = new Cell(HIDE);
            }
        }

        return result;
    }

    public boolean allShipsPlaced() {
        return countOccurrences(player, SHIP) == 20;
    }
    
    private int countOccurrences(Cell[][] array, byte targetValue) {
        int count = 0;

        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                if (array[i][j].getState() == targetValue) {
                    count++;
                }
            }
        }

        return count;
    }

    public void mouseMoved(int mouseX, int mouseY) {
        if (!isStart) {
            btnAuto.onMouseMove(mouseX, mouseY);
        }
        btnExit.onMouseMove(mouseX, mouseY);
        btnRestart.onMouseMove(mouseX, mouseY);
        if (btnAuto.isNeedRepaint() || btnExit.isNeedRepaint() || btnRestart.isNeedRepaint()) {
            viewer.update();
        }
    }

    public void onRestart() {
        playMusic.playButtonSound();
        isStart = !isStart;
        if (!isStart) {
            restart();
        } else {
            computerThread.start();
            playerThread.start();
            System.out.println("Two threads are running");
        }
        viewer.resize();
        viewer.update();
    }

    public void doAction(int clickX, int clickY) {
        if (btnRestart.contains(clickX, clickY)) {
            onRestart();
            return;
        }

        if (!isStart && btnAuto.contains(clickX, clickY)) {
            playMusic.playButtonSound();
            placementGenerator.randomPlacementShips(player);
            viewer.resize();
            viewer.update();
            return;
        }

        if (btnExit.contains(clickX, clickY)) {
            System.exit(0);
        }

        // no start or computer
        if (!isStart || turn != HUMAN_TURN) {
            return;
        }
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                Cell cell = computer[y][x];
                if (cell.contains(clickX, clickY)) {
                    humanShot(x, y);
                }
            }
        }

        viewer.update();
    }


    // The shot is already definitely possible, we just carry it out
    private boolean shot(Cell[][] gameField, Cell goal) throws InterruptedException {
        goal.setVisible(true);
        if (goal.getState() == SHIP) {
            // success
            goal.setState(SUNK); // помечаем раненым
            // TODO: помечаем диагональные как невозможные
            viewer.resize();
            viewer.update();
            playMusic.playGoal();
            // shoot again
             return true;
        } else {
            goal.setState(MISS);
            viewer.resize();
            viewer.update();
            playMusic.playShot();
            return false;
        }
    }

    public Cell[][] getPlayer() {
        return player;
    }

    public Cell[][] getComputer() {
        return computer;
    }

    public void playerAutoDrawShips(){
        placementGenerator.randomPlacementShips(player);
        viewer.resize();
    }

    private boolean isFreeSpaceForShip(int x, int y, int length, boolean horizontal, Cell[][] field){
        for (int i = 0; i < length; i++) {
            int currentX = x + (horizontal ? 0 : i);
            int currentY = y + (horizontal ? i : 0);

            if (!placementGenerator.freedom(currentX, currentY, field)) {
                return false;
            }
        }

        return true;
    }

    private void placeShipOnField(int x, int y, int length, boolean horizontal, Cell[][] field){
        if (isFreeSpaceForShip(x, y, length, horizontal,field)) {
            int deltaX = horizontal ? 0 : 1;
            int deltaY = horizontal ? 1 : 0;

            for (int i = 0; i < length; i++) {
                field[x + deltaX * i][y + deltaY * i].setState(SHIP);
            }
            System.out.println("\nThe ship was placed on the field");
        } else {
            System.out.println("\nUnable to place a ship in a selected location!");
        }
    }

    public void resize() {
        viewer.resize();
    }

    public void setBtnAuto(Button btnAuto) {
        this.btnAuto = btnAuto;
    }

    public void setBtnRestart(Button btnRestart) {
        this.btnRestart = btnRestart;
    }

    public void setBtnExit(Button btnExit) {
        this.btnExit = btnExit;
    }

    public PlayMusic getPlayMusic() {
        return playMusic;
    }

    public boolean isStart() {
        return isStart;
    }

}

