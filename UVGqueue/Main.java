import controller.UsuarioController;
import javax.swing.SwingUtilities;
import view.LoginView;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            new UsuarioController(loginView);
            loginView.setVisible(true);
        });
    }
} 