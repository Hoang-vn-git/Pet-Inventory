package com.example.pet_inventory.controller;

import com.example.pet_inventory.dao.OrderDao;
import com.example.pet_inventory.dao.OrderItemDao;
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
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controller for Checkout Page.
 * Handles scanning products, managing cart, and printing receipts.
 */
public class CheckoutPageController {

    // ==================== FXML UI Components ====================
    @FXML
    private WebView webView;
    private WebEngine webEngine;

    @FXML
    private TextField txtUPC;
    @FXML
    private TableView<OrderItem> tableCart;
    @FXML
    private TableColumn<OrderItem, String> productName;
    @FXML
    private TableColumn<OrderItem, BigDecimal> productPrice;
    @FXML
    private TableColumn<OrderItem, Integer> productQuantity;
    @FXML
    private TableColumn<OrderItem, BigDecimal> subTotal;
    @FXML
    private TextField txtQuantity;
    @FXML
    private Label txtSubtotal;
    @FXML
    private TextField txtRemove;
    @FXML
    private Button btnCheckout;

    // ==================== Constants ====================
    private final BigDecimal HST = new BigDecimal("0.13");
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

    // ==================== Internal State ====================
    private BigDecimal cashInput;
    private ObservableList<OrderItem> data = FXCollections.observableArrayList();

    // ==================== JavaFX Lifecycle ====================
    public void initialize() {
        Platform.runLater(() -> txtUPC.requestFocus());

        // Bind TableView columns
        productName.setCellValueFactory(c -> c.getValue().productNameProperty());
        productPrice.setCellValueFactory(c -> c.getValue().productPriceProperty());
        productQuantity.setCellValueFactory(c -> c.getValue().productQuantityProperty().asObject());
        subTotal.setCellValueFactory(c -> c.getValue().subTotalProperty());

        // Ensure TableView has observable list
        if (tableCart.getItems() == null) {
            tableCart.setItems(FXCollections.observableArrayList());
        }

        // Listen for changes to update subtotal
        tableCart.getItems().addListener((ListChangeListener<OrderItem>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (OrderItem item : change.getAddedSubList()) {
                        item.subTotalProperty().addListener((obs, oldVal, newVal) -> updateSubTotalLabel());
                    }
                }
            }
            updateSubTotalLabel();
        });

        // Setup WebView engine
        webEngine = webView.getEngine();
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                try {
                    renderReceipt();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    // ==================== Scan & Cart Operations ====================

    // Scan product by UPC and add/update cart
    public void processingScan() {
        ProductDao productDao = new ProductDao();
        data = tableCart.getItems();
        if (data == null) data = FXCollections.observableArrayList();

        try (ResultSet rs = productDao.searchProductByUPC(txtUPC.getText())) {
            if (!rs.next()) {
                showError("Invalid UPC", "Product not found!");
                txtUPC.requestFocus();
                txtUPC.clear();
                return;
            }

            do {
                String productUPC = rs.getString("productUPC");
                String name = rs.getString("productName");
                BigDecimal price = rs.getBigDecimal("productPrice");
                int quantity = txtQuantity.getText().isEmpty() ? 1 : Integer.parseInt(txtQuantity.getText());

                OrderItem product = new OrderItem(
                        new SimpleStringProperty("0001"), // temporary checkout ID
                        new SimpleStringProperty("0001"), // temporary order ID
                        new SimpleStringProperty(productUPC),
                        new SimpleStringProperty(name),
                        new SimpleObjectProperty<>(price),
                        new SimpleIntegerProperty(quantity),
                        new SimpleObjectProperty<>(price.multiply(BigDecimal.valueOf(quantity)))
                );

                OrderItem existing = itemExists(productUPC, tableCart);
                if (existing == null) {
                    data.add(product);
                } else {
                    existing.setProductQuantity(existing.getProductQuantity() + quantity);
                    existing.setSubTotal(existing.getProductPrice()
                            .multiply(BigDecimal.valueOf(existing.getProductQuantity())));
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

    // Check if item exists in the cart
    public OrderItem itemExists(String productUPC, TableView<OrderItem> table) {
        for (OrderItem item : table.getItems()) {
            if (item.getProductUPC().equals(productUPC)) return item;
        }
        return null;
    }

    // Triggered by Scan button
    public void scanProduct() {
        String productUPC = txtUPC.getText();
        Matcher matcher = Pattern.compile("[a-zA-Z0-9]{9}").matcher(productUPC);
        if (matcher.find()) {
            processingScan();
        } else {
            showError("Invalid UPC", "Product not found!");
        }
    }

    // Remove product from cart
    @FXML
    private void removeProductFromCart() {
        String productUPC = txtRemove.getText().trim();
        if (productUPC.isEmpty()) {
            showError("Invalid Input", "Please enter a product UPC to remove.");
            return;
        }

        OrderItem itemToRemove = itemExists(productUPC, tableCart);
        if (itemToRemove == null) {
            showError("Not Found", "Product with UPC " + productUPC + " not found in cart.");
            return;
        }

        tableCart.getItems().remove(itemToRemove);
        tableCart.getSelectionModel().clearSelection();
        txtRemove.clear();
    }

    // ==================== Checkout & Receipt ====================

    // Show checkout modal
    @FXML
    private void showCheckoutModal(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pet_inventory/fxml/CheckoutModal.fxml"));
        Parent root = loader.load();

        stage.setScene(new Scene(root));
        stage.setTitle("Checkout");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((Node) event.getSource()).getScene().getWindow());
        stage.setResizable(false);
        stage.show();

        CheckoutModalController modalController = loader.getController();
        modalController.setCheckoutPageController(this);
        modalController.calcTaxTotal(updateSubTotalLabel());
    }

    // Print receipt and update database
    public void printReceipt(BigDecimal cash) throws IOException {
        this.cashInput = cash;
        webEngine.load(getClass().getResource("/com/example/pet_inventory/web/receipt.html").toExternalForm());
    }

    // Render receipt in WebView and save order to database
    private void renderReceipt() throws SQLException {
        String orderID = UUID.randomUUID().toString();
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM-dd-yyyy HH:mm:ss"));
        webEngine.executeScript(String.format("setHeader('Pet Store', 'Toronto, Ontario', '%s', 'Cash', '%s')", date, orderID));

        BigDecimal subTotal = BigDecimal.ZERO;
        ProductDao productDao = new ProductDao();

        for (OrderItem item : tableCart.getItems()) {
            BigDecimal amount = item.getProductPrice().multiply(BigDecimal.valueOf(item.getProductQuantity()));
            webEngine.executeScript(String.format("addItem('%s', %d, %.2f, %.2f)",
                    item.getProductName(),
                    item.getProductQuantity(),
                    item.getProductPrice(),
                    amount
            ));

            subTotal = subTotal.add(amount);
            productDao.soldProductByUPC(item.getProductUPC(), item.getProductQuantity());
        }

        BigDecimal hst = subTotal.multiply(HST);
        BigDecimal total = subTotal.add(hst);
        BigDecimal rounded = total.divide(new BigDecimal("0.05"), 0, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("0.05"));

        webEngine.executeScript(String.format("setTotal(%.2f, %.2f, %.2f, %.2f, %.2f, %.2f, %.2f)",
                subTotal,
                hst,
                rounded.subtract(total),
                rounded,
                total,
                cashInput,
                cashInput.subtract(rounded)
        ));

        // Save order and order items to database
        OrderDao orderDao = new OrderDao();
        orderDao.addOrder(orderID, total, "cash");

        OrderItemDao orderItemDao = new OrderItemDao();
        for (OrderItem item : tableCart.getItems()) {
            BigDecimal amount = item.getProductPrice().multiply(BigDecimal.valueOf(item.getProductQuantity()));
            orderItemDao.addOrderItem(orderID, item.getProductUPC(), item.getProductQuantity(), amount);
        }

        tableCart.getItems().clear();
    }

    // ==================== Utility ====================

    // Update subtotal label
    private BigDecimal updateSubTotalLabel() {
        BigDecimal subTotal = BigDecimal.ZERO;
        for (OrderItem item : tableCart.getItems()) {
            subTotal = subTotal.add(item.getSubTotal());
        }
        txtSubtotal.setText(currencyFormat.format(subTotal.setScale(2, BigDecimal.ROUND_HALF_UP)));
        return subTotal;
    }

    // Show error alert
    public void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}