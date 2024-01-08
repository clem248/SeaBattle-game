import javax.swing.JPanel;

import java.awt.Image;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.event.MouseEvent;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;

import java.awt.event.MouseAdapter;
import java.io.NotSerializableException;

public class Canvas extends JPanel {
    private static final long serialVersionUID = 1L;
    private final static String HIDE_CELL = "images/hide.png";
    private final static String MISS_CELL = "images/miss.png";    
    private final static String SHIP_CELL = "images/ship.png";
    private final static String SUNK_CELL = "images/sunk.png";
    private final static String BACKGROUND = "images/background.png";
    private final static String BTN_AUTO = "images/auto.png";
    private final static String BTN_EXIT = "images/exit.png";
    private final static String BTN_RESTART = "images/restart.png";
    private final static String BTN_START = "images/start.png";

    private final Model model;
    private final Image[] cellImages;
    private final Image background;
    private final Cell[][] playerField;
    private final Cell[][] computerField;
    private int radarSize;
    private int horizontalMargin;
    private int verticalMargin;
    private final Button btnExit;
    private final Button btnRestart;
    private final Button btnAuto;
    private final Image imgStart;
    private final Image imgRestart;
    private final Color radarColor;
    private boolean backgroundNeedRepaint;


    public Canvas(Model model) {
        this.model = model;
        playerField = model.getPlayer();
        computerField = model.getComputer();

        cellImages = loadCellImages();
        background = loadImage(BACKGROUND);
        radarColor = new Color(59, 165, 98);

        imgStart = loadImage(BTN_START);
        imgRestart = loadImage(BTN_RESTART);
        btnRestart = new Button(imgStart, model.getPlayMusic());
        btnAuto = new Button(loadImage(BTN_AUTO), model.getPlayMusic());
        btnExit = new Button(loadImage(BTN_EXIT), model.getPlayMusic());
        model.setBtnRestart(btnRestart);
        model.setBtnAuto(btnAuto);
        model.setBtnExit(btnExit);
    }

    private Image[] loadCellImages() {
        Image[] images = new Image[5];
        images[Model.HIDE] = loadImage(HIDE_CELL);
        images[Model.MISS] = loadImage(MISS_CELL);
        images[Model.SHIP] = loadImage(SHIP_CELL);
        images[Model.SUNK] = loadImage(SUNK_CELL);
        images[Model.FULLY_SUNK] = loadImage(HIDE_CELL);
        return images;
    }

    private Image loadImage(String filename) {
        Image image;
        try {
            image = ImageIO.read(new File(filename));
        } catch (IOException ioe) {
            System.err.println(ioe + " Filename: " + filename);
            image = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
        }
        return image;
    }

    @Override
    public void paintComponent(Graphics g) {
        if (backgroundNeedRepaint || btnAuto.isNeedBackgroundRepaint()
                || btnExit.isNeedBackgroundRepaint() || btnRestart.isNeedBackgroundRepaint()) {
            backgroundNeedRepaint = true;

            btnAuto.setNeedRepaint(true);
            btnRestart.setNeedRepaint(true);
            btnExit.setNeedRepaint(true);

            for (int y = 0; y < 10; y++) {
                for (int x = 0; x < 10; x++) {
                    playerField[y][x].setNeedRepaint(true);
                    computerField[y][x].setNeedRepaint(true);
                }
            }
        } else {
            backgroundNeedRepaint = false;
        }

        if (backgroundNeedRepaint) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);

            g.setColor(radarColor);
            // Player Radar
            g.drawRect(horizontalMargin, verticalMargin, radarSize, radarSize);
            g.drawOval(horizontalMargin, verticalMargin, radarSize, radarSize);

            // Computer Radar
            g.drawRect(radarSize + horizontalMargin * 2, verticalMargin, radarSize, radarSize);
            g.drawOval(radarSize + horizontalMargin * 2, verticalMargin, radarSize, radarSize);

            backgroundNeedRepaint = false;
        }

        
        // Player Radar
        for (int y = 0; y < playerField.length; y++) {
            for (int x = 0; x < playerField.length; x++) {
                Cell cell = playerField[y][x];
                Image image = cellImages[cell.getState()];
                cell.paint(g, image);
            }
        }

        // Computer Radar
        for (int y = 0; y < computerField.length; y++) {
            for (int x = 0; x < computerField.length; x++) {
                Cell cell = computerField[y][x];
                Image image = (cell.isVisible()) ? cellImages[cell.getState()] : cellImages[Model.HIDE];
                cell.paint(g, image);
            }
        }

        if (model.isStart()) {
            btnRestart.setImage(imgRestart);
        } else {
            btnRestart.setImage(imgStart);
            btnAuto.paint(g);
        }
        btnRestart.paint(g);        
        btnExit.paint(g);
    }

    public void resize() {
        backgroundNeedRepaint = true;

        // Game Window
        int width = getWidth();
        int height = getHeight();

        // Buttons Size, Original size: 200 * 79
        int btnWidth = (int) (width / 1920.0 * 200);
        int btnHeight = (int) (height / 1080.0 * 79);

        // Width: 10% PlayerField 10% ComputerField 10%
        // Height: 10% Field 10% button 10%
        int radarFieldWidth = (int) (width * 0.7 / 2);
        int radarFieldHeight = (int) (height * 0.7 - btnHeight);
        radarSize = Math.min(radarFieldWidth, radarFieldHeight) - 1;
        horizontalMargin = (width - radarSize * 2) / 3;
        verticalMargin = (height - radarSize - btnHeight) / 3;

        // Buttons Position
        int btnLeft = horizontalMargin + radarSize / 2 - btnWidth / 2;
        int btnMiddle = width / 2 - btnWidth / 2;
        int btnRight = width - horizontalMargin - radarSize / 2 - btnWidth / 2;
        int btnTop = height - verticalMargin - btnHeight;
        btnRestart.onResize(btnLeft, btnTop, btnWidth, btnHeight);
        btnAuto.onResize(btnMiddle, btnTop, btnWidth, btnHeight);
        btnExit.onResize(btnRight, btnTop, btnWidth, btnHeight);

        // Player & Computer Field
        int padding = (int) (radarSize / 2.0 - Math.sqrt(radarSize * radarSize / 8.0));
        int size = radarSize - padding * 2;
        int cellSize = size / 10;

        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                int cellX = x * cellSize + padding + horizontalMargin;
                int cellY = y * cellSize + padding + verticalMargin;

                playerField[y][x].x = cellX;
                playerField[y][x].y = cellY;
                playerField[y][x].width = cellSize;
                playerField[y][x].height = cellSize;

                computerField[y][x].x = cellX + radarSize + horizontalMargin;
                computerField[y][x].y = cellY;
                computerField[y][x].width = cellSize;
                computerField[y][x].height = cellSize;
            }
        }
    }

    /**
     * We explicitly prohibit serialization and deserialization, violating the Liskov principle...
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        throw new NotSerializableException("This class cannot be serialized");
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        throw new NotSerializableException("This class cannot be deserialized");
    }

}