package controller;

import dao.RestauranteDAO;
import java.util.ArrayList;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.util.Duration;
import model.Restaurante;
import view.EstudianteFrame;

public class RestauranteController {
    private final EstudianteFrame estudianteFrame;
    private final RestauranteDAO restauranteDAO;
    private Timeline temporizador;
    public RestauranteController(
            EstudianteFrame estudianteFrame) {
        this.estudianteFrame = estudianteFrame;
        this.restauranteDAO = new RestauranteDAO();
        registrarEventos();
        cargarRestaurantes();
        iniciarActualizacionAutomatica();
    }
    private void registrarEventos() {

        estudianteFrame.getBotonActualizar().addEventHandler(
                ActionEvent.ACTION, evento -> cargarRestaurantes());
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
        temporizador = new Timeline(new KeyFrame(
                Duration.seconds(60), evento -> cargarRestaurantes()));
        temporizador.setCycleCount(Timeline.INDEFINITE);
        temporizador.play();
    }
    public void detenerActualizacionAutomatica() {

        if (temporizador != null) {
            temporizador.stop();
        }
    }
}
