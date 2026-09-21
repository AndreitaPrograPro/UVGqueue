package dao;
import database.*;
import model.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO{
    public boolean registrar(Usuario usuario){
        String sql = "INSERT INTO usuario(nombre, correo, contrasena_hash, tipo) VALUES(?,?,?,?)";
        try(Connection connection = Databaseconnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, usuario.getNombre());
            statement.setString(2, usuario.getCorreo());
            statement.setString(3, usuario.getContrasenaHash());
            statement.setString(4, usuario.getTipo().name());
            return statement.executeUpdate() >0;
        } catch (SQLException e) {
            System.out.println("No fue posible buscar al usuario");
            e.printStackTrace();
            return false;
        }
    }
    public Usuario buscarPorCorreo(String correo){
        String sql = "SELECT id_usuario, nombre, correo, contrasena_hash, tipo FROM usuario WHERE LOWER(correo) = LOWER(?)";
        try(Connection connection = Databaseconnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1,correo);
            try(ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()){
                    return crearUsuario(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("No fue posible buscar al usuario");
            e.printStackTrace();
        }
        return null;
    }
    public Usuario buscarPorNombre(String nombre){
        String sql ="SELECT id_usuario, nombre, correo, contrasena_hash, tipo FROM usuario WHERE LOWER(nombre) = LOWER(?)";
        try(Connection connection = Databaseconnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1,nombre);
            try(ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()){
                    return crearUsuario(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("No fue posible buscar al usuario");
            e.printStackTrace();    
        }
        return null;
    }
    private Usuario crearUsuario(ResultSet resultSet) throws SQLException{
        TipoUsuario tipo = TipoUsuario.valueOf(resultSet.getString("tipo"));
        return new Usuario(resultSet.getInt("id_usuario"),
    resultSet.getString("nombre"), resultSet.getString("correo"),
    resultSet.getString("contrasena_hash"),tipo);
    }
}