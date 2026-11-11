package dao;
import database.Databaseconnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Restaurante;
import model.Usuario;

public class ReporteCierreDAO {

    public boolean registrarVerificacion(
            Usuario usuario,
            Restaurante restaurante,
            boolean estaCerrado) {

        String buscarRestaurante = """
            SELECT id_restaurante
            FROM restaurante
            WHERE nombre = ?
            """;

        String buscarReporte = """
            SELECT id_reporte_cierre
            FROM reporte_cierre
            WHERE id_usuario = ?
              AND id_restaurante = ?
            FOR UPDATE
            """;

        String insertar = """
            INSERT INTO reporte_cierre
                (id_usuario, id_restaurante,
                 confirmacion, fecha_hora)
            VALUES (?, ?, ?, NOW())
            """;

        String actualizar = """
            UPDATE reporte_cierre
            SET confirmacion = ?,
                fecha_hora = NOW()
            WHERE id_usuario = ?
              AND id_restaurante = ?
            """;

        try (Connection connection =
                Databaseconnection.getConnection()) {

            connection.setAutoCommit(false);

            try {

                int idRestaurante = -1;

                // Buscar el restaurante en MySQL
                try (PreparedStatement statement =
                        connection.prepareStatement(
                                buscarRestaurante)) {

                    statement.setString(
                            1, restaurante.getNombre()
                    );

                    try (ResultSet resultado =
                            statement.executeQuery()) {

                        if (resultado.next()) {
                            idRestaurante = resultado.getInt(
                                    "id_restaurante"
                            );
                        }
                    }
                }

                if (idRestaurante == -1) {
                    connection.rollback();
                    return false;
                }

                boolean existeReporte = false;

                try (PreparedStatement statement =
                        connection.prepareStatement(
                                buscarReporte)) {

                    statement.setInt(1, usuario.getID());
                    statement.setInt(2, idRestaurante);

                    try (ResultSet resultado =
                            statement.executeQuery()) {

                        existeReporte = resultado.next();
                    }
                }

                String confirmacion =
                        estaCerrado ? "CERRADO" : "ABIERTO";

                if (existeReporte) {

                    try (PreparedStatement statement =
                            connection.prepareStatement(
                                    actualizar)) {

                        statement.setString(1, confirmacion);
                        statement.setInt(2, usuario.getID());
                        statement.setInt(3, idRestaurante);

                        statement.executeUpdate();
                    }

                } else {

                    try (PreparedStatement statement =
                            connection.prepareStatement(
                                    insertar)) {

                        statement.setInt(1, usuario.getID());
                        statement.setInt(2, idRestaurante);
                        statement.setString(3, confirmacion);

                        statement.executeUpdate();
                    }
                }

                connection.commit();
                return true;

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {

            System.out.println(
                    "No fue posible registrar la verificacion."
            );

            e.printStackTrace();
            return false;
        }
    }
}