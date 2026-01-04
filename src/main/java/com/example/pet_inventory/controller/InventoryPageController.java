package com.example.pet_inventory.controller;

import com.example.pet_inventory.dao.ProductDao;
import com.example.pet_inventory.models.Category;
import com.example.pet_inventory.models.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controller for Inventory Page.
 * Handles product CRUD, search, import/export Excel.
 */
public class InventoryPageController {

    // ==================== FXML Components ====================
    @FXML private TableView<Product> tableInventory;
    @FXML private TableColumn<Product, String> productUPC;
    @FXML private TableColumn<Product, String> productName;
    @FXML private TableColumn<Product, Category> productCategory;
    @FXML private TableColumn<Product, BigDecimal> productPrice;
    @FXML private TableColumn<Product, Integer> productQuantity;

    @FXML private TextField txtUPC;
    @FXML private TextField txtProductName;
    @FXML private TextField txtQuantity;
    @FXML private TextField txtPrice;
    @FXML private ChoiceBox<String> choiceBox;

    @FXML private Button btnInsert;
    @FXML private Button btnEdit;
    @FXML private Button btnRemove;
    @FXML private Button btnSearch;
    @FXML private Button btnClear;
    @FXML private Button btnExport;
    @FXML private Button btnSave;
    @FXML private Button btnImport;

    // Categories for ChoiceBox
    private final String[] categories = {"Dog Food", "Cat Food", "Dog Treat", "Cat Treat", "Accessory", "None"};

    // ==================== Initialize ====================
    public void initialize() {
        // Set choice box options
        choiceBox.getItems().addAll(categories);

        // Bind TableView columns to Product properties
        productUPC.setCellValueFactory(cell -> cell.getValue().productUPCProperty());
        productName.setCellValueFactory(cell -> cell.getValue().productNameProperty());
        productCategory.setCellValueFactory(cell -> cell.getValue().productCategoryProperty());
        productPrice.setCellValueFactory(cell -> cell.getValue().priceProperty());
        productQuantity.setCellValueFactory(cell -> cell.getValue().productQuantityPropertyObservable().asObject());

        // Display products
        try { display(); }
        catch (SQLException e) { e.printStackTrace(); }

        // Populate fields when selecting a product
        tableInventory.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> { if (newSelection != null) populateFields(newSelection); });

        btnSearch.setDefaultButton(true);
    }

    // ==================== CRUD Operations ====================
    /** Display all products from DB */
    private void display() throws SQLException {
        ProductDao productDao = new ProductDao();
        ResultSet rs = productDao.displayProducts();
        ObservableList<Product> data = FXCollections.observableArrayList();

        while (rs.next()) {
            data.add(new Product(
                    rs.getString("productUPC"),
                    rs.getString("productName"),
                    Category.fromDb(rs.getString("productCategory")),
                    rs.getInt("productQuantity"),
                    rs.getBigDecimal("productPrice")
            ));
        }
        tableInventory.setItems(data);
        rs.getStatement().getConnection().close();
    }

    /** Search products by filters */
    @FXML
    private void search() throws SQLException {
        ProductDao productDao = new ProductDao();
        String upc = txtUPC.getText();
        String name = txtProductName.getText();
        String category = choiceBox.getValue();
        Integer quantity = null;
        if (!txtQuantity.getText().isBlank()) quantity = Integer.parseInt(txtQuantity.getText());

        ResultSet rs = productDao.searchProducts(name, category, upc, quantity);
        tableInventory.getItems().clear();
        while (rs.next()) {
            tableInventory.getItems().add(new Product(
                    rs.getString("productUPC"),
                    rs.getString("productName"),
                    Category.fromDb(rs.getString("productCategory")),
                    rs.getInt("productQuantity"),
                    rs.getBigDecimal("productPrice")
            ));
        }
        rs.getStatement().getConnection().close();
    }

    /** Insert new product into DB */
    @FXML
    public void insert(ActionEvent event) {
        String upc = txtUPC.getText().trim();
        String name = txtProductName.getText().trim();
        String categoryStr = choiceBox.getValue();
        String quantityText = txtQuantity.getText().trim();
        String priceText = txtPrice.getText().trim();

        // Validate input
        if (upc.isEmpty() || name.isEmpty() || categoryStr == null || categoryStr.equalsIgnoreCase("None")
                || quantityText.isEmpty() || priceText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all fields.");
            return;
        }

        int quantity; BigDecimal price;
        try { quantity = Integer.parseInt(quantityText); if(quantity<0) throw new NumberFormatException(); }
        catch(NumberFormatException e) { showAlert(Alert.AlertType.ERROR, "Invalid Quantity", "Quantity must be a non-negative integer."); return; }

        try { price = new BigDecimal(priceText); if(price.compareTo(BigDecimal.ZERO)<0) throw new NumberFormatException(); }
        catch(NumberFormatException e) { showAlert(Alert.AlertType.ERROR, "Invalid Price", "Price must be >= 0."); return; }

        // Insert into DB
        try {
            ProductDao dao = new ProductDao();
            dao.insertProduct(new Product(upc, name, Category.fromDb(categoryStr), quantity, price));
            showAlert(Alert.AlertType.INFORMATION, "Success", "Product inserted successfully!");
            display();
        } catch (SQLException e) { showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage()); }
    }

    /** Populate input fields from selected TableView row */
    private void populateFields(Product product) {
        txtUPC.setText(product.getProductUPC());
        txtProductName.setText(product.getProductName());
        choiceBox.setValue(product.getProductCategory().toString());
        txtQuantity.setText(String.valueOf(product.getQuantity()));
        txtPrice.setText(product.getPrice().toString());
    }
    /** CLear input */
    @FXML
    public void clear(ActionEvent actionEvent) {
        txtProductName.clear();
        txtUPC.clear();
        txtQuantity.clear();
        txtPrice.clear();
        choiceBox.setValue(null);
    }

    /** Edit selected product */
    @FXML
    public void edit(ActionEvent event) throws SQLException {
        Product selected = tableInventory.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(Alert.AlertType.WARNING, "No Selection", "Select a product to edit."); return; }

        String upc = txtUPC.getText().trim();
        String name = txtProductName.getText().trim();
        Category category = Category.fromDb(choiceBox.getValue());
        int quantity = Integer.parseInt(txtQuantity.getText().trim());
        BigDecimal price = new BigDecimal(txtPrice.getText().trim());

        if(upc.isEmpty() || name.isEmpty() || category == null) { showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all required fields"); return; }

        // Update DB
        ProductDao dao = new ProductDao();
        dao.updateProduct(upc, name, category, quantity, price);

        // Update TableView
        selected.setProductName(name);
        selected.setProductCategory(category);
        selected.setQuantity(quantity);
        selected.setPrice(price);
        tableInventory.refresh();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Product updated successfully!");
    }

    /** Remove selected product */
    @FXML
    public void remove(ActionEvent event) {
        Product selected = tableInventory.getSelectionModel().getSelectedItem();
        if(selected==null){ showAlert(Alert.AlertType.WARNING, "No Selection", "Select a product to remove."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete product: " + selected.getProductName() + "?");
        confirm.showAndWait().ifPresent(response -> {
            if(response==ButtonType.OK){
                try { new ProductDao().deleteProduct(selected.getProductUPC()); tableInventory.getItems().remove(selected);
                    showAlert(Alert.AlertType.INFORMATION, "Deleted", "Product removed successfully!"); }
                catch(SQLException e){ showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage()); }
            }
        });
    }

    // ==================== Excel ====================
    /** Export inventory to Excel */
    @FXML
    public void export(ActionEvent event) throws IOException, SQLException {
        ProductDao dao = new ProductDao();
        ResultSet rs = dao.displayProducts();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Products");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle textStyle = createTextStyle(workbook);
        CellStyle centerStyle = createCenterStyle(workbook);
        CellStyle priceStyle = createPriceStyle(workbook);

        // Header
        Row header = sheet.createRow(0);
        String[] headers = {"UPC","Name","Category","Quantity","Price"};
        for(int i=0;i<headers.length;i++){ Cell c = header.createCell(i); c.setCellValue(headers[i]); c.setCellStyle(headerStyle); }

        // Fill data
        int rowNum=1;
        while(rs.next()){
            Row row = sheet.createRow(rowNum++);
            int quantity = rs.getInt("productQuantity");
            boolean lowStock = quantity<=10;

            createCell(row,0,rs.getString("productUPC"),lowStock? textStyle:textStyle);
            createCell(row,1,rs.getString("productName"),lowStock? textStyle:textStyle);
            createCell(row,2,rs.getString("productCategory"),lowStock? textStyle:textStyle);
            createCell(row,3,quantity,centerStyle);
            createCell(row,4,rs.getBigDecimal("productPrice").doubleValue(),priceStyle);
        }

        for(int i=0;i<headers.length;i++){ sheet.autoSizeColumn(i); }
        sheet.createFreezePane(0,1);

        try(FileOutputStream fos=new FileOutputStream("products.xlsx")){ workbook.write(fos); }
        workbook.close();
        rs.getStatement().getConnection().close();
    }

    /** Import products from Excel */
    @FXML
    public void importExcel(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Excel File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files","*.xlsx"));
        File file = fileChooser.showOpenDialog(null);
        if(file==null) return;

        try(FileInputStream fis = new FileInputStream(file); Workbook workbook=new XSSFWorkbook(fis)){
            Sheet sheet = workbook.getSheetAt(0);
            ObservableList<Product> products = FXCollections.observableArrayList();

            for(int i=1;i<=sheet.getLastRowNum();i++){
                Row row = sheet.getRow(i); if(row==null) continue;
                String upc = row.getCell(0).getStringCellValue();
                String name = row.getCell(1).getStringCellValue();
                String categoryStr = row.getCell(2).getStringCellValue();
                int quantity = (int) row.getCell(3).getNumericCellValue();
                BigDecimal price = BigDecimal.valueOf(row.getCell(4).getNumericCellValue());
                products.add(new Product(upc,name,Category.fromDb(categoryStr),quantity,price));
            }
            tableInventory.setItems(products);
        }catch(IOException e){ showAlert(Alert.AlertType.ERROR,"Error","Failed to read Excel: "+e.getMessage()); }
    }

    /** Save all TableView products to DB */
    @FXML
    public void save(ActionEvent event) throws SQLException{
        ObservableList<Product> products = tableInventory.getItems();
        ProductDao dao = new ProductDao();
        for(Product p:products) dao.saveAll(p);
        showAlert(Alert.AlertType.INFORMATION,"Saved","All changes have been saved to the database.");
    }

    // ==================== Helper Methods ====================
    private void createCell(Row row,int col,Object value,CellStyle style){
        Cell cell = row.createCell(col);
        if(value instanceof String) cell.setCellValue((String)value);
        else if(value instanceof Integer) cell.setCellValue((Integer)value);
        else if(value instanceof Double) cell.setCellValue((Double)value);
        cell.setCellStyle(style);
    }

    private CellStyle createHeaderStyle(Workbook wb){
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont(); font.setBold(true); font.setFontHeightInPoints((short)14); style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER); style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex()); style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN); style.setBorderTop(BorderStyle.THIN); style.setBorderLeft(BorderStyle.THIN); style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    private CellStyle createTextStyle(Workbook wb){
        CellStyle style = wb.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN); style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN); style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    private CellStyle createCenterStyle(Workbook wb){
        CellStyle style = createTextStyle(wb); style.setAlignment(HorizontalAlignment.CENTER); return style;
    }
    private CellStyle createPriceStyle(Workbook wb){
        CellStyle style = createTextStyle(wb);
        DataFormat format = wb.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0.00"));
        return style;
    }

    private void showAlert(Alert.AlertType type,String title,String msg){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}