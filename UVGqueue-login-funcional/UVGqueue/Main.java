import controller.Logincontroller;
import service.Authservice;
import view.LoginView;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        configurarApariencia();

        SwingUtilities.invokeLater(() -> {
            LoginView vista = new LoginView();
            new Logincontroller(vista, new Authservice());
            vista.setVisible(true);
        });
    }

    private static void configurarApariencia() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception excepcion) {
            // Swing utilizará su apariencia predeterminada.
        }
    }
}
