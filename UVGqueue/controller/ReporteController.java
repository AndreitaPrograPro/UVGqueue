package controller;
import dao.ReporteDAO;
import model.*;
import view.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class ReporteController {
    private final EstudianteFrame estudianteFrame;
    private final ReporteDAO reporteDAO;
    private final Usuario usuarioActivo;
    public ReporteController(
            EstudianteFrame estudianteFrame,
            Usuario usuarioActivo
    ) {
        this.estudianteFrame = estudianteFrame;
        this.usuarioActivo = usuarioActivo;
        this.reporteDAO = new ReporteDAO();
        estudianteFrame.setAccionReportar(this::procesarReporte);
        cargarPromedios();
    }
    private void procesarReporte(Restaurante restaurante) {
        if (restaurante.getEstado() != EstadoRestaurante.ABIERTO) {
            estudianteFrame.mostrarError(
                    "El restaurante está cerrado."
            );
            return;
        }
        FormularioView formulario = new FormularioView();
        formulario.setAccionEnviar(
                (cantidadPersonas, tiempoAtencion) -> {
                    int tiempoEspera = (int) Math.round(tiempoAtencion);
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
    private void guardarReporte(
            Restaurante restaurante,
            int cantidadPersonas,
            int tiempoEspera,
            FormularioView formulario
    ) {
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
            estudianteFrame.mostrarMensaje(
                    "Reporte registrado",
                    "Gracias por compartir la información."
            );
            formulario.dispose();
            cargarPromedios();
        } else {
            estudianteFrame.mostrarError(
                    "No fue posible guardar el reporte."
            );
        }
    }
    public void cargarPromedios() {
        Map<String, ArrayList<Integer>> tiempos =
                reporteDAO.obtenerTiempos();
        Map<String, Integer> promedios = new HashMap<>();
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
    private int calcularPromedio(ArrayList<Integer> tiempos) {
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
}