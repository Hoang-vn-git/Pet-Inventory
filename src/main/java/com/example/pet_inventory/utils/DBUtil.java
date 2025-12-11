package com.example.pet_inventory.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {

    public static Connection getConnection() throws SQLException {
        // Reading configuration
        Properties prop = new Properties();

        try (FileInputStream file = new FileInputStream("src/main/java/com/example/pet_inventory/resources/config.properties")) {
            prop.load(file);
        } catch (IOException E) {
            E.printStackTrace();
        }
        // Get the database url, username and password from config.properties.
        String url = prop.getProperty("db.url");
        String username = prop.getProperty("db.username");
        String password = prop.getProperty("db.password");
        // Connecting to MySQL database.
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException E){
            E.printStackTrace();
            throw new SQLException("Could not connect to the database." + E.getMessage());
        }
    }
}
