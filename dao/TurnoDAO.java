package dao;

import database.Databaseconnection;
import model.Turno;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TurnoDAO {

    // Crear un nuevo turno
    public boolean crearTurno(Turno turno) {

        // Primero revisa si el usuario ya tiene un turno activo
        if (tieneTurnoActivo(
                turno.getIdFila(),
                turno.getIdUsuario())) {

            System.out.println("El usuario ya tiene un turno activo.");
            return false;
        }

        String sql = """
                INSERT INTO turno
                (id_fila, id_usuario, numero_turno, estado)
                VALUES (?, ?, ?, ?)
                """;

        try (
            Connection connection = Databaseconnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, turno.getIdFila());
            statement.setInt(2, turno.getIdUsuario());
            statement.setInt(3, turno.getNumeroTurno());
            statement.setString(4, turno.getEstado());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
}


    // Obtener el siguiente número disponible
    public int obtenerSiguienteNumero(int idFila) {

        String sql = """
                SELECT COALESCE(MAX(numero_turno), 0) + 1
                AS siguiente
                FROM turno
                WHERE id_fila = ?
                """;

        try (
            Connection connection = Databaseconnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, idFila);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt("siguiente");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 1;
    }


    // Obtener todos los turnos que siguen esperando
    public List<Turno> obtenerPendientes(int idFila) {

        List<Turno> turnos = new ArrayList<>();

        String sql = """
                SELECT *
                FROM turno
                WHERE id_fila = ?
                AND estado = 'ESPERANDO'
                ORDER BY numero_turno ASC
                """;

        try (
            Connection connection = Databaseconnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, idFila);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    Turno turno = new Turno(
                        resultSet.getInt("id_turno"),
                        resultSet.getInt("id_fila"),
                        resultSet.getInt("id_usuario"),
                        resultSet.getInt("numero_turno"),
                        resultSet.getString("estado"),
                        resultSet.getTimestamp("fecha_hora")
                                 .toLocalDateTime()
                    );

                    turnos.add(turno);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return turnos;
    }


    // Cambiar el estado de un turno
    public boolean cambiarEstado(int idTurno, String nuevoEstado) {

        String sql = """
                UPDATE turno
                SET estado = ?
                WHERE id_turno = ?
                """;

        try (
            Connection connection = Databaseconnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, nuevoEstado);
            statement.setInt(2, idTurno);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean tieneTurnoActivo(int idFila, int idUsuario) {

        String sql = """
                SELECT COUNT(*) AS cantidad
                FROM turno
                WHERE id_fila = ?
                AND id_usuario = ?
                AND estado IN ('ESPERANDO', 'LLAMADO')
                """;

        try (
            Connection connection = Databaseconnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, idFila);
            statement.setInt(2, idUsuario);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt("cantidad") > 0;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    public Turno obtenerSiguiente(int idFila) {

        String sql = """
                SELECT *
                FROM turno
                WHERE id_fila = ?
                AND estado = 'ESPERANDO'
                ORDER BY numero_turno ASC
                LIMIT 1
                """;

        try (
            Connection connection = Databaseconnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, idFila);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    return new Turno(
                        resultSet.getInt("id_turno"),
                        resultSet.getInt("id_fila"),
                        resultSet.getInt("id_usuario"),
                        resultSet.getInt("numero_turno"),
                        resultSet.getString("estado"),
                        resultSet.getTimestamp("fecha_hora")
                                .toLocalDateTime()
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    public int obtenerPosicion(int idFila, int idUsuario) {

        String sql = """
                SELECT COUNT(*) + 1 AS posicion
                FROM turno
                WHERE id_fila = ?
                AND estado = 'ESPERANDO'
                AND numero_turno < (
                    SELECT numero_turno
                    FROM turno
                    WHERE id_fila = ?
                    AND id_usuario = ?
                    AND estado = 'ESPERANDO'
                    LIMIT 1
                )
                """;

        try (
            Connection connection = Databaseconnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, idFila);
            statement.setInt(2, idFila);
            statement.setInt(3, idUsuario);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt("posicion");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
    public Turno buscarTurnoActivo(int idUsuario) {

        String sql = """
                SELECT *
                FROM turno
                WHERE id_usuario = ?
                AND estado IN ('ESPERANDO', 'LLAMADO')
                ORDER BY fecha_hora DESC
                LIMIT 1
                """;

        try (
            Connection connection = Databaseconnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, idUsuario);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    return new Turno(
                        resultSet.getInt("id_turno"),
                        resultSet.getInt("id_fila"),
                        resultSet.getInt("id_usuario"),
                        resultSet.getInt("numero_turno"),
                        resultSet.getString("estado"),
                        resultSet.getTimestamp("fecha_hora")
                                .toLocalDateTime()
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
