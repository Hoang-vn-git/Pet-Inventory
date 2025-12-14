package com.example.pet_inventory.controller;

import com.example.pet_inventory.dao.ProductDao;
import com.example.pet_inventory.models.Category;
import com.example.pet_inventory.models.Product;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryPageController {

    @FXML
    public TableColumn<Product, String> productUPC;
    @FXML
    public TableColumn<Product, String> productName;
    @FXML
    public TableColumn<Product, Category> productCategory;
    @FXML
    public TableColumn<Product, BigDecimal> productPrice;
    @FXML
    public TableColumn<Product, Integer> productQuantity;
    @FXML
    public TableView<Product> tableInventory;


    @FXML private ChoiceBox<String> choiceBox;

    String[] category = {"Dog Food", "Cat Food", "Dog Treat", "Cat Treat", "Accessory"};

    public void initialize() {
        choiceBox.getItems().addAll(category);
        // bind columns
        productUPC.setCellValueFactory(
                new PropertyValueFactory<>("productUPC")
        );
        productName.setCellValueFactory(
                new PropertyValueFactory<>("productName")
        );
        productCategory.setCellValueFactory(
                new PropertyValueFactory<>("productCategory")
        );
        productPrice.setCellValueFactory(
                new PropertyValueFactory<>("price")
        );
        productQuantity.setCellValueFactory(
                new PropertyValueFactory<>("quantity")
        );
        try {
            display();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    };

    private void display() throws SQLException {
        ProductDao productDao = new ProductDao();
        ResultSet rs = productDao.displayProducts();

        tableInventory.getItems().clear();

        while (rs.next()) {
            Product product = new Product(
                    rs.getString("productUPC"),
                    rs.getString("productName"),
                    Category.fromDb(rs.getString("productCategory")), // 🔥 QUAN TRỌNG
                    rs.getInt("productQuantity"),
                    rs.getBigDecimal("productPrice")
            );

            tableInventory.getItems().add(product);
        }
    }




}
