package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class Databaseconnection {

    private Databaseconnection() {
    }

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mariadb://" + requiredEnv("DB_HOST")
                + ":" + requiredEnv("DB_PORT")
                + "/" + requiredEnv("DB_NAME");

        Properties properties = new Properties();
        properties.setProperty("user", requiredEnv("DB_USER"));
        properties.setProperty("password", requiredEnv("DB_PASSWORD"));
        properties.setProperty("sslMode", "verify-full");
        properties.setProperty("connectTimeout", "10000");

        String caPath = System.getenv("DB_SSL_CA");
        if (caPath != null && !caPath.trim().isEmpty()) {
            properties.setProperty("serverSslCert", caPath);
        }

        return DriverManager.getConnection(url, properties);
    }

    private static String requiredEnv(String name) throws SQLException {
        String value = System.getenv(name);
        if (value == null || value.isEmpty()) {
            throw new SQLException("Falta configurar la variable de entorno " + name);
        }
        return value;
    }
}
