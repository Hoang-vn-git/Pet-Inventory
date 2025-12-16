package com.example.pet_inventory.controller;

import com.example.pet_inventory.dao.ProductDao;
import com.example.pet_inventory.models.Category;
import com.example.pet_inventory.models.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    @FXML
    public TextField txtProductName;
    @FXML
    public TextField txtUPC;
    @FXML
    public TextField txtQuantity;
    @FXML
    public TextField txtPrice;
    @FXML
    private ChoiceBox<String> choiceBox;

    String[] category = {"Dog Food", "Cat Food", "Dog Treat", "Cat Treat", "Accessory", "None"};

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
        ObservableList<Product> data = FXCollections.observableArrayList();
        while (rs.next()) {
            Product product = new Product(
                    rs.getString("productUPC"),
                    rs.getString("productName"),
                    Category.fromDb(rs.getString("productCategory")), // 🔥 QUAN TRỌNG
                    rs.getInt("productQuantity"),
                    rs.getBigDecimal("productPrice")
            );

            data.add(product);
        }
        tableInventory.setItems(data);
        rs.getStatement().getConnection().close();
    }

    @FXML
    private void search() throws SQLException {
        ProductDao productDao = new ProductDao();
        String productUPC = txtUPC.getText();
        String productName = txtProductName.getText();
        String productCategory = choiceBox.getValue();
        String productQuantityText = txtQuantity.getText();
        Integer productQuantity = null;

        if (productQuantityText != null && !productQuantityText.isBlank()) {
            productQuantity= Integer.parseInt(productQuantityText);
        }
        ResultSet rs = productDao.searchProducts(productName, productCategory, productUPC, productQuantity);


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
        rs.getStatement().getConnection().close();

    }

    @FXML
    public void clear(ActionEvent actionEvent) {
        txtProductName.clear();
        txtUPC.clear();
        choiceBox.setValue(null);
    }
    @FXML
    public void export(ActionEvent actionEvent) throws IOException, SQLException {

        ProductDao productDao = new ProductDao();
        ResultSet rs = productDao.displayProducts();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Products");

        /* ================= HEADER STYLE ================= */
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        /* ================= NORMAL CELL STYLE ================= */
        CellStyle textStyle = workbook.createCellStyle();
        textStyle.setBorderBottom(BorderStyle.THIN);
        textStyle.setBorderTop(BorderStyle.THIN);
        textStyle.setBorderLeft(BorderStyle.THIN);
        textStyle.setBorderRight(BorderStyle.THIN);

        /* ================= CENTER STYLE ================= */
        CellStyle centerStyle = workbook.createCellStyle();
        centerStyle.cloneStyleFrom(textStyle);
        centerStyle.setAlignment(HorizontalAlignment.CENTER);

        /* ================= PRICE STYLE ================= */
        CellStyle priceStyle = workbook.createCellStyle();
        priceStyle.cloneStyleFrom(textStyle);
        DataFormat format = workbook.createDataFormat();
        priceStyle.setDataFormat(format.getFormat("$#,##0.00"));

        /* ================= CREATE HEADER ================= */
        Row header = sheet.createRow(0);
        String[] headers = {"UPC", "Name", "Category", "Quantity", "Price"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        /* ================= WARNING STYLE (LOW STOCK) ================= */
        CellStyle warningStyle = workbook.createCellStyle();
        warningStyle.cloneStyleFrom(textStyle);
        warningStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        warningStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        warningStyle.setAlignment(HorizontalAlignment.CENTER);

        /* ================= DATA ================= */
        int rowNum = 1;
        while (rs.next()) {
            Row row = sheet.createRow(rowNum++);

            int quantity = rs.getInt("productQuantity");
            boolean lowStock = quantity <= 10;

            CellStyle rowStyle = lowStock ? warningStyle : textStyle;
            CellStyle qtyStyle = lowStock ? warningStyle : centerStyle;
            CellStyle priceCellStyle = lowStock ? warningStyle : priceStyle;

            Cell c0 = row.createCell(0);
            c0.setCellValue(rs.getString("productUPC"));
            c0.setCellStyle(rowStyle);

            Cell c1 = row.createCell(1);
            c1.setCellValue(rs.getString("productName"));
            c1.setCellStyle(rowStyle);

            Cell c2 = row.createCell(2);
            c2.setCellValue(rs.getString("productCategory"));
            c2.setCellStyle(rowStyle);

            Cell c3 = row.createCell(3);
            c3.setCellValue(quantity);
            c3.setCellStyle(qtyStyle);

            Cell c4 = row.createCell(4);
            c4.setCellValue(rs.getBigDecimal("productPrice").doubleValue());
            c4.setCellStyle(priceCellStyle);
        }
        /* ================= AUTO SIZE COLUMN ================= */
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        /* ================= FREEZE HEADER ================= */
        sheet.createFreezePane(0, 1);

        /* ================= WRITE FILE ================= */
        try (OutputStream fileOut = new FileOutputStream("products.xlsx")) {
            workbook.write(fileOut);
        }

        workbook.close();
    }
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    public void insert(ActionEvent actionEvent) {

        String productUPC = txtUPC.getText().trim();
        String productName = txtProductName.getText().trim();
        String productCategory = choiceBox.getValue();
        String quantityText = txtQuantity.getText().trim();
        String priceText = txtPrice.getText().trim();


        if (productUPC.isEmpty() ||
                productName.isEmpty() ||
                productCategory == null ||
                quantityText.isEmpty() ||
                priceText.isEmpty()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Missing Information",
                    "Please fill in all fields."
            );
            return;
        }

        // 3️⃣ Validate quantity
        int productQuantity;
        try {
            productQuantity = Integer.parseInt(quantityText);
            if (productQuantity < 0) {
                showAlert(
                        Alert.AlertType.WARNING,
                        "Invalid Quantity",
                        "Quantity must be a non-negative number."
                );
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Invalid Quantity",
                    "Quantity must be an integer."
            );
            return;
        }

        BigDecimal productPrice;
        try {
            productPrice = new BigDecimal(priceText);
            if (productPrice.compareTo(BigDecimal.ZERO) < 0) {
                showAlert(
                        Alert.AlertType.WARNING,
                        "Invalid Price",
                        "Price must be greater than or equal to 0."
                );
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Invalid Price",
                    "Price must be a valid number."
            );
            return;
        }

        // 5️⃣ Insert vào DB
        try {
            ProductDao productDao = new ProductDao();
            productDao.insertProduct(
                    new Product(
                            productUPC,
                            productName,
                            Category.fromDb(productCategory),
                            productQuantity,
                            productPrice
                    )
            );

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Success",
                    "Product inserted successfully!"
            );
         display();
        } catch (SQLException e) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Database Error",
                    e.getMessage()
            );
        }
    }
}
