package com.example.pet_inventory.utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utility class to manage database connections.
 */
public class DBUtil {

    /**
     * Get a new connection to the database using properties from config file.
     *
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        Properties prop = new Properties();

        // Load database configuration from properties file
        try (InputStream is = DBUtil.class.getClassLoader()
                .getResourceAsStream("com/example/pet_inventory/config.properties")) {

            if (is == null) {
                throw new RuntimeException("Cannot find config.properties in resources");
            }
            prop.load(is);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load database configuration", e);
        }

        String url = prop.getProperty("db.url");
        String username = prop.getProperty("db.username");
        String password = prop.getProperty("db.password");

        // Establish and return database connection
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Could not connect to the database: " + e.getMessage(), e);
        }
    }
}