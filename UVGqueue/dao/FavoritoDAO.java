package dao;

import database.Databaseconnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import model.Restaurante;
import model.Usuario;

public class FavoritoDAO {

    public boolean agregar(Usuario usuario, Restaurante restaurante) {
        String sql = "INSERT INTO favorito (id_usuario, id_restaurante) "
                + "SELECT ?, id_restaurante FROM restaurante WHERE nombre = ?";

        try (Connection connection = Databaseconnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, usuario.getID());
            statement.setString(2, restaurante.getNombre());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("No fue posible agregar el restaurante a favoritos.");
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(Usuario usuario, Restaurante restaurante) {
        String sql = "DELETE f FROM favorito f "
                + "INNER JOIN restaurante r ON r.id_restaurante = f.id_restaurante "
                + "WHERE f.id_usuario = ? AND r.nombre = ?";

        try (Connection connection = Databaseconnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, usuario.getID());
            statement.setString(2, restaurante.getNombre());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("No fue posible quitar el restaurante de favoritos.");
            e.printStackTrace();
            return false;
        }
    }

    public Set<String> obtenerNombres(Usuario usuario) {
        Set<String> nombres = new HashSet<>();
        String sql = "SELECT r.nombre FROM favorito f "
                + "INNER JOIN restaurante r ON r.id_restaurante = f.id_restaurante "
                + "WHERE f.id_usuario = ? ORDER BY r.nombre";

        try (Connection connection = Databaseconnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, usuario.getID());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    nombres.add(resultSet.getString("nombre"));
                }
            }
        } catch (SQLException e) {
            System.out.println("No fue posible obtener los favoritos.");
            e.printStackTrace();
        }

        return nombres;
    }

    public boolean esFavorito(Usuario usuario, Restaurante restaurante) {
        String sql = "SELECT 1 FROM favorito f "
                + "INNER JOIN restaurante r ON r.id_restaurante = f.id_restaurante "
                + "WHERE f.id_usuario = ? AND r.nombre = ?";

        try (Connection connection = Databaseconnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, usuario.getID());
            statement.setString(2, restaurante.getNombre());

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.out.println("No fue posible verificar el favorito.");
            e.printStackTrace();
            return false;
        }
    }
}
