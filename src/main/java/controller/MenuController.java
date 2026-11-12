package controller;

import dao.MenuDAO;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.stage.WindowEvent;
import model.MenuRestaurante;
import view.EstudianteFrame;
import view.MenuView;

/** Carga SQL en segundo plano; las búsquedas no bloquean la ventana. */
public class MenuController {
    private static final Logger LOG = Logger.getLogger(MenuController.class.getName());
    private final MenuDAO dao = new MenuDAO();
    private final MenuView vista;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "fastqueue-menu");
        thread.setDaemon(true);
        return thread;
    });
    private Task<List<MenuRestaurante>> tarea;
    private boolean cerrado;

    public MenuController(EstudianteFrame frame) {
        vista = frame.getVistaMenu();
        frame.setAccionVerMenu(this::cargar);
        vista.setAccionActualizar(this::cargar);
        frame.getStage().addEventHandler(WindowEvent.WINDOW_HIDDEN, event -> {
            cerrado = true;
            if (tarea != null) tarea.cancel();
            executor.shutdownNow();
        });
    }

    public void cargar() {
        if (cerrado || (tarea != null && !tarea.isDone())) return;
        vista.mostrarCargando();
        Task<List<MenuRestaurante>> nueva = new Task<>() {
            @Override protected List<MenuRestaurante> call() throws Exception {
                return dao.obtenerMenus();
            }
        };
        tarea = nueva;
        nueva.setOnSucceeded(event -> {
            if (!cerrado && tarea == nueva) vista.mostrarMenus(nueva.getValue());
        });
        nueva.setOnFailed(event -> {
            LOG.log(Level.WARNING, "No se pudieron cargar los menús", nueva.getException());
            if (!cerrado && tarea == nueva) vista.mostrarError("No se pudieron cargar los menús. Intenta actualizar.");
        });
        executor.execute(nueva);
    }
}
