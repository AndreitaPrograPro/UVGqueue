package controller;
import dao.ReporteDAO;
import dao.RestauranteDAO;
import model.*;
import view.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class ReporteController {
    private final EstudianteFrame estudianteFrame;
    private final ReporteDAO reporteDAO;
    private final RestauranteDAO restauranteDAO;
    private final Usuario usuarioActivo;

    public ReporteController(
            EstudianteFrame estudianteFrame,
            Usuario usuarioActivo) {

        this.estudianteFrame = estudianteFrame;
        this.usuarioActivo = usuarioActivo;
        this.reporteDAO = new ReporteDAO();
        this.restauranteDAO = new RestauranteDAO();

        estudianteFrame.setAccionReportar(
                this::procesarReporte
        );

        // Conservamos la sección Mis reportes
        estudianteFrame.setAccionVerReportes(
                this::cargarMisReportes
        );

        cargarPromedios();
    }

    private void procesarReporte(Restaurante restaurante) {

        // Verificar el estado actual desde MariaDB
        if (!restauranteDisponible(restaurante)) {

            estudianteFrame.mostrarError(
                    "Este restaurante no está disponible "
                    + "para reportes de fila."
            );
            return;
        }
        FormularioView formulario = new FormularioView();
        formulario.setAccionEnviar(
                (cantidadPersonas, tiempoAtencion) -> {

                    int tiempoEspera =
                            (int) Math.round(tiempoAtencion);

                    guardarReporte(
                            restaurante,
                            cantidadPersonas,
                            tiempoEspera,
                            formulario
                    );
                }
        );

        formulario.setVisible(true);
    }
    private boolean restauranteDisponible(
            Restaurante restaurante) {

        ArrayList<Restaurante> restaurantes =
                restauranteDAO.obtenerTodos();

        for (Restaurante actual : restaurantes) {

            if (actual.getNombre().equals(
                    restaurante.getNombre())) {

                return actual.permiteReportes();
            }
        }
        return false;
    }
    private void guardarReporte(
            Restaurante restaurante,
            int cantidadPersonas,
            int tiempoEspera,
            FormularioView formulario) {

        if (!restauranteDisponible(restaurante)) {

            estudianteFrame.mostrarError(
                    "El restaurante ya no está disponible. "
                    + "No se puede guardar el reporte."
            );

            return;
        }

        Reporte reporte = new Reporte(
                usuarioActivo,
                cantidadPersonas,
                tiempoEspera
        );
        boolean registrado = reporteDAO.registrar(
                reporte,
                restaurante
        );
        if (registrado) {
            restaurante.agregarReporte(reporte);
            formulario.dispose();
            cargarPromedios();

            estudianteFrame.mostrarMensaje(
                    "Reporte registrado",
                    "Gracias por compartir la información."
            );

        } else {
            estudianteFrame.mostrarError(
                    "No fue posible guardar el reporte."
            );
        }
    }
    public void cargarPromedios() {
        Map<String, ArrayList<Integer>> tiempos =
                reporteDAO.obtenerTiempos();

        Map<String, Integer> promedios =
                new HashMap<>();

        for (String restaurante : tiempos.keySet()) {

            promedios.put(
                    restaurante,
                    calcularPromedio(
                            tiempos.get(restaurante)
                    )
            );
        }
        estudianteFrame.mostrarPromedios(promedios);
    }

    private int calcularPromedio(
            ArrayList<Integer> tiempos) {

        if (tiempos == null || tiempos.isEmpty()) {
            return 0;
        }
        int suma = 0;
        for (int tiempo : tiempos) {
            suma += tiempo;
        }
        return Math.round(
                (float) suma / tiempos.size()
        );
    }

    private void cargarMisReportes() {

        ArrayList<Reporte> reportes =
                reporteDAO.obtenerPorUsuario(usuarioActivo);

        estudianteFrame.mostrarMisReportes(reportes);
    }
}