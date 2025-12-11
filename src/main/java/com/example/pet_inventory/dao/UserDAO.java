package com.example.pet_inventory.dao;

import com.example.pet_inventory.utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    public ResultSet authUser(String username, String password) throws SQLException {
        String query = "SELECT userID FROM user WHERE userID = ? AND password = ?";

        Connection myCon = DBUtil.getConnection();
        PreparedStatement myStm = myCon.prepareStatement(query);

        myStm.setString(1, username);
        myStm.setString(2, password);

        return myStm.executeQuery();   // KHÔNG đóng ở đây
    }
}
