import dao.FilaDAO;
import dao.RestauranteDAO;
import dao.TurnoDAO;
import dao.UsuarioDAO;
import java.util.List;
import javax.swing.SwingUtilities;
import model.Fila;
import model.Restaurante;
import model.Turno;
import model.Usuario;
import view.LoginView;

public class Main {

    public static void main(String[] args) {

        System.out.println("=== PRUEBA DE DAO UVGQUEUE ===");

        // ---------------------------------
        // 1. PROBAR RESTAURANTE DAO
        // ---------------------------------
        System.out.println("\n--- RESTAURANTES ---");
        RestauranteDAO restauranteDAO = new RestauranteDAO();
        List<Restaurante> restaurantes = restauranteDAO.obtenerTodos();

        for (Restaurante restaurante : restaurantes) {
            System.out.println(
                    restaurante.getIdRestaurante()
                    + " | "
                    + restaurante.getNombre()
                    + " | "
                    + restaurante.getUbicacion()
            );
        }

        // ---------------------------------
        // 2. PROBAR USUARIO DAO
        // ---------------------------------
        System.out.println("\n--- USUARIO ---");
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = usuarioDAO.buscarPorCorreo("prueba@uvg.edu.gt");

        if (usuario != null) {
            System.out.println(
                    "Usuario encontrado: "
                    + usuario.getNombre()
                    + " | "
                    + usuario.getRol()
            );
        } else {
            System.out.println("Usuario de prueba no encontrado.");
        }

        // ---------------------------------
        // 3. PROBAR FILA DAO
        // ---------------------------------
        System.out.println("\n--- FILA ---");
        FilaDAO filaDAO = new FilaDAO();
        Fila fila = filaDAO.buscarPorRestaurante(1);

        if (fila != null) {
            System.out.println(
                    "Fila encontrada"
                    + " | ID: " + fila.getIdFila()
                    + " | Restaurante: " + fila.getIdRestaurante()
                    + " | Estado: " + fila.getEstado()
            );
        } else {
            System.out.println("No hay fila para el restaurante 1.");
        }

        // ---------------------------------
        // 4. PROBAR TURNO DAO
        // ---------------------------------
        System.out.println("\n--- TURNOS ---");
        if (fila != null) {
            TurnoDAO turnoDAO = new TurnoDAO();
            List<Turno> pendientes = turnoDAO.obtenerPendientes(fila.getIdFila());

            System.out.println("Turnos esperando: " + pendientes.size());

            for (Turno turno : pendientes) {
                System.out.println(
                        "Turno #" + turno.getNumeroTurno()
                        + " | Usuario: " + turno.getIdUsuario()
                        + " | Estado: " + turno.getEstado()
                );
            }

            Turno siguiente = turnoDAO.obtenerSiguiente(fila.getIdFila());
            if (siguiente != null) {
                System.out.println("Siguiente turno: #" + siguiente.getNumeroTurno());
            } else {
                System.out.println("No hay personas esperando.");
            }
        }

        System.out.println("\n=== FIN DE PRUEBAS DAO ===");

        // ---------------------------------
        // 5. LANZAR VISTA DE LOGIN (SWING)
        // ---------------------------------
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();

            // Evento Clic en Sign In
            loginView.getBtnSignIn().addActionListener(e -> {
                String user = loginView.getLoginUser();
                System.out.println("Intento de login con: " + user);
            });

            // Evento Clic en Olvidé mi contraseña
            loginView.getBtnForgotPassword().addActionListener(e -> {
                loginView.mostrarMensaje("Recuperar Contraseña",
                        "Se han enviado las instrucciones de recuperación a tu correo.");
            });

            // Evento Clic en Confirmar datos (Registro)
            loginView.getBtnConfirmRegister().addActionListener(e -> {
                String correo = loginView.getRegEmail();
                loginView.mostrarMensaje("Registro completado",
                        "Se ha enviado un correo de confirmación de la creación de la cuenta a: " + correo);
            });

            loginView.setVisible(true);
        });
    }
}