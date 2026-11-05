package dao;
import database.Databaseconnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import model.EstadoRestaurante;
import model.Restaurante;

public class RestauranteDAO {
    public ArrayList<Restaurante> obtenerTodos() {
        try (Connection connection = Databaseconnection.getConnection()) {
            Map<Integer, Integer> cierresPorRestaurante =
                    obtenerCierresDelDia(connection);
            ArrayList<Restaurante> restaurantes =
                    cargarRestaurantes(connection, cierresPorRestaurante);
            if (restaurantes.isEmpty()) {
                inicializarRestaurantesBase(connection);
                restaurantes = cargarRestaurantes(
                        connection, cierresPorRestaurante);
            }
            return restaurantes;
        } catch (SQLException e) {
            System.out.println(
                    "No fue posible obtener los restaurantes."
            );
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private ArrayList<Restaurante> cargarRestaurantes(
            Connection connection, Map<Integer, Integer> cierres)
            throws SQLException {
        ArrayList<Restaurante> restaurantes = new ArrayList<>();
        String sql = """
            SELECT id_restaurante, nombre, ubicacion,
                   hora_apertura, hora_cierre
            FROM restaurante
            ORDER BY nombre
            """;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id_restaurante");
                Time apertura = resultSet.getTime("hora_apertura");
                Time cierre = resultSet.getTime("hora_cierre");
                int cantidadCierres = cierres.getOrDefault(id, 0);
                EstadoRestaurante estado = cantidadCierres >= 3
                        ? EstadoRestaurante.CERRADO
                        : cantidadCierres >= 1
                                ? EstadoRestaurante.POSIBLEMENTE_CERRADO
                                : EstadoRestaurante.ABIERTO;

                restaurantes.add(new Restaurante(
                        resultSet.getString("nombre"),
                        resultSet.getString("ubicacion"),
                        estado,
                        apertura == null ? null : apertura.toLocalTime(),
                        cierre == null ? null : cierre.toLocalTime()));
            }
        }
        return restaurantes;
    }

    private void inicializarRestaurantesBase(Connection connection)
            throws SQLException {
        String sql = """
            INSERT IGNORE INTO restaurante
                (nombre, ubicacion, estado, hora_apertura, hora_cierre)
            VALUES
                ('Gitane', 'Cafetería CIT', 'ABIERTO', '06:00:00', '23:59:59'),
                ('GoGreen', 'Cafetería CIT', 'ABIERTO', '06:00:00', '23:59:59'),
                ('Mixtas Frankfurt', 'Cafetería CIT', 'ABIERTO', '06:00:00', '23:59:59'),
                ('Panitos', 'Cafetería CIT', 'ABIERTO', '06:00:00', '23:59:59'),
                ('&Cafe', 'CIT 6', 'ABIERTO', '06:00:00', '23:59:59'),
                ('Barista', 'Patio CIT', 'ABIERTO', '06:00:00', '23:59:59'),
                ('Golden Harvest', 'Puerta 6', 'ABIERTO', '06:00:00', '23:59:59'),
                ('Sarita', 'Edificio F', 'ABIERTO', '06:00:00', '23:59:59')
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
    }

    private Map<Integer, Integer> obtenerCierresDelDia(Connection connection) {
        Map<Integer, Integer> cierres = new HashMap<>();
        String sql = """
            SELECT id_restaurante, COUNT(DISTINCT id_usuario) AS cantidad
            FROM reporte_cierre
            WHERE confirmacion = 'CERRADO'
              AND fecha_hora >= CURRENT_DATE()
              AND fecha_hora < CURRENT_DATE() + INTERVAL 1 DAY
            GROUP BY id_restaurante
            """;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                cierres.put(resultSet.getInt("id_restaurante"),
                        resultSet.getInt("cantidad"));
            }
        } catch (SQLException e) {
            System.out.println(
                    "No se pudieron consultar los cierres; se mostrarán los restaurantes.");
        }
        return cierres;
    }
}
