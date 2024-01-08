import javax.swing.*;
import java.awt.*;

public class Viewer {
    private final Canvas canvas;
    private JFrame frame;
    private Model model;

    public Viewer() {
        Controller controller = new Controller(this);
        model = controller.getModel();
        canvas = new Canvas(model);

        frame = new JFrame("Spring API Web Services - Sea Battle");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setSize(700, 500);
        frame.setUndecorated(true);
        frame.setResizable(false);
        frame.add(canvas);
        frame.setVisible(true);
        frame.addMouseListener(controller);
        frame.addComponentListener(controller);
        frame.addMouseMotionListener(controller);
    }

    public void update() {
        canvas.repaint();
    }

    public void resize() {
        canvas.resize();
    }

    public void setWaitCursor(boolean wait) {
        if (frame == null) {
            return;
        }

        if (wait) {
            frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        } else {
            frame.setCursor(Cursor.getDefaultCursor());
        }
    }

    public void onComputerWin() {
        JOptionPane.showMessageDialog(frame, "Computer WIN!", "Computer win", JOptionPane.INFORMATION_MESSAGE);
        model.onRestart();
    }

    public void onPlayerWin() {
        JOptionPane.showMessageDialog(frame, "You WIN!", "You win", JOptionPane.INFORMATION_MESSAGE);
        model.onRestart();
    }
}
