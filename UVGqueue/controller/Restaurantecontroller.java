package controller;

import dao.RestauranteDAO;
import java.util.ArrayList;
import model.Restaurante;
import view.EstudianteFrame;

public class RestauranteController{
    private final EstudianteFrame estudianteFrame;
    private final RestauranteDAO restauranteDAO;

    public RestauranteController(
            EstudianteFrame estudianteFrame
    ) {
        this.estudianteFrame = estudianteFrame;
        this.restauranteDAO = new RestauranteDAO();

        registrarEventos();
        cargarRestaurantes();
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
}