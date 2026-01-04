package com.example.pet_inventory.controller;

import com.example.pet_inventory.dao.OrderItemDao;
import com.example.pet_inventory.models.History;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controller for History page.
 * Handles displaying and searching order history in TableView.
 */
public class HistoryController {

    // ==================== FXML UI Components ====================
    @FXML
    public TableColumn<History, String> orderID;
    @FXML
    public TableColumn<History, String> productName;
    @FXML
    public TableColumn<History, Integer> quantity;
    @FXML
    public TableColumn<History, BigDecimal> subtotal;
    @FXML
    public TableColumn<History, String> dateCreated;
    @FXML
    public TableColumn<History, String> paymentMethod;
    @FXML
    public TableView<History> historyTable;
    @FXML
    public TextField txtOrderID;

    // ==================== JavaFX Lifecycle ====================
    public void initialize() {
        // Bind TableView columns to History properties
        orderID.setCellValueFactory(c -> c.getValue().orderIDProperty());
        productName.setCellValueFactory(c -> c.getValue().productNameProperty());
        quantity.setCellValueFactory(c -> c.getValue().quantityProperty().asObject());
        subtotal.setCellValueFactory(c -> c.getValue().subtotalProperty());
        dateCreated.setCellValueFactory(c -> c.getValue().dateCreatedProperty());
        paymentMethod.setCellValueFactory(c -> c.getValue().paymentMethodProperty());

        // Ensure TableView has an observable list
        if (historyTable.getItems() == null) {
            historyTable.setItems(FXCollections.observableArrayList());
        }
    }

    // ==================== Search Operation ====================
    /**
     * Search for order history by Order ID.
     * Populates the TableView with matching results.
     */
    @FXML
    public void search(ActionEvent actionEvent) throws SQLException {
        String inputOrderID = txtOrderID.getText().trim();

        // Validate input
        if (inputOrderID.isEmpty()) {
            historyTable.getItems().clear();
            return; // No search if input is empty
        }

        OrderItemDao orderItemDao = new OrderItemDao();
        ResultSet rs = orderItemDao.searchOrder(inputOrderID);

        // Clear previous results
        historyTable.getItems().clear();

        // Populate table with results
        while (rs.next()) {
            History history = new History(
                    rs.getString("orderID"),
                    rs.getString("productName"),
                    rs.getInt("quantity"),
                    rs.getBigDecimal("subtotal"),
                    rs.getString("dateCreated"),
                    rs.getString("paymentMethod")
            );
            historyTable.getItems().add(history);
        }

        // Close database connection
        rs.getStatement().getConnection().close();
    }
}