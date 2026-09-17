package dao;

import database.Databaseconnection;
import model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    // Registrar un nuevo usuario
    public boolean registrar(Usuario usuario) {

        String sql = """
                INSERT INTO usuario
                (nombre, correo, contrasena, rol)
                VALUES (?, ?, ?, ?)
                """;

        try (
            Connection connection = Databaseconnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, usuario.getNombre());
            statement.setString(2, usuario.getCorreo());
            statement.setString(3, usuario.getContrasena());
            statement.setString(4, usuario.getRol());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Buscar usuario por correo
    public Usuario buscarPorCorreo(String correo) {

        String sql = """
                SELECT *
                FROM usuario
                WHERE correo = ?
                """;

        try (
            Connection connection = Databaseconnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, correo);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    return new Usuario(
                        resultSet.getInt("id_usuario"),
                        resultSet.getString("nombre"),
                        resultSet.getString("correo"),
                        resultSet.getString("contrasena"),
                        resultSet.getString("rol")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    // Validar login
    public Usuario iniciarSesion(String correo, String contrasena) {

        String sql = """
                SELECT *
                FROM usuario
                WHERE correo = ?
                AND contrasena = ?
                """;

        try (
            Connection connection = Databaseconnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, correo);
            statement.setString(2, contrasena);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    return new Usuario(
                        resultSet.getInt("id_usuario"),
                        resultSet.getString("nombre"),
                        resultSet.getString("correo"),
                        resultSet.getString("contrasena"),
                        resultSet.getString("rol")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}