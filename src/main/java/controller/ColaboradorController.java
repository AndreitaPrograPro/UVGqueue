package controller;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import javafx.stage.FileChooser;
import dao.ReporteDAO;
import dao.RestauranteDAO;
import java.util.List;
import javafx.concurrent.Task;
import model.Reporte;
import model.Restaurante;
import view.ColaboradorFrame;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
public class ColaboradorController {

    private final ColaboradorFrame vista;
    private final RestauranteDAO restauranteDAO = new RestauranteDAO();
    private final ReporteDAO reporteDAO = new ReporteDAO();

    private long ultimaConsulta;

    public ColaboradorController(ColaboradorFrame vista) {
        this.vista = vista;

        vista.setAccionSeleccionarRestaurante(this::cargarReportes);
        vista.setAccionActualizar(this::cargarRestaurantes);
        vista.setAccionExportar(this::exportarCSV);
        vista.setAccionDescartar(this::eliminarReporte);
        cargarRestaurantes();
    }

    private void cargarRestaurantes() {
        long consulta = ++ultimaConsulta;

        Task<List<Restaurante>> tarea = new Task<>() {
            @Override
            protected List<Restaurante> call() {
                return restauranteDAO.obtenerTodos();
            }
        };

        tarea.setOnSucceeded(evento -> {
            if (consulta == ultimaConsulta) {
                vista.mostrarRestaurantes(tarea.getValue());
            }
        });

        tarea.setOnFailed(evento -> {
            if (consulta == ultimaConsulta) {
                vista.mostrarError("No se pudieron cargar los restaurantes.");
                tarea.getException().printStackTrace();
            }
        });

        ejecutar(tarea);
    }

    private void cargarReportes(Restaurante restaurante) {
        long consulta = ++ultimaConsulta;

        Task<List<Reporte>> tarea = new Task<>() {
            @Override
            protected List<Reporte> call() throws Exception {
                return reporteDAO.obtenerPorRestaurante(
                        restaurante.getNombre()
                );
            }
        };

        tarea.setOnSucceeded(evento -> {
            System.out.println(
                    "Restaurante: " + restaurante.getNombre()
                    + " | Reportes encontrados: " + tarea.getValue().size()
            );

        if (consulta == ultimaConsulta) {
            vista.mostrarReportes(tarea.getValue());
        }
    });

        tarea.setOnFailed(evento -> {
            if (consulta == ultimaConsulta) {
                vista.mostrarError("No se pudieron cargar los reportes.");
                tarea.getException().printStackTrace();
            }
        });

        ejecutar(tarea);
    }

    private void ejecutar(Task<?> tarea) {
        Thread hilo = new Thread(tarea, "consulta-colaborador");
        hilo.setDaemon(true);
        hilo.start();
    }
    private void exportarCSV() {
    Restaurante restaurante = vista.getRestauranteSeleccionado();
    List<Reporte> reportes = vista.getReportesVisibles();

    if (restaurante == null || reportes.isEmpty()) {
        vista.mostrarError("Selecciona un restaurante con reportes.");
        return;
    }

    FileChooser selector = new FileChooser();
    selector.setTitle("Exportar reportes");
    selector.setInitialFileName("reportes.csv");
    selector.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Archivo CSV", "*.csv")
    );

    File archivo = selector.showSaveDialog(vista.getStage());

    if (archivo == null) {
        return;
    }

    DateTimeFormatter formato =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    StringBuilder csv = new StringBuilder(
            "restaurante,id_usuario,cantidad_personas,"
            + "tiempo_espera,fecha_hora\r\n"
    );

    for (Reporte reporte : reportes) {
        csv.append(escaparCSV(restaurante.getNombre())).append(",");

        if (reporte.getUsuario() != null) {
            csv.append(reporte.getUsuario().getID());
        }

        csv.append(",")
           .append(reporte.getCantidadPersonas()).append(",")
           .append(reporte.getTiempoEspera()).append(",")
           .append(reporte.getFechaHora() == null
                   ? "" : reporte.getFechaHora().format(formato))
           .append("\r\n");
    }

    try {
        Files.writeString(
                archivo.toPath(),
                csv.toString(),
                StandardCharsets.UTF_8
        );

        vista.mostrarMensaje(
                "Exportación completada",
                "El archivo CSV se guardó correctamente."
        );

    } catch (IOException excepcion) {
        vista.mostrarError("No fue posible guardar el archivo CSV.");
        excepcion.printStackTrace();
    }
}

    private String escaparCSV(String texto) {
        if (texto == null) {
            return "\"\"";
        }

        // Evita interpretar nombres como fórmulas al abrirlos en Excel.
        if (texto.stripLeading().matches("^[=+@-].*")
                || texto.startsWith("\t")
                || texto.startsWith("\r")
                || texto.startsWith("\n")) {
            texto = "'" + texto;
        }

        return "\"" + texto.replace("\"", "\"\"") + "\"";
    }
    private void eliminarReporte(Reporte reporte) {
        Restaurante restaurante = vista.getRestauranteSeleccionado();

        if (restaurante == null || reporte.getIdReporte() <= 0) {
            vista.mostrarError(
                    "No se pudo identificar el reporte. Actualiza la lista."
            );
            return;
        }

        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION,
                "¿Eliminar el reporte #" + reporte.getIdReporte()
                        + " de " + restaurante.getNombre()
                        + "?\nEsta acción no se puede deshacer.",
                ButtonType.CANCEL,
                ButtonType.OK
        );

        confirmacion.initOwner(vista.getStage());
        confirmacion.setTitle("Eliminar reporte");
        confirmacion.setHeaderText("Confirma la eliminación");

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL)
                != ButtonType.OK) {
            return;
        }

        Task<Boolean> tarea = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return reporteDAO.eliminar(
                        reporte.getIdReporte(),
                        restaurante.getNombre()
                );
            }
        };

        tarea.setOnSucceeded(evento -> {
            if (tarea.getValue()) {
                vista.mostrarMensaje(
                        "Reporte eliminado",
                        "El reporte se eliminó correctamente."
                );
            } else {
                vista.mostrarError(
                        "No se eliminó el reporte. Puede que ya no exista."
                );
            }

            Restaurante seleccionado = vista.getRestauranteSeleccionado();

            if (seleccionado != null) {
                cargarReportes(seleccionado);
            }
        });

        tarea.setOnFailed(evento -> {
            vista.mostrarError("No fue posible eliminar el reporte.");
            tarea.getException().printStackTrace();
        });

        ejecutar(tarea);
    }
}
