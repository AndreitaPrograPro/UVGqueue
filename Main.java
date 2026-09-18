import view.EstudianteFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {

        configurarApariencia();
        SwingUtilities.invokeLater(() -> new EstudianteFrame().setVisible(true));
    }

    private static void configurarApariencia() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception excepcion) {
            // Swing usará su apariencia predeterminada.
        }
    }
}
