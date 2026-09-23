package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Proporciona conexiones a la base de datos local de UVGqueue. */
public final class Databaseconnection {

    private static final String URL =
            "jdbc:mariadb://localhost:3306/uvgqueue";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private Databaseconnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
