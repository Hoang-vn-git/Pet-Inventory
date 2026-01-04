package com.example.pet_inventory.dao;

import com.example.pet_inventory.utils.DBUtil;
import com.example.pet_inventory.models.OrderItem;

import java.math.BigDecimal;
import java.sql.*;
import java.util.UUID;

/**
 * Data Access Object for Order Items.
 * Handles adding order items, searching by order, and exporting all order items.
 */
public class OrderItemDao {

    /**
     * Add a new order item to the database.
     *
     * @param orderID    ID of the order
     * @param productUPC UPC of the product
     * @param quantity   Quantity purchased
     * @param subtotal   Subtotal amount for this item
     * @throws SQLException if a database access error occurs
     */
    public void addOrderItem(String orderID, String productUPC, int quantity, BigDecimal subtotal) throws SQLException {
        String query = "INSERT INTO order_item (id, orderID, productUPC, quantity, subtotal) VALUES (?, ?, ?, ?, ?)";

        try (Connection myCon = DBUtil.getConnection();
             PreparedStatement myStm = myCon.prepareStatement(query)) {

            myStm.setString(1, UUID.randomUUID().toString());
            myStm.setString(2, orderID);
            myStm.setString(3, productUPC);
            myStm.setInt(4, quantity);
            myStm.setBigDecimal(5, subtotal);

            myStm.executeUpdate();
        }
    }

    /**
     * Search order items for a specific orderID.
     * Caller must close the ResultSet and underlying connection to free resources.
     *
     * @param orderID ID of the order to search
     * @return ResultSet containing order item details
     * @throws SQLException if a database access error occurs
     */
    public ResultSet searchOrder(String orderID) throws SQLException {
        String query = """
                SELECT
                    oi.orderID,
                    p.productName,
                    oi.quantity,
                    oi.subtotal,
                    o.dateCreated,
                    o.paymentMethod
                FROM pet.order_item oi
                         JOIN pet.orders o ON oi.orderID = o.orderID
                         JOIN pet.product p ON oi.productUPC = p.productUPC
                WHERE oi.orderID = ?;
                """;

        Connection myCon = DBUtil.getConnection();
        PreparedStatement myStm = myCon.prepareStatement(query);
        myStm.setString(1, orderID);

        return myStm.executeQuery();
    }

    /**
     * Export all order items.
     * Caller must close the ResultSet and underlying connection to free resources.
     *
     * @return ResultSet containing all order items with details
     * @throws SQLException if a database access error occurs
     */
    public ResultSet exportOrder() throws SQLException {
        String query = """
                SELECT
                    oi.orderID,
                    p.productName,
                    oi.quantity,
                    oi.subtotal,
                    o.dateCreated,
                    o.paymentMethod
                FROM pet.order_item oi
                         JOIN pet.orders o ON oi.orderID = o.orderID
                         JOIN pet.product p ON oi.productUPC = p.productUPC
                ORDER BY o.dateCreated ASC;
                """;

        Connection myCon = DBUtil.getConnection();
        PreparedStatement myStm = myCon.prepareStatement(query);

        return myStm.executeQuery();
    }
}