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
import java.time.LocalDateTime;

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
    public Map<String, ArrayList<Integer>> obtenerTiempos(LocalDateTime fechaInicio,LocalDateTime fechaFin) {
        Map<String, ArrayList<Integer>> tiempos =
                new HashMap<>();
        String sql = """
                SELECT
                    restaurante.nombre,
                    reporte.tiempo_espera
                FROM reporte
                INNER JOIN restaurante
                    ON reporte.id_restaurante =
                    restaurante.id_restaurante
                WHERE reporte.fecha_hora >= ?
                AND reporte.fecha_hora <= ?
                """;

        try (
            Connection connection =
                    Databaseconnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {
            statement.setTimestamp(
                    1,
                    Timestamp.valueOf(fechaInicio)
            );

            statement.setTimestamp(
                    2,
                    Timestamp.valueOf(fechaFin)
            );

            try (
                ResultSet resultSet =
                        statement.executeQuery()
            ) {
                while (resultSet.next()) {

                    String nombre =
                            resultSet.getString("nombre");

                    int tiempo =
                            resultSet.getInt(
                                    "tiempo_espera"
                            );

                    tiempos.computeIfAbsent(
                            nombre,
                            clave -> new ArrayList<>()
                    ).add(tiempo);
                }
            }

        } catch (SQLException excepcion) {

            System.out.println(
                    "No fue posible obtener los tiempos."
            );

            excepcion.printStackTrace();
        }

        return tiempos;
    }
    public ArrayList<Reporte> obtenerPorUsuario(Usuario usuario) {
        ArrayList<Reporte> reportes = new ArrayList<>();
        String sql = """
                SELECT
                    restaurante.nombre AS nombre_restaurante,
                    reporte.cantidad_personas,
                    reporte.tiempo_espera,
                    reporte.fecha_hora
                FROM reporte
                INNER JOIN restaurante
                    ON reporte.id_restaurante =
                    restaurante.id_restaurante
                WHERE reporte.id_usuario = ?
                ORDER BY reporte.fecha_hora DESC
                """;
        try (Connection connection =Databaseconnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1,usuario.getID());
            try (ResultSet resultSet =statement.executeQuery()
            ) {
                while (resultSet.next()) {
                    String nombreRestaurante = resultSet.getString("nombre_restaurante");
                    int cantidadPersonas =resultSet.getInt("cantidad_personas");
                    int tiempoEspera =resultSet.getInt("tiempo_espera");
                    Timestamp fecha =resultSet.getTimestamp("fecha_hora");
                    Reporte reporte = new Reporte(usuario,nombreRestaurante,cantidadPersonas,tiempoEspera,fecha.toLocalDateTime());
                    reportes.add(reporte);
                }
            }
        } catch (SQLException excepcion) {
            System.out.println("No fue posible obtener los reportes del usuario.");
            excepcion.printStackTrace();
        }
        return reportes;
    }

}
