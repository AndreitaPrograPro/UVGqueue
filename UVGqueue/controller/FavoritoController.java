package controller;

import dao.FavoritoDAO;
import model.Restaurante;
import model.Usuario;
import view.EstudianteFrame;

public class FavoritoController {
    private final EstudianteFrame estudianteFrame;
    private final FavoritoDAO favoritoDAO;
    private final Usuario usuarioActivo;

    public FavoritoController(EstudianteFrame estudianteFrame, Usuario usuarioActivo) {
        this.estudianteFrame = estudianteFrame;
        this.usuarioActivo = usuarioActivo;
        this.favoritoDAO = new FavoritoDAO();
        registrarEventos();
        cargarFavoritos();
    }

    private void registrarEventos() {
        estudianteFrame.getBotonInicio().addActionListener(
                evento -> estudianteFrame.mostrarSoloFavoritos(false)
        );
        estudianteFrame.getBotonFavoritos().addActionListener(evento -> {
            cargarFavoritos();
            estudianteFrame.mostrarSoloFavoritos(true);
        });
        estudianteFrame.setAccionAlternarFavorito(this::alternarFavorito);
    }

    private void alternarFavorito(Restaurante restaurante) {
        boolean eraFavorito = favoritoDAO.esFavorito(usuarioActivo, restaurante);
        boolean actualizado = eraFavorito
                ? favoritoDAO.eliminar(usuarioActivo, restaurante)
                : favoritoDAO.agregar(usuarioActivo, restaurante);

        if (!actualizado) {
            estudianteFrame.mostrarError("No fue posible actualizar tus favoritos.");
            return;
        }

        cargarFavoritos();
        estudianteFrame.mostrarMensaje(
                "Favoritos",
                eraFavorito
                        ? "Restaurante eliminado de favoritos."
                        : "Restaurante agregado a favoritos."
        );
    }

    private void cargarFavoritos() {
        estudianteFrame.mostrarFavoritos(favoritoDAO.obtenerNombres(usuarioActivo));
    }
}
