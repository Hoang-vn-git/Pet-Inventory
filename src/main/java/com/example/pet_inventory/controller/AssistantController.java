package com.example.pet_inventory.controller;

import com.example.pet_inventory.dao.ProductDao;
import com.example.pet_inventory.models.Category;
import com.example.pet_inventory.models.Product;
import com.example.pet_inventory.models.ProductDTO;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AssistantController {

    // FXML UI components
    @FXML private TextArea txtQuery;
    @FXML private TextArea txtAnswer;
    @FXML private TableView<Product> tableResult;
    @FXML private TableColumn<Product, String> productUPC;
    @FXML private TableColumn<Product, String> productName;
    @FXML private TableColumn<Product, Category> productCategory;
    @FXML private TableColumn<Product, Integer> productQuantity;
    @FXML private TableColumn<Product, BigDecimal> productPrice;

    // Initialize TableView columns
    public void initialize() {
        productUPC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProductUPC()));
        productName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProductName()));
        productCategory.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getProductCategory()));
        productQuantity.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getQuantity()).asObject());
        productPrice.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getPrice()));
    }

    // Event: submit query to AI and display results
    @FXML
    public void submit(ActionEvent event) throws SQLException {
        ProductDao productDao = new ProductDao();
        Client client = new Client();

        // Retrieve products as JSON from DB
        ResultSet resultSet = productDao.JSONproduct();
        String productJson = "";
        while (resultSet.next()) {
            productJson = resultSet.getString("product_json");
        }

        // Compose AI prompt for Gemini
        String prompt = String.format("""
                Determine whether the task requires operating on the given product JSON list.

                Task:
                %s

                Product JSON:
                %s

                If the task requires using the JSON list and is related only to the product list:
                - Output ONLY a valid JSON list
                - No explanations
                - No markdown
                - No extra text

                Otherwise:
                - Answer the task normally, no JSON
                """, txtQuery.getText(), productJson);

        // Call AI
        GenerateContentResponse response = client.models.generateContent(
                "gemini-2.5-flash",
                prompt,
                null
        );

        // Try to parse JSON into TableView
        try {
            handleJsonResponse(response.text());
            txtAnswer.clear(); // clear any previous plain text
        } catch (Exception e) {
            tableResult.getItems().clear();
            txtAnswer.setText(response.text());
        }
    }

    // Helper: parse JSON list of ProductDTO and show in TableView
    private void handleJsonResponse(String json) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<ProductDTO>>() {}.getType();
        List<ProductDTO> dtoList = gson.fromJson(json, listType);

        ObservableList<Product> products = FXCollections.observableArrayList();
        for (ProductDTO dto : dtoList) {
            Product product = new Product(
                    dto.getProductUPC(),
                    dto.getProductName(),
                    dto.getProductCategory(),
                    dto.getProductQuantity(),
                    dto.getProductPrice()
            );
            products.add(product);
        }
        tableResult.setItems(products);
    }
}