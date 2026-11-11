import controller.UsuarioController;
import javafx.application.Application;
import javafx.stage.Stage;
import view.LoginView;

public class FastQueueApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        LoginView loginView = new LoginView(primaryStage);
        new UsuarioController(loginView);
        loginView.show();
    }
}
