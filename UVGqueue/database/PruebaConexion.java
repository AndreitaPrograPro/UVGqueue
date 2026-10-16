package database;

import java.sql.*;

public class PruebaConexion {

    public static void main(String[] args) {
        try (
            Connection conexion = Databaseconnection.getConnection();
            Statement consulta = conexion.createStatement();
            ResultSet resultado = consulta.executeQuery(
                "SELECT COUNT(*) FROM restaurante"
            )
        ) {
            resultado.next();

            System.out.println("Conexión exitosa");
            System.out.println("Base: " + conexion.getCatalog());
            System.out.println("Restaurantes: " + resultado.getInt(1));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}