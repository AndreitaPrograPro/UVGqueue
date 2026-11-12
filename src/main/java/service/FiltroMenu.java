package service;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import model.ElementoMenu;
import model.MenuRestaurante;

/** Búsqueda sobre la última lectura de SQL, sin una conexión por cada tecla. */
public final class FiltroMenu {
    private FiltroMenu() { }

    public static List<MenuRestaurante> aplicar(List<MenuRestaurante> menus, String consulta,
            BigDecimal minimo, BigDecimal maximo, boolean baratosPrimero) {
        if ((minimo != null && minimo.signum() < 0) || (maximo != null && maximo.signum() < 0)) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        if (minimo != null && maximo != null && minimo.compareTo(maximo) > 0) {
            throw new IllegalArgumentException("El precio mínimo no puede superar al máximo.");
        }
        String texto = normalizar(consulta);
        List<String> palabras = texto.isEmpty() ? List.of() : List.of(texto.split("\\s+"));
        boolean hayFiltros = hayFiltros(consulta, minimo, maximo);
        Comparator<ElementoMenu> porNombre = Comparator
                .comparing((ElementoMenu e) -> normalizar(e.nombre()))
                .thenComparing(e -> normalizar(e.tipo1()))
                .thenComparing(e -> normalizar(e.tipo2()))
                .thenComparingLong(ElementoMenu::id);
        Comparator<ElementoMenu> porPrecio = Comparator
                .comparing(ElementoMenu::precio, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(porNombre);

        List<MenuRestaurante> resultado = new ArrayList<>();
        for (MenuRestaurante menu : menus) {
            List<ElementoMenu> elementos = menu.elementos().stream().filter(e -> {
                String contenido = normalizar(e.nombre() + " " + e.tipo1() + " " + e.tipo2());
                if (!palabras.stream().allMatch(contenido::contains)) return false;
                if ((minimo != null || maximo != null) && e.precio() == null) return false;
                return (minimo == null || e.precio().compareTo(minimo) >= 0)
                        && (maximo == null || e.precio().compareTo(maximo) <= 0);
            }).sorted(baratosPrimero ? porPrecio : porNombre).toList();
            // Los restaurantes sin menú se muestran al explorar, pero no como resultados falsos.
            if (!elementos.isEmpty() || !hayFiltros) {
                resultado.add(new MenuRestaurante(menu.idRestaurante(), menu.nombre(),
                        menu.ubicacion(), elementos));
            }
        }
        Comparator<MenuRestaurante> porRestaurante = Comparator
                .comparing((MenuRestaurante m) -> normalizar(m.nombre()))
                .thenComparingInt(MenuRestaurante::idRestaurante);
        if (baratosPrimero) {
            // Se calcula después de filtrar, para que un extra ajeno a la búsqueda
            // no cambie el orden de los restaurantes en, por ejemplo, "Bebida".
            resultado.sort(Comparator.comparing(FiltroMenu::precioMasBajo,
                    Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(porRestaurante));
        } else {
            resultado.sort(porRestaurante);
        }
        return List.copyOf(resultado);
    }

    /**
     * Indica si la búsqueda por nombre/tipo o el rango de precio están en uso.
     * Un texto de solo espacios no cuenta como filtro. El orden no es un filtro.
     */
    public static boolean hayFiltros(String consulta, BigDecimal minimo, BigDecimal maximo) {
        return !normalizar(consulta).isEmpty() || minimo != null || maximo != null;
    }

    public static BigDecimal precioMasBajo(MenuRestaurante menu) {
        return menu.elementos().stream().map(ElementoMenu::precio)
                .filter(p -> p != null).min(BigDecimal::compareTo).orElse(null);
    }

    public static String normalizar(String texto) {
        if (texto == null) return "";
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT).strip();
    }
}
