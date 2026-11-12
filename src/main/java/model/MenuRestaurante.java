package model;

import java.util.List;

/** Conserva el id real de restaurante; el nombre solo se usa para presentación. */
public record MenuRestaurante(int idRestaurante, String nombre, String ubicacion,
                              List<ElementoMenu> elementos) {
    public MenuRestaurante {
        elementos = List.copyOf(elementos);
    }
}
