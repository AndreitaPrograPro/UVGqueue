package dao;
import database.Databaseconnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;

import model.EstadoRestaurante;
import model.Restaurante;

public class RestauranteDAO {
    public ArrayList<Restaurante> obtenerTodos() {
        ArrayList<Restaurante> restaurantes = new ArrayList<>();

        String sql = """
            SELECT
                r.id_restaurante,
                r.nombre,
                r.ubicacion,
                r.hora_apertura,
                r.hora_cierre,

                COUNT(DISTINCT CASE
                    WHEN rc.confirmacion = 'CERRADO'
                    THEN rc.id_usuario
                    ELSE NULL
                END) AS cantidad_cierres

            FROM restaurante r

            LEFT JOIN reporte_cierre rc
                ON r.id_restaurante = rc.id_restaurante
                AND DATE(rc.fecha_hora) = CURRENT_DATE()

            GROUP BY
                r.id_restaurante,
                r.nombre,
                r.ubicacion,
                r.hora_apertura,
                r.hora_cierre

            ORDER BY r.nombre
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

                Time aperturaSQL =
                        resultSet.getTime("hora_apertura");

                Time cierreSQL =
                        resultSet.getTime("hora_cierre");

                LocalTime horaApertura =
                        aperturaSQL != null
                        ? aperturaSQL.toLocalTime()
                        : null;

                LocalTime horaCierre =
                        cierreSQL != null
                        ? cierreSQL.toLocalTime()
                        : null;

                int cantidadCierres =
                        resultSet.getInt("cantidad_cierres");

                EstadoRestaurante estado;

                if (cantidadCierres >= 3) {

                    estado = EstadoRestaurante.CERRADO;

                } else if (cantidadCierres >= 1) {

                    estado =
                            EstadoRestaurante.POSIBLEMENTE_CERRADO;

                } else {

                    estado = EstadoRestaurante.ABIERTO;
                }

                Restaurante restaurante = new Restaurante(
                        nombre,
                        ubicacion,
                        estado,
                        horaApertura,
                        horaCierre
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