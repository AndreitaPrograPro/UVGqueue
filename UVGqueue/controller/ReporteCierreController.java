package controller;
import dao.ReporteCierreDAO;
import model.Restaurante;
import model.Usuario;
import view.EstudianteFrame;

public class ReporteCierreController {

    private final EstudianteFrame estudianteFrame;
    private final Usuario usuarioActivo;
    private final RestauranteController restauranteController;
    private final ReporteCierreDAO reporteCierreDAO;

    public ReporteCierreController(
            EstudianteFrame estudianteFrame,
            Usuario usuarioActivo,
            RestauranteController restauranteController) {

        this.estudianteFrame = estudianteFrame;
        this.usuarioActivo = usuarioActivo;
        this.restauranteController = restauranteController;
        this.reporteCierreDAO = new ReporteCierreDAO();

        registrarEventos();
    }

    private void registrarEventos() {

        estudianteFrame.setAccionVerificarCierre(
                this::verificarRestaurante
        );
    }

    private void verificarRestaurante(
            Restaurante restaurante,
            boolean estaCerrado) {

        if (!restaurante.estaEnHorario()) {

            estudianteFrame.mostrarError(
                    "El restaurante está fuera de su horario de atención."
            );

            return;
        }

        boolean registrado =
                reporteCierreDAO.registrarVerificacion(
                        usuarioActivo,
                        restaurante,
                        estaCerrado
                );

        if (registrado) {

            restauranteController.cargarRestaurantes();

            estudianteFrame.mostrarMensaje(
                    "Verificación registrada",
                    estaCerrado
                            ? "Gracias por reportar que el restaurante está cerrado."
                            : "Gracias por confirmar que el restaurante está abierto."
            );

        } else {

            estudianteFrame.mostrarError(
                    "No fue posible guardar la verificación."
            );
        }
    }
}
