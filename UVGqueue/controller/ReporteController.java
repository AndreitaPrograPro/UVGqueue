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
        if (restaurante.getEstado()
                != EstadoRestaurante.ABIERTO) {
            estudianteFrame.mostrarError(
                    "El restaurante está cerrado."
            );
            return;
        }

        String personas = estudianteFrame.solicitarDato(
                "¿Cuántas personas hay en la fila?"
        );

        if (personas == null) {
            return;
        }

        String espera = estudianteFrame.solicitarDato(
                "¿Cuántos minutos aproximadamente hay que esperar?"
        );

        if (espera == null) {
            return;
        }

        try {
            int cantidadPersonas =
                    Integer.parseInt(personas);

            int tiempoEspera =
                    Integer.parseInt(espera);

            if (cantidadPersonas < 0 || tiempoEspera < 0) {
                estudianteFrame.mostrarError(
                        "Los números no pueden ser negativos."
                );
                return;
            }

            Reporte reporte = new Reporte(
                    usuarioActivo,
                    cantidadPersonas,
                    tiempoEspera
            );

            boolean registrado =
                    reporteDAO.registrar(
                            reporte,
                            restaurante
                    );

            if (registrado) {
                restaurante.agregarReporte(reporte);
                estudianteFrame.mostrarMensaje("Reporte registrado", "Gracias por compartir la información.");
                cargarPromedios();
            } else {
                estudianteFrame.mostrarError(
                        "No fue posible guardar el reporte."
                );
            }

        } catch (NumberFormatException e) {
            estudianteFrame.mostrarError(
                    "Debes ingresar números enteros."
            );
        }
    }
    public void cargarPromedios(){
        Map<String, ArrayList<Integer>> tiempos = reporteDAO.obtenerTiempos();
        Map<String, Integer> promedios = new HashMap<>();
        for (String restaurante : tiempos.keySet()){
                promedios.put(restaurante, calcularPromedio(tiempos.get(restaurante)));
        }
        estudianteFrame.mostrarPromedios(promedios);
    }
    private int calcularPromedio(ArrayList<Integer> tiempos){
        if (tiempos == null ||  tiempos.isEmpty){
                return 0;
        }
        int suma=0;
        for (int tiempo : tiempos){
                suma+=tiempo;
        }
        return Math.round((float) suma / tiempos.size());
    }
}