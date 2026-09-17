package dao;

import database.Databaseconnection;
import model.Restaurante;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class RestauranteDAO {

    public List<Restaurante> obtenerTodos() {

        List<Restaurante> restaurantes = new ArrayList<>();

        String sql = "SELECT * FROM restaurante";

        try (
            Connection connection = Databaseconnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {

                Restaurante restaurante = new Restaurante(
                    resultSet.getInt("id_restaurante"),
                    resultSet.getString("nombre"),
                    resultSet.getString("ubicacion"),
                    resultSet.getTime("hora_apertura").toLocalTime(),
                    resultSet.getTime("hora_cierre").toLocalTime(),
                    resultSet.getBoolean("activo")
                );

                restaurantes.add(restaurante);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return restaurantes;
    }
    public Restaurante buscarPorId(int idRestaurante) {

        String sql = """
                SELECT *
                FROM restaurante
                WHERE id_restaurante = ?
                """;

        try (
            Connection connection = Databaseconnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, idRestaurante);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    return new Restaurante(
                        resultSet.getInt("id_restaurante"),
                        resultSet.getString("nombre"),
                        resultSet.getString("ubicacion"),
                        resultSet.getTime("hora_apertura").toLocalTime(),
                        resultSet.getTime("hora_cierre").toLocalTime(),
                        resultSet.getBoolean("activo")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}