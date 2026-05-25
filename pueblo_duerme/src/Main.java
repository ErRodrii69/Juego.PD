import javax.swing.SwingUtilities;
import ui.JuegoFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JuegoFrame().setVisible(true));
    }
}
