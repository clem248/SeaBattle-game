import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Graphics;

public class Button {
    private Image image;
    private boolean isHover;
    private final Rectangle origin;
    private final Rectangle hover;
    private boolean needRepaint;
    private final PlayMusic playMusic;
    private boolean needBackgroundRepaint;

    public Button(Image image, PlayMusic playMusic) {
        this.image = image;
        this.playMusic = playMusic;
        origin = new Rectangle();
        hover = new Rectangle();
        needRepaint = true;
    }

    public void paint(Graphics g) {
        if (!needRepaint) {
            return;
        }

        if (isHover) {
            g.drawImage(image, hover.x, hover.y, hover.width, hover.height, null);
        } else {
            g.drawImage(image, origin.x, origin.y, origin.width, origin.height, null);
        }

        needRepaint = false;
        needBackgroundRepaint = false;
    }

    public void onResize(int x, int y, int width, int height) {
        origin.x = x;
        origin.y = y;
        origin.width = width;
        origin.height = height;

        double scaleFactor = 1.05;
        hover.width = (int) (width * scaleFactor);
        hover.height = (int) (height * scaleFactor);
        hover.x = x - (hover.width - width) / 2;
        hover.y = y - (hover.height - height) / 2;

        needRepaint = true;
    }

    public void setImage(Image newImage) {
        image = newImage;
        needRepaint = true;
    }
   
    public void onMouseMove(int x, int y) {
        if (!isHover() && contains(x, y)) {
            playMusic.playButtonHoverSound();
            isHover = true;
            needRepaint = true;
        } else if (isHover() && !contains(x, y)) {
            playMusic.playButtonHoverSound();
            isHover = false;
            needRepaint = true;
            needBackgroundRepaint = true;
        }
    }

    public boolean contains(int x, int y) {
        return (isHover) ? hover.contains(x, y) : origin.contains(x, y);
    }

    public boolean isHover() {
        return isHover;
    }

    public boolean isNeedRepaint() {
        return needRepaint;
    }

    public boolean isNeedBackgroundRepaint() {
        return needBackgroundRepaint;
    }

    public void setNeedRepaint(boolean needRepaint) {
        this.needRepaint = needRepaint;
    }

}