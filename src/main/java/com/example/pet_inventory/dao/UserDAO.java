package com.example.pet_inventory.dao;

import com.example.pet_inventory.utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for User authentication
 */
public class UserDAO {

    /**
     * Authenticate user by username and password.
     * Caller is responsible for closing ResultSet, Statement, and Connection.
     *
     * @param username User ID / username
     * @param password Password
     * @return ResultSet containing matching user, empty if none
     * @throws SQLException if DB operation fails
     */
    public ResultSet authUser(String username, String password) throws SQLException {
        String query = "SELECT userID FROM user WHERE userID = ? AND password = ?";

        Connection con = DBUtil.getConnection();
        PreparedStatement stmt = con.prepareStatement(query);

        stmt.setString(1, username);
        stmt.setString(2, password);

        return stmt.executeQuery(); // Caller must close ResultSet and connection
    }
}