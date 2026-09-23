package dao;
import database.Databaseconnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import model.*;
public class ReporteDAO {
    public boolean registrar(Reporte reporte, Restaurante restaurante){
        String sql = """
        INSERT INTO reporte
        (id_usuario, id_restaurante, cantidad_personas, 
        tiempo_espera, fecha_hora) SELECT ?, 
        id_restaurante, ?,?,? 
        FROM restaurante WHERE nombre = ?
        """;
        try(Connection connection = Databaseconnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, reporte.getUsuario().getID());
            statement.setInt(2,reporte.getCantidadPersonas());
            statement.setInt(3,reporte.getTiempoEspera());
            statement.setTimestamp(4, Timestamp.valueOf(reporte.getFechaHora()));
            statement.setString(5, restaurante.getNombre());
            return statement.executeUpdate()>0;
        }  catch (SQLException excepcion){
            System.out.println("No fue posible guardar el reporte");
            excepcion.printStackTrace();
            return false;
        }
        
    }
    public Map<String, ArrayList<Integer>> obtenerTiempos() {
        Map<String, ArrayList<Integer>> tiempos = new HashMap<>();
        String sql = "SELECT restaurante.nombre, reporte.tiempo_espera "
                + "FROM reporte "
                + "INNER JOIN restaurante ON reporte.id_restaurante = "
                + "restaurante.id_restaurante";
        try (
            Connection connection = Databaseconnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                String nombre = resultSet.getString("nombre");
                int tiempo = resultSet.getInt("tiempo_espera");

                tiempos.computeIfAbsent(
                        nombre,
                        clave -> new ArrayList<>()
                ).add(tiempo);
            }
        } catch (SQLException e) {
            System.out.println("No fue posible obtener los tiempos.");
            e.printStackTrace();
        }
        return tiempos;
    }
}
