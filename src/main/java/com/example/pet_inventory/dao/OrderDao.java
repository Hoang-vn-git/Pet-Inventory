package com.example.pet_inventory.dao;

import com.example.pet_inventory.utils.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;

/**
 * Data Access Object for Orders table.
 * Handles adding new orders and searching orders by date range.
 */
public class OrderDao {

    /**
     * Add a new order to the database.
     *
     * @param orderID       Unique ID of the order
     * @param totalAmount   Total amount of the order
     * @param paymentMethod Payment method (e.g., Cash, Card)
     * @throws SQLException if a database access error occurs
     */
    public void addOrder(String orderID, BigDecimal totalAmount, String paymentMethod) throws SQLException {
        String query = "INSERT INTO Orders(orderID, dateCreated, totalAmount, paymentMethod) VALUES (?, ?, ?, ?)";
        try (Connection myCon = DBUtil.getConnection();
             PreparedStatement myStm = myCon.prepareStatement(query)) {

            myStm.setString(1, orderID);
            myStm.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            myStm.setBigDecimal(3, totalAmount);
            myStm.setString(4, paymentMethod);

            myStm.executeUpdate();
        }
    }

    /**
     * Search orders between two dates.
     * Note: The returned ResultSet must be closed by the caller to free resources.
     *
     * @param dateFrom Inclusive start date (format: YYYY-MM-DD)
     * @param dateTo   Exclusive end date (format: YYYY-MM-DD)
     * @return ResultSet containing matching orders
     * @throws SQLException if a database access error occurs
     */
    public ResultSet searchOrder(String dateFrom, String dateTo) throws SQLException {
        String query = """
                SELECT *
                FROM orders
                WHERE dateCreated >= ?
                  AND dateCreated < ?
                ORDER BY dateCreated ASC
                """;

        Connection myCon = DBUtil.getConnection();
        PreparedStatement myStm = myCon.prepareStatement(query);

        myStm.setString(1, dateFrom);
        myStm.setString(2, dateTo);

        // Caller is responsible for closing ResultSet and Connection
        return myStm.executeQuery();
    }
}