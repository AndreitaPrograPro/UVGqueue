package model;

import java.math.BigDecimal;

/** Un producto del menú. Un precio null significa que todavía no está publicado. */
public record ElementoMenu(long id, String nombre, String tipo1, String tipo2,
                           BigDecimal precio) {
}
