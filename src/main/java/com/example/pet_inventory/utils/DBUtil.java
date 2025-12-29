package com.example.pet_inventory.utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {

    public static Connection getConnection() throws SQLException {

        Properties prop = new Properties();

        try (InputStream is = DBUtil.class
                .getClassLoader()
                .getResourceAsStream("com/example/pet_inventory/config.properties")) {

            if (is == null) {
                throw new RuntimeException("Cannot find config.properties in resources");
            }

            prop.load(is);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load database configuration");
        }

        String url = prop.getProperty("db.url");
        String username = prop.getProperty("db.username");
        String password = prop.getProperty("db.password");

        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Could not connect to the database: " + e.getMessage());
        }
    }
}