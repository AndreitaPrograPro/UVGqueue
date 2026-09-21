package dao;

import database.Databaseconnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.EstadoRestaurante;
import model.Restaurante;

public class RestauranteDAO {

    public ArrayList<Restaurante> obtenerTodos() {
        ArrayList<Restaurante> restaurantes =
                new ArrayList<>();

        String sql = """
                SELECT nombre, ubicacion, estado
                FROM restaurante
                ORDER BY nombre
                """;

        try (
            Connection connection =
                    Databaseconnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            ResultSet resultSet =
                    statement.executeQuery()
        ) {
            while (resultSet.next()) {
                String nombre =
                        resultSet.getString("nombre");

                String ubicacion =
                        resultSet.getString("ubicacion");

                EstadoRestaurante estado =
                        EstadoRestaurante.valueOf(
                                resultSet.getString("estado")
                        );

                Restaurante restaurante =
                        new Restaurante(
                                nombre,
                                ubicacion,
                                estado
                        );

                restaurantes.add(restaurante);
            }

        } catch (SQLException e) {
            System.out.println(
                    "No fue posible obtener los restaurantes."
            );
            e.printStackTrace();
        }

        return restaurantes;
    }
}