package controller;

import dao.ReporteDAO;
import dao.RestauranteDAO;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.util.Duration;
import model.*;
import view.*;

public class ReporteController {
        private final EstudianteFrame estudianteFrame;
        private final ReporteDAO reporteDAO;
        private final RestauranteDAO restauranteDAO;
        private final Usuario usuarioActivo;
        private final Timeline temporizadorPromedios;
        public ReporteController(
                EstudianteFrame estudianteFrame,
                Usuario usuarioActivo
        ) {
                this.estudianteFrame = estudianteFrame;
                this.usuarioActivo = usuarioActivo;
                this.reporteDAO = new ReporteDAO();
                this.restauranteDAO = new RestauranteDAO();
                estudianteFrame.setAccionReportar(this::procesarReporte);
                estudianteFrame.setAccionVerReportes(this::cargarMisReportes);
                estudianteFrame.getBotonActualizar().addEventHandler(
                        ActionEvent.ACTION, evento -> cargarPromedios());
                estudianteFrame.setAccionVerDetalle(
                this::mostrarDetalleRestaurante);

                cargarPromedios();

                temporizadorPromedios = new Timeline(new KeyFrame(
                        Duration.seconds(60), evento -> cargarPromedios()));
                temporizadorPromedios.setCycleCount(Timeline.INDEFINITE);
                temporizadorPromedios.play();
        }
        private void procesarReporte(Restaurante restaurante) {

                if (!restauranteDisponible(restaurante)) {
                estudianteFrame.mostrarError(
                        "Este restaurante no está disponible "
                        + "para reportes de fila."
                );
                return;
                }
                FormularioView formulario = new FormularioView();

                formulario.setAccionEnviar(
                        (cantidadPersonas, minutosEspera) ->
                                guardarReporte(
                                        restaurante,
                                        cantidadPersonas,
                                        (int) Math.round(minutosEspera),
                                        formulario
                                )
                );
                formulario.show();
        }
        private boolean restauranteDisponible(
                Restaurante restaurante
        ) {
                return restauranteDAO.obtenerTodos()
                        .stream()
                        .filter(actual -> actual.getNombre().equals(
                                restaurante.getNombre()
                        ))
                        .findFirst()
                        .map(Restaurante::permiteReportes)
                        .orElse(false);
        }
        private void guardarReporte(
                Restaurante restaurante,
                int cantidadPersonas,
                int tiempoEspera,
                FormularioView formulario
        ) {
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

                if (!reporteDAO.registrar(reporte, restaurante)) {
                estudianteFrame.mostrarError(
                        "No fue posible guardar el reporte."
                );
                return;
                }

                restaurante.agregarReporte(reporte);
                formulario.close();
                cargarPromedios();

                estudianteFrame.mostrarMensaje(
                        "Reporte registrado",
                        "Gracias por compartir la información."
                );
        }

        public void cargarPromedios() {

                LocalDateTime horaActual = LocalDateTime.now();

                Map<String, ArrayList<Integer>> tiempos =
                        reporteDAO.obtenerTiempos(
                                horaActual.minusMinutes(30),
                                horaActual.plusMinutes(30)
                        );

                Map<String, Integer> promedios = new HashMap<>();

                tiempos.forEach((restaurante, lista) -> {
                if (lista != null && !lista.isEmpty()) {
                        promedios.put(
                                restaurante,
                                calcularPromedio(lista)
                        );
                }
                });

                estudianteFrame.mostrarPromedios(promedios);
        }

        private int calcularPromedio(
                ArrayList<Integer> tiempos
        ) {
                return (int) Math.round(
                        tiempos.stream()
                                .mapToInt(Integer::intValue)
                                .average()
                                .orElse(0)
                );
        }

        private void cargarMisReportes() {
                estudianteFrame.mostrarMisReportes(
                        reporteDAO.obtenerPorUsuario(usuarioActivo)
                );
        }
        private void mostrarDetalleRestaurante(
                        Restaurante restaurante
                ) {
                LocalDateTime fechaInicio =
                        LocalDateTime.of(
                                2026, 1, 1,
                                0, 0
                        );

                LocalDateTime fechaFin =
                        LocalDateTime.now();

                Map<String, ArrayList<Integer>> tiemposPorRestaurante =
                        reporteDAO.obtenerTiempos(
                                fechaInicio,
                                fechaFin
                        );

                ArrayList<Integer> tiempos =
                        tiemposPorRestaurante.getOrDefault(
                                restaurante.getNombre(),
                                new ArrayList<>()
                        );

                Integer promedio = tiempos.isEmpty()
                        ? null
                        : calcularPromedio(tiempos);

                RestauranteDetalleFrame detalle =
                        new RestauranteDetalleFrame(
                                restaurante,
                                promedio,
                                tiempos
                        );

                detalle.show();
        }
}
