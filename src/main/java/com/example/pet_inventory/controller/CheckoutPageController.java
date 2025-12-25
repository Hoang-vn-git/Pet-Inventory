package com.example.pet_inventory.controller;

import com.example.pet_inventory.dao.ProductDao;
import com.example.pet_inventory.models.OrderItem;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckoutPageController {
    @FXML
    public WebView webView;
    public WebEngine webEngine;
    @FXML
    public TextField txtUPC;
    @FXML
    public TableView<OrderItem> tableCart;
    @FXML
    public TableColumn<OrderItem, String> productName;
    @FXML
    public TableColumn<OrderItem, BigDecimal> productPrice;
    @FXML
    public TableColumn<OrderItem, Integer> productQuantity;
    @FXML
    public TableColumn<OrderItem, BigDecimal> subTotal;
    @FXML
    public TextField txtQuantity;
    @FXML
    public Label txtSubtotal;
    public TextField txtRemove;
    @FXML
    public Button btnCheckout;
    final private BigDecimal HST = new BigDecimal("0.13");
    private BigDecimal cashInput;

    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

    ObservableList<OrderItem> data = FXCollections.observableArrayList();
    private CashCheckoutController cashCheckoutController;

    public void initialize(){
        Platform.runLater(() -> {
            txtUPC.requestFocus();
        });
        // bind columns
        productName.setCellValueFactory(cell -> cell.getValue().productNameProperty()

        );
        productPrice.setCellValueFactory(
                cell -> cell.getValue().productPriceProperty()
        );
        productQuantity.setCellValueFactory(
                cell -> cell.getValue().productQuantityProperty().asObject()
        );
        subTotal.setCellValueFactory(
                cell -> cell.getValue().subTotalProperty()
        );
// Đảm bảo TableView dùng ObservableList
        if (tableCart.getItems() == null) {
            tableCart.setItems(FXCollections.observableArrayList());
        }

        // Listener cho việc thêm/xóa row
        tableCart.getItems().addListener((ListChangeListener<OrderItem>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (OrderItem item : change.getAddedSubList()) {
                        // Listener cho amount/subTotal của từng row
                        item.subTotalProperty().addListener((obs, oldVal, newVal) -> {
                            updateSubTotalLabel();
                        });
                    }
                }
            }
            // Cập nhật tổng khi thêm/xóa row
            updateSubTotalLabel();
        });
        webEngine = webView.getEngine();
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                renderReceipt();
            }
        });
    }

    public void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void processingScan() {
        ProductDao productDao = new ProductDao();
        data = tableCart.getItems();

        if (data == null) {
            data = FXCollections.observableArrayList();
        }

        try (ResultSet rs = productDao.searchProductByUPC(txtUPC.getText())) {

            // ❌ Không tìm thấy sản phẩm
            if (!rs.next()) {
                showError("Invalid UPC", "Product not found!");
                txtUPC.requestFocus();
                txtUPC.clear();
                return;
            }

            // ✅ Có sản phẩm
            do {
                String productUPC = rs.getString("productUPC");
                String productName = rs.getString("productName");
                BigDecimal productPrice = rs.getBigDecimal("productPrice");

                int quantity = 0;
               if (txtQuantity.getText().isEmpty()) {
                   quantity = 1;
               } else {
                   quantity = Integer.parseInt(txtQuantity.getText());
               }

                OrderItem product = new OrderItem(
                        new SimpleStringProperty("0001"),    // orderId
                        new SimpleStringProperty("0001"),    // customerId
                        new SimpleStringProperty(productUPC),
                        new SimpleStringProperty(productName),
                        new SimpleObjectProperty<BigDecimal>(productPrice),
                        new SimpleIntegerProperty(quantity),
                        new SimpleObjectProperty<BigDecimal>(productPrice.multiply(BigDecimal.valueOf(quantity)))
                );

                if (itemExists(productUPC, tableCart) == null){
                    data.add(product);
                } else {
                    OrderItem existingItem = itemExists(productUPC, tableCart);
                    existingItem.setProductQuantity(existingItem.getProductQuantity() + quantity);
                    existingItem.setSubTotal(existingItem.getProductPrice().multiply(BigDecimal.valueOf(existingItem.getProductQuantity())));
                }
            } while (rs.next());

            tableCart.setItems(data);
            txtUPC.clear();
            txtQuantity.clear();
            txtUPC.requestFocus();


        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database Error", "Unable to scan product.");
        }
    }

    public OrderItem itemExists(String productUPC, TableView<OrderItem> tableCart) {
        ObservableList<OrderItem> items = tableCart.getItems();
        for (OrderItem item : items) {
            if (item.getProductUPC().equals(productUPC)) {
                return item;
            }
        }
        return null;
    }

    public void scanProduct(){
        String productUPC = txtUPC.getText();
        Pattern pattern = Pattern.compile("[a-zA-Z0-9]{9}");
        Matcher matcher = pattern.matcher(productUPC);

        if (matcher.find()) {
            processingScan();
        } else {
            showError("Invalid UPC", "Product not found!");
        }
    }

    private BigDecimal updateSubTotalLabel() {
        BigDecimal subTotal = BigDecimal.ZERO;
        for (OrderItem item : tableCart.getItems()) {
            subTotal = subTotal.add(item.getSubTotal());
        }
        txtSubtotal.setText(currencyFormat.format(subTotal.setScale(2, BigDecimal.ROUND_HALF_UP)));
        return subTotal;
    }




    @FXML
    private void removeProductFromCart() {
        String productUPC = txtRemove.getText().trim();

        // 1. Validate input
        if (productUPC.isEmpty()) {
            showError("Invalid Input", "Please enter a product UPC to remove.");
            return;
        }
        // 2. Kiểm tra xem sản phẩm có trong cart không
        OrderItem itemToRemove = null;
        for (OrderItem item : tableCart.getItems()) {
            if (item.getProductUPC().equals(productUPC)) {
                itemToRemove = item;
                break;
            }
        }

        if (itemToRemove == null) {
            showError("Not Found", "Product with UPC " + productUPC + " not found in cart.");
            return;
        }

        // 3. Xóa sản phẩm
        tableCart.getItems().remove(itemToRemove);
        tableCart.getSelectionModel().clearSelection();
        txtRemove.clear();
    }


    @FXML
    private void showCheckoutModal(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pet_inventory/fxml/CheckoutModal.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.setTitle("Checkout");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(
                ((Node)event.getSource()).getScene().getWindow() );
        stage.setResizable(false);
        stage.show();
        // Communicate modal
        CheckoutModalController checkoutModalController = loader.getController();
        checkoutModalController.setCheckoutPageController(this);
        checkoutModalController.calcTaxTotal(updateSubTotalLabel());
    }

    // Print receipt
    public void printReceipt(BigDecimal cash){
        this.cashInput = cash;

        webEngine.load(
                getClass().getResource("/com/example/pet_inventory/web/receipt.html").toExternalForm()
        );
    }
    private void renderReceipt(){
        String date = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("MMM-dd-yyyy HH:mm:ss"));

        webEngine.executeScript(
                String.format("setHeader('Pet Uno', '675 College Street', '%s', 'Cash')", date)
        );

        BigDecimal subTotal = BigDecimal.ZERO;

        for (OrderItem item : tableCart.getItems()) {
            BigDecimal amount = item.getProductPrice()
                    .multiply(BigDecimal.valueOf(item.getProductQuantity()));

            webEngine.executeScript(
                    String.format("addItem('%s', %d, %.2f, %.2f)",
                            item.getProductName(),
                            item.getProductQuantity(),
                            item.getProductPrice(),
                            amount)
            );

            subTotal = subTotal.add(amount);
        }

        BigDecimal hst = subTotal.multiply(HST);
        BigDecimal total = subTotal.add(hst);

        BigDecimal rounded = total
                .divide(new BigDecimal("0.05"), 0, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("0.05"));

        webEngine.executeScript(
                String.format("setTotal(%.2f, %.2f, %.2f, %.2f, %.2f, %.2f, %.2f)",
                        subTotal,
                        hst,
                        rounded.subtract(total),
                        rounded,
                        total,
                        cashInput,
                        cashInput.subtract(rounded))
        );

        tableCart.getItems().clear();
    }

}
