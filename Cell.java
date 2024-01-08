import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.NotSerializableException;

public class Cell extends Rectangle {
    private static final long serialVersionUID = 1L;
    private boolean visible;
    private byte state;
    private boolean needRepaint;

    public Cell (byte state) {
        this.state = state;
    }

    public void paint(Graphics g, Image image) {
        if (!needRepaint) {
            return;
        }
        g.drawImage(image, x, y, width, height, null);
        needRepaint = false;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        needRepaint = true;
    }

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public boolean isNeedRepaint() {
        return needRepaint;
    }

    public void setNeedRepaint(boolean needRepaint) {
        this.needRepaint = needRepaint;
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
