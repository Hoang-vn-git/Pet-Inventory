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
    @FXML
    private TextArea txtQuery;

    @FXML
    private TextArea txtAnswer;

    @FXML
    private TableView<Product> tableResult;

    @FXML
    private TableColumn<Product, String> productUPC;

    @FXML
    private TableColumn<Product, String> productName;

    @FXML
    private TableColumn<Product, Category> productCategory;

    @FXML
    private TableColumn<Product, Integer> productQuantity;

    @FXML
    private TableColumn<Product, BigDecimal> productPrice;

    @FXML
    private TableColumn<Product, Integer> numOfSold;

    // JavaFX lifecycle
    public void initialize() {
        productUPC.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getProductUPC()));

        productName.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getProductName()));

        productCategory.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().getProductCategory()));

        productQuantity.setCellValueFactory(c ->
                new SimpleIntegerProperty(c.getValue().getQuantity()).asObject());

        productPrice.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().getPrice()));

        numOfSold.setCellValueFactory(c ->
                new SimpleIntegerProperty(c.getValue().getNumOfSold()).asObject());
    }

    // Event handlers
    @FXML
    public void submit(ActionEvent event) throws SQLException {

        ProductDao productDao = new ProductDao();
        Client client = new Client();

        // Retrieve product list as JSON from database
        ResultSet resultSet = productDao.JSONproduct();
        String productJson = "";

        while (resultSet.next()) {
            productJson = resultSet.getString("product_json");
        }

        // Prompt designed to return either pure JSON or plain text
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
                """,
                txtQuery.getText(),
                productJson
        );

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        prompt,
                        null
                );

        // Try to parse JSON response into table
        // If parsing fails, treat response as normal text
        try {
            handleJsonResponse(response.text());
            txtAnswer.clear();
        } catch (Exception e) {
            tableResult.getItems().clear();
            txtAnswer.setText(response.text());
        }
    }

    // Helper methods
    /**
     * Parse JSON list of ProductDTO and display as Product objects in TableView.
     * Assumes the response is a valid JSON array.
     */
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
                    dto.getProductPrice(),
                    dto.getNumOfSold()
            );
            products.add(product);
        }

        tableResult.setItems(products);
    }
}