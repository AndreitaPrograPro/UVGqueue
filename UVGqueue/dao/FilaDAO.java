package dao;

import database.Databaseconnection;
import model.Fila;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FilaDAO {

    // Crear una fila para un restaurante
    public boolean crearFila(Fila fila) {

        String sql = """
                INSERT INTO fila
                (id_restaurante, estado, fecha)
                VALUES (?, ?, ?)
                """;

        try (
            Connection connection = Databaseconnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, fila.getIdRestaurante());
            statement.setString(2, fila.getEstado());
            statement.setDate(
                3,
                java.sql.Date.valueOf(fila.getFecha())
            );

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Buscar una fila por su ID
    public Fila buscarPorId(int idFila) {

        String sql = """
                SELECT *
                FROM fila
                WHERE id_fila = ?
                """;

        try (
            Connection connection = Databaseconnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, idFila);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    return new Fila(
                        resultSet.getInt("id_fila"),
                        resultSet.getInt("id_restaurante"),
                        resultSet.getString("estado"),
                        resultSet.getDate("fecha").toLocalDate()
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    // Buscar la fila de un restaurante
    public Fila buscarPorRestaurante(int idRestaurante) {

        String sql = """
                SELECT *
                FROM fila
                WHERE id_restaurante = ?
                ORDER BY id_fila DESC
                LIMIT 1
                """;

        try (
            Connection connection = Databaseconnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, idRestaurante);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    return new Fila(
                        resultSet.getInt("id_fila"),
                        resultSet.getInt("id_restaurante"),
                        resultSet.getString("estado"),
                        resultSet.getDate("fecha").toLocalDate()
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    // Cambiar el estado: ABIERTA, PAUSADA o CERRADA
    public boolean cambiarEstado(int idFila, String nuevoEstado) {

        String sql = """
                UPDATE fila
                SET estado = ?
                WHERE id_fila = ?
                """;

        try (
            Connection connection = Databaseconnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, nuevoEstado);
            statement.setInt(2, idFila);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}