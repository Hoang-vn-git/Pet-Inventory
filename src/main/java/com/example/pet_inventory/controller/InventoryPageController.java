package com.example.pet_inventory.controller;

import com.example.pet_inventory.dao.ProductDao;
import com.example.pet_inventory.models.Category;
import com.example.pet_inventory.models.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryPageController {

    // FXML UI components
    @FXML
    private TableColumn<Product, String> productUPC;
    @FXML
    private TableColumn<Product, String> productName;
    @FXML
    private TableColumn<Product, Category> productCategory;
    @FXML
    private TableColumn<Product, BigDecimal> productPrice;
    @FXML
    private TableColumn<Product, Integer> productQuantity;
    @FXML
    private TableView<Product> tableInventory;

    @FXML
    private TextField txtProductName;
    @FXML
    private TextField txtUPC;
    @FXML
    private TextField txtQuantity;
    @FXML
    private TextField txtPrice;

    @FXML
    private Button btnInsert;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnClear;
    @FXML
    private Button btnExport;

    @FXML
    private ChoiceBox<String> choiceBox;

    // Categories for ChoiceBox
    private final String[] categories = {"Dog Food", "Cat Food", "Dog Treat", "Cat Treat", "Accessory", "None"};

    // Initialize UI components
    public void initialize() {
        choiceBox.getItems().addAll(categories);

        // Bind columns to Product properties
        productUPC.setCellValueFactory(cell -> cell.getValue().productUPCProperty());
        productName.setCellValueFactory(cell -> cell.getValue().productNameProperty());
        productCategory.setCellValueFactory(cell -> cell.getValue().productCategoryProperty());
        productPrice.setCellValueFactory(cell -> cell.getValue().productPriceProperty());
        productQuantity.setCellValueFactory(cell -> cell.getValue().productQuantityProperty().asObject());

        try {
            display();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        btnSearch.setDefaultButton(true);
    }

    // Display all products in TableView
    private void display() throws SQLException {
        ProductDao productDao = new ProductDao();
        ResultSet rs = productDao.displayProducts();

        ObservableList<Product> data = FXCollections.observableArrayList();
        while (rs.next()) {
            Product product = new Product(
                    rs.getString("productUPC"),
                    rs.getString("productName"),
                    Category.fromDb(rs.getString("productCategory")),
                    rs.getInt("productQuantity"),
                    rs.getBigDecimal("productPrice"),
                    rs.getInt("numOfSold")
            );
            data.add(product);
        }

        tableInventory.setItems(data);
        rs.getStatement().getConnection().close();
    }

    // Search products based on criteria
    @FXML
    private void search() throws SQLException {
        ProductDao productDao = new ProductDao();

        String upc = txtUPC.getText();
        String name = txtProductName.getText();
        String category = choiceBox.getValue();
        Integer quantity = null;

        String quantityText = txtQuantity.getText();
        if (quantityText != null && !quantityText.isBlank()) {
            quantity = Integer.parseInt(quantityText);
        }

        ResultSet rs = productDao.searchProducts(name, category, upc, quantity);
        tableInventory.getItems().clear();

        while (rs.next()) {
            Product product = new Product(
                    rs.getString("productUPC"),
                    rs.getString("productName"),
                    Category.fromDb(rs.getString("productCategory")),
                    rs.getInt("productQuantity"),
                    rs.getBigDecimal("productPrice"),
                    rs.getInt("numOfSold")
            );
            tableInventory.getItems().add(product);
        }

        rs.getStatement().getConnection().close();
    }

    // Clear search inputs
    @FXML
    public void clear(ActionEvent actionEvent) {
        txtProductName.clear();
        txtUPC.clear();
        choiceBox.setValue(null);
    }

    // Export inventory to Excel
    @FXML
    public void export(ActionEvent actionEvent) throws IOException, SQLException {
        ProductDao productDao = new ProductDao();
        ResultSet rs = productDao.displayProducts();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Products");

        // Header style
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

        // Normal style
        CellStyle textStyle = workbook.createCellStyle();
        textStyle.setBorderBottom(BorderStyle.THIN);
        textStyle.setBorderTop(BorderStyle.THIN);
        textStyle.setBorderLeft(BorderStyle.THIN);
        textStyle.setBorderRight(BorderStyle.THIN);

        // Centered style
        CellStyle centerStyle = workbook.createCellStyle();
        centerStyle.cloneStyleFrom(textStyle);
        centerStyle.setAlignment(HorizontalAlignment.CENTER);

        // Price style
        CellStyle priceStyle = workbook.createCellStyle();
        priceStyle.cloneStyleFrom(textStyle);
        DataFormat format = workbook.createDataFormat();
        priceStyle.setDataFormat(format.getFormat("$#,##0.00"));

        // Warning style for low stock
        CellStyle warningStyle = workbook.createCellStyle();
        warningStyle.cloneStyleFrom(textStyle);
        warningStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        warningStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        warningStyle.setAlignment(HorizontalAlignment.CENTER);

        // Create header row
        Row header = sheet.createRow(0);
        String[] headers = {"UPC", "Name", "Category", "Quantity", "Price"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Fill data
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

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Freeze header
        sheet.createFreezePane(0, 1);

        // Write to file
        try (OutputStream fileOut = new FileOutputStream("products.xlsx")) {
            workbook.write(fileOut);
        }
        workbook.close();
    }

    // Show alert
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Insert new product
    @FXML
    public void insert(ActionEvent actionEvent) {
        String upc = txtUPC.getText().trim();
        String name = txtProductName.getText().trim();
        String categoryValue = choiceBox.getValue();
        String quantityText = txtQuantity.getText().trim();
        String priceText = txtPrice.getText().trim();

        if (upc.isEmpty() || name.isEmpty() || categoryValue == null || categoryValue.equalsIgnoreCase("None")
                || quantityText.isEmpty() || priceText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Please fill in all fields.");
            return;
        }

        // Validate quantity
        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
            if (quantity < 0) {
                showAlert(Alert.AlertType.WARNING, "Invalid Quantity", "Quantity must be a non-negative number.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Quantity", "Quantity must be an integer.");
            return;
        }

        // Validate price
        BigDecimal price;
        try {
            price = new BigDecimal(priceText);
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                showAlert(Alert.AlertType.WARNING, "Invalid Price", "Price must be greater than or equal to 0.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Price", "Price must be a valid number.");
            return;
        }

        // Insert product into DB
        try {
            ProductDao productDao = new ProductDao();
            productDao.insertProduct(new Product(
                    upc,
                    name,
                    Category.fromDb(categoryValue),
                    quantity,
                    price,
                    0
            ));

            showAlert(Alert.AlertType.INFORMATION, "Success", "Product inserted successfully!");
            display();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }
}