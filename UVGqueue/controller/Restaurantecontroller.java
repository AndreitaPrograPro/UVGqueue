package controller;

import dao.RestauranteDAO;
import java.util.ArrayList;
import javax.swing.Timer;
import model.Restaurante;
import view.EstudianteFrame;

public class RestauranteController {

    private final EstudianteFrame estudianteFrame;
    private final RestauranteDAO restauranteDAO;
    private Timer temporizador;

    public RestauranteController(
            EstudianteFrame estudianteFrame) {

        this.estudianteFrame = estudianteFrame;
        this.restauranteDAO = new RestauranteDAO();

        registrarEventos();
        cargarRestaurantes();
        iniciarActualizacionAutomatica();
    }

    private void registrarEventos() {

        estudianteFrame.getBotonActualizar()
                .addActionListener(
                        evento -> cargarRestaurantes()
                );
    }

    public void cargarRestaurantes() {

        ArrayList<Restaurante> restaurantes =
                restauranteDAO.obtenerTodos();

        estudianteFrame.mostrarRestaurantes(
                restaurantes
        );
    }
    private void iniciarActualizacionAutomatica() {

        // Actualizar cada 60 segundos
        temporizador = new Timer(60000, evento -> {
            cargarRestaurantes();
        });

        temporizador.start();
    }
    public void detenerActualizacionAutomatica() {

        if (temporizador != null) {
            temporizador.stop();
        }
    }
}
