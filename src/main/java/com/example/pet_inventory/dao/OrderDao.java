package com.example.pet_inventory.dao;

import com.example.pet_inventory.models.OrderItem;
import com.example.pet_inventory.utils.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderDao {

    public void addOrder(BigDecimal totalAmount, String paymentMethod) throws SQLException {
        String orderID = UUID.randomUUID().toString();

        String query = "INSERT INTO Orders(orderID, dateCreated, totalAmount, paymentMethod) VALUES (?, ?, ?, ?)";

        Connection myCon = DBUtil.getConnection();
        PreparedStatement myStm = myCon.prepareStatement(query);

        myStm.setString(1, orderID);
        myStm.setDate(2, Date.valueOf(LocalDate.now()));
        myStm.setBigDecimal(3, totalAmount);
        myStm.setString(4, paymentMethod);

        myStm.executeUpdate();

    }
}
