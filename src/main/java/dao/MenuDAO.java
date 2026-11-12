package dao;

import database.Databaseconnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.ElementoMenu;
import model.MenuRestaurante;

public class MenuDAO {
    public List<MenuRestaurante> obtenerMenus() throws SQLException {
        try (Connection connection = Databaseconnection.getConnection()) {
            return obtenerMenus(connection);
        }
    }

    /** La conexión recibida pertenece al llamador; permite probar el DAO sin tocar producción. */
    public List<MenuRestaurante> obtenerMenus(Connection connection) throws SQLException {
        String sql = """
                SELECT r.id_restaurante, r.nombre AS restaurante, r.ubicacion,
                       e.id_elemento_menu, e.nombre AS elemento, e.tipo_1, e.tipo_2, e.precio
                FROM restaurante r
                LEFT JOIN menu_elemento e ON e.id_restaurante = r.id_restaurante
                ORDER BY r.nombre, r.id_restaurante, e.id_elemento_menu
                """;
        Map<Integer, MenuRestaurante> restaurantes = new LinkedHashMap<>();
        Map<Integer, List<ElementoMenu>> productos = new LinkedHashMap<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setQueryTimeout(20);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_restaurante");
                    if (!restaurantes.containsKey(id)) {
                        String nombre = rs.getString("restaurante");
                        // No se renombra el registro de SQL, usado también por favoritos/reportes.
                        if (service.FiltroMenu.normalizar(nombre).replace(" ", "").equals("&cafe")) {
                            nombre = "& Café";
                        }
                        restaurantes.put(id, new MenuRestaurante(id, nombre,
                                rs.getString("ubicacion"), List.of()));
                        productos.put(id, new ArrayList<>());
                    }
                    long idElemento = rs.getLong("id_elemento_menu");
                    if (!rs.wasNull()) {
                        productos.get(id).add(new ElementoMenu(idElemento,
                                rs.getString("elemento"), rs.getString("tipo_1"),
                                rs.getString("tipo_2"), rs.getBigDecimal("precio")));
                    }
                }
            }
        }
        return restaurantes.values().stream().map(r -> new MenuRestaurante(
                r.idRestaurante(), r.nombre(), r.ubicacion(), productos.get(r.idRestaurante()))).toList();
    }
}
