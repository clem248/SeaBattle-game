import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.Point;

import java.awt.event.*;

public class Controller implements MouseListener, ComponentListener, MouseMotionListener {
    private Model model;

    public Controller(Viewer viewer) {
        model = new Model(viewer);
    }

    public Model getModel() {
        return model;
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        // trim window header
        JFrame frame = (JFrame) event.getComponent();
        JPanel panel = (JPanel) frame.getContentPane();
        Point mouse = SwingUtilities.convertPoint(frame, event.getPoint(), panel);

        model.mouseMoved(mouse.x, mouse.y);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent event) {
        // trim window header
        JFrame frame = (JFrame) event.getComponent();
        JPanel panel = (JPanel) frame.getContentPane();
        Point point = SwingUtilities.convertPoint(frame, event.getPoint(), panel);

        model.doAction(point.x, point.y);
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void componentResized(ComponentEvent e) {
        model.resize();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

}
