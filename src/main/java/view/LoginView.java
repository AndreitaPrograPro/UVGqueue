package view;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginView {
    private final Stage stage;
    private final StackPane formHost = new StackPane();
    private final TextField loginUser = new TextField();
    private final PasswordField loginPassword = new PasswordField();
    private final TextField regUser = new TextField();
    private final TextField regEmail = new TextField();
    private final PasswordField regPassword = new PasswordField();
    private final PasswordField regConfirmPassword = new PasswordField();
    private final Button btnSignIn = new Button("Iniciar sesión");
    private final Button btnConfirmRegister = new Button("Crear mi cuenta");
    private final Button btnForgotPassword = new Button("Olvidé mi contraseña");

    public LoginView(Stage stage) {
        this.stage = stage;
        configurarCampos();
        BorderPane root = new BorderPane();
        root.getStyleClass().add("login-shell");
        root.setLeft(crearMarca());
        root.setCenter(crearFormulario());
        Scene scene = new Scene(root, 1120, 720);
        scene.getStylesheets().add(getClass().getResource("/styles/fastqueue.css").toExternalForm());
        stage.setTitle("FastQueue · Tu tiempo importa");
        stage.setMinWidth(940);
        stage.setMinHeight(620);
        stage.setScene(scene);
        stage.centerOnScreen();
        mostrarLogin();
    }

    public LoginView() { this(new Stage()); }

    private void configurarCampos() {
        loginUser.setPromptText("Usuario o correo");
        loginPassword.setPromptText("Contraseña");
        regUser.setPromptText("Nombre de usuario");
        regEmail.setPromptText("Correo electrónico");
        regPassword.setPromptText("Contraseña (mínimo 8 caracteres)");
        regConfirmPassword.setPromptText("Confirmar contraseña");
        btnSignIn.getStyleClass().add("primary-button");
        btnConfirmRegister.getStyleClass().add("primary-button");
        btnForgotPassword.getStyleClass().add("ghost-button");
        btnSignIn.setMaxWidth(Double.MAX_VALUE);
        btnConfirmRegister.setMaxWidth(Double.MAX_VALUE);
    }

    private Pane crearMarca() {
        VBox box = new VBox(18);
        box.getStyleClass().add("login-brand");
        box.setPadding(new Insets(70, 60, 60, 60));
        box.setPrefWidth(440);
        box.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("FastQueue");
        title.getStyleClass().add("brand-title");
        Label copy = new Label("Menos espera. Más tiempo para disfrutar.");
        copy.getStyleClass().add("brand-copy");
        copy.setWrapText(true);

        StackPane art = new StackPane(new IlustracionCafe());
        art.setMinHeight(300);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        Label footer = new Label("La fila se comparte. El tiempo se aprovecha.");
        footer.getStyleClass().add("brand-copy");
        box.getChildren().addAll(title, copy, art, spacer, footer);
        return box;
    }

    private Pane crearFormulario() {
        StackPane wrapper = new StackPane(formHost);
        wrapper.setPadding(new Insets(45, 70, 45, 70));
        StackPane.setAlignment(formHost, Pos.CENTER);
        formHost.setMaxWidth(430);
        return wrapper;
    }

    private VBox loginPane() {
        Label eyebrow = new Label("QUÉ GUSTO VERTE");
        eyebrow.setStyle("-fx-text-fill:#4F7A52;-fx-font-weight:bold;");
        Label title = new Label("Bienvenida de nuevo");
        title.getStyleClass().addAll("display", "h1");
        title.setStyle("-fx-font-size:32px;");
        title.setWrapText(true);
        Label subtitle = new Label("Entra y encuentra la fila que más te conviene.");
        subtitle.getStyleClass().add("muted");
        subtitle.setWrapText(true);

        Button create = new Button("Crear cuenta");
        create.getStyleClass().add("ghost-button");
        create.setOnAction(e -> mostrarRegistro());

        VBox links = new VBox(1, btnForgotPassword, create);
        links.setAlignment(Pos.CENTER);
        VBox card = new VBox(16, eyebrow, title, subtitle, space(8), labeled("Usuario", loginUser),
                labeled("Contraseña", loginPassword), space(4), btnSignIn, links);
        card.getStyleClass().add("form-card");
        card.setPadding(new Insets(38));
        return card;
    }

    private VBox registerPane() {
        Label eyebrow = new Label("EMPIEZA A AHORRAR TIEMPO");
        eyebrow.setStyle("-fx-text-fill:#E85D3F;-fx-font-weight:bold;");
        Label title = new Label("Crea tu cuenta");
        title.getStyleClass().addAll("display", "h1");
        title.setStyle("-fx-font-size:32px;");
        Label subtitle = new Label("Tus favoritos y reportes estarán listos en cualquier equipo.");
        subtitle.getStyleClass().add("muted");
        subtitle.setWrapText(true);
        Button back = new Button("Ya tengo cuenta");
        back.getStyleClass().add("ghost-button");
        back.setOnAction(e -> mostrarLogin());
        VBox card = new VBox(13, eyebrow, title, subtitle, labeled("Nombre", regUser),
                labeled("Correo", regEmail), labeled("Contraseña", regPassword),
                labeled("Confirmación", regConfirmPassword), btnConfirmRegister, back);
        card.getStyleClass().add("form-card");
        card.setPadding(new Insets(34));
        return card;
    }

    private VBox labeled(String text, Control field) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight:bold;-fx-text-fill:#3B2A20;");
        field.setMaxWidth(Double.MAX_VALUE);
        return new VBox(6, label, field);
    }

    private Region space(double height) { Region r = new Region(); r.setMinHeight(height); return r; }

    private void swap(Pane pane) {
        formHost.getChildren().setAll(pane);
        FadeTransition fade = new FadeTransition(Duration.millis(260), pane);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    public void mostrarLogin() { swap(loginPane()); }
    public void mostrarRegistro() { swap(registerPane()); }
    public void limpiarRegistro() { regUser.clear(); regEmail.clear(); regPassword.clear(); regConfirmPassword.clear(); }
    public String getLoginUser() { return loginUser.getText(); }
    public String getLoginPassword() { return loginPassword.getText(); }
    public String getRegUser() { return regUser.getText(); }
    public String getRegEmail() { return regEmail.getText(); }
    public String getRegPassword() { return regPassword.getText(); }
    public String getRegConfirmPassword() { return regConfirmPassword.getText(); }
    public Button getBtnSignIn() { return btnSignIn; }
    public Button getBtnConfirmRegister() { return btnConfirmRegister; }
    public Button getBtnForgotPassword() { return btnForgotPassword; }
    public void show() { stage.show(); }
    public void close() { stage.close(); }
    public Stage getStage() { return stage; }

    public void mostrarMensaje(String titulo, String mensaje) {
        Alert alert = new Alert(titulo.toLowerCase().contains("error") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.initOwner(stage);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.setTitle("FastQueue");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/fastqueue.css").toExternalForm());
        alert.showAndWait();
    }
}
