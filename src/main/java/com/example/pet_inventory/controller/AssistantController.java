package com.example.pet_inventory.controller;

import com.example.pet_inventory.dao.ProductDao;
import com.example.pet_inventory.models.Category;
import com.example.pet_inventory.models.Product;
import com.example.pet_inventory.models.ProductDTO;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.google.gson.Gson;
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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AssistantController {


    @FXML
    public TextArea txtAnswer;
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

    public void initialize() {

        productUPC.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getProductUPC()));

        productName.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getProductName()));

        productCategory.setCellValueFactory(c ->
                new SimpleObjectProperty<Category>(c.getValue().getProductCategory())
        );

        productQuantity.setCellValueFactory(c ->
                new SimpleIntegerProperty(c.getValue().getQuantity()).asObject());

        productPrice.setCellValueFactory(c ->
                new SimpleObjectProperty<BigDecimal>(c.getValue().getPrice()));

        numOfSold.setCellValueFactory(c ->
                new SimpleIntegerProperty(c.getValue().getNumOfSold()).asObject());

        handleJSON("""
       [
       {"numOfSold": 1, "productUPC": "UPC000052", "productName": "Henry", "productPrice": 100.00, "productCategory": "Accessory", "productQuantity": 0},
       {"numOfSold": null, "productUPC": "UPC000051", "productName": "Hoang", "productPrice": 100.00, "productCategory": "Cat Food", "productQuantity": 10}
       ]
    """);
    }
    public void submit(ActionEvent actionEvent) throws SQLException {
        ProductDao productDao = new ProductDao();
        Client client = new Client();

        ResultSet result = productDao.lowQuantityProduct();

        String jsonResult ="";
        while (result.next()) {
            jsonResult = result.getString("product_json");
        }
        String prompt = String.format("""
                From the JSON list below, list only products with quantity ≤ 20 in this format:
                
                1. Product UPC: ... | Product Name: ... | Product Category: ... | Product Quantity: ...
                ---------------
                2. ...
                ...
                Total: n products
                
                Number consecutively. Do not add explanations. JSON: %s
                """, jsonResult);
        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        "Top 5 dog food in Canada",
                        null);

        txtAnswer.setText(response.text());


    }

    public void handleJSON(String jsonString){
        Gson gson = new Gson();
        Type listType = new TypeToken<List<ProductDTO>>(){}.getType();
        List<ProductDTO> list =
                gson.fromJson(jsonString, listType);

        ObservableList<Product> items = FXCollections.observableArrayList();

        for (ProductDTO dto : list) {
            Product item = new Product(
                   dto.getProductUPC(),
                    dto.getProductName(),
                    dto.getProductCategory(),
                    dto.getProductQuantity(),
                    dto.getProductPrice(),
                    dto.getNumOfSold()
            );
            items.add(item);
        }
        tableResult.setItems(items);
    }

}
