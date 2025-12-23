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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
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
    @FXML
    public Label txtHST;
    @FXML
    public Label txtCashRounding;
    @FXML
    public Label txtTotal;
    @FXML
    public TextField txtRemove;
    @FXML
    public Button btnCheckout;

    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

    ObservableList<OrderItem> data = FXCollections.observableArrayList();

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
        // Print receipt
        webEngine = webView.getEngine();
        webEngine.loadContent("""
                <html lang="en">
                <head>
                <meta charset="UTF-8" />
                <title>POS Receipt</title>
                
                <style>
                  :root{
                    --ink:#111;
                    --muted:#555;
                    --paper:#fff;
                    --border:#ddd;
                  }
                
                  *{ box-sizing:border-box; }
                  body{
                    margin:0;
                    background:#f5f5f5;
                    color:var(--ink);
                    font-family: "JetBrains Mono", Consolas, Menlo, Monaco, "Courier New", monospace;
                    font-size:13px;
                    line-height:1.45;
                  }
                  .page{
                    padding:16px;
                    display:flex;
                    justify-content:center;
                  }
                
                  .receipt{
                    width: 80mm;
                    max-width: 100%;
                    background:var(--paper);
                    border:1px solid var(--border);
                    box-shadow: 0 3px 12px rgba(0,0,0,.08);
                    padding: 8px 10px;
                  }
                  .receipt.narrow-58{ width:58mm; }
                
                  .store{
                    text-align:center;
                    font-weight:700;
                    letter-spacing: .2px;
                  }
                  .info{
                    text-align:center;
                    color:var(--muted);
                    font-size:12px;
                    margin-top:4px;
                  }
                  .hr{
                    border-top:1px dashed var(--border);
                    margin:8px 0;
                  }
                
                  .meta{
                    display:flex;
                    justify-content:space-between;
                    gap:8px;
                    font-size:12px;
                  }
                  .mono{ font-variant-numeric: tabular-nums; }
                
                  table{
                    width:100%;
                    border-collapse:collapse;
                  }
                  thead th{
                    text-align:left;
                    color:var(--muted);
                    font-weight:600;
                    font-size:12px;
                    padding:4px 0;
                  }
                  thead th.right, td.right{ text-align:right; }
                  thead th.center, td.center{ text-align:center; }
                
                  tbody td{
                    padding:3px 0;
                    vertical-align:top;
                    font-variant-numeric: tabular-nums;
                  }
                  .sku{
                    color:var(--muted);
                    font-size:11px;
                  }
                
                  td.right, th.right { min-width:42px; }
                  td.price-col { padding-right:8px; }
                  td.amount-col { min-width:48px; }
                
                  .totals{
                    margin-top:6px;
                    font-variant-numeric: tabular-nums;
                  }
                  .row{
                    display:flex;
                    justify-content:space-between;
                    padding:3px 0;
                  }
                  .grand{
                    border-top:1px dashed var(--border);
                    padding-top:6px;
                    margin-top:4px;
                    font-weight:700;
                  }
                
                  .thanks{
                    text-align:center;
                    font-size:12px;
                    color:var(--muted);
                    margin-top:8px;
                  }
                
                  .barcode{
                    margin:8px auto 0;
                    width:90%;
                    height:36px;
                    background:
                      repeating-linear-gradient(
                        to right,
                        #000 0 2px,
                        transparent 2px 4px
                      );
                    opacity:.12;
                  }
                  .rid{
                    text-align:center;
                    font-size:12px;
                    color:var(--muted);
                    margin-top:4px;
                  }
                
                  @media print{
                    body{ background:#fff; }
                    .page{ padding:0; }
                    .receipt{
                      border:none;
                      box-shadow:none;
                    }
                  }
                </style>
                </head>
                
                <body>
                <div class="page">
                <div class="receipt">
                
                  <!-- Store -->
                  <div class="store" id="storeName"></div>
                  <div class="info" id="storeInfo"></div>
                
                  <div class="hr"></div>
                
                  <!-- Meta -->
                  <div class="meta mono">
                    <div id="date"></div>
                    <div id="cashier"></div>
                  </div>
                  <div class="meta mono" style="margin-top:2px;">
                    <div id="payment"></div>
                    <div id="orderId"></div>
                  </div>
                
                  <div class="hr"></div>
                
                  <!-- Items -->
                  <table>
                    <thead>
                      <tr>
                        <th>Item</th>
                        <th class="center">Qty</th>
                        <th class="right">Price</th>
                        <th class="right">Amount</th>
                      </tr>
                    </thead>
                    <tbody id="itemsBody">
                      <!-- Java will inject rows here -->
                    </tbody>
                  </table>
                
                  <div class="hr"></div>
                
                  <!-- Totals -->
                  <div class="totals mono">
                    <div class="row"><div>Subtotal</div><div id="subtotal"></div></div>
                    <div class="row"><div>HST (13%)</div><div id="tax"></div></div>
                    <div class="row"><div>Discount</div><div id="discount"></div></div>
                    <div class="row grand"><div>Total</div><div id="total"></div></div>
                    <div class="row"><div>Paid</div><div id="paid"></div></div>
                  </div>
                
                  <div class="hr"></div>
                
                  <!-- Footer -->
                  <div class="thanks">
                    Thank you for shopping with us! Keep the receipt for warranty. \s
                    Returns accepted within 7 days with original packaging.
                  </div>
                  <div class="barcode"></div>
                  <div class="rid mono" id="receiptId"></div>
                
                </div>
                </div>
                
                <script>
                function setHeader(store, info, date, cashier, payment, orderId) {
                  document.getElementById("storeName").innerText = store;
                  document.getElementById("storeInfo").innerText = info;
                  document.getElementById("date").innerText = date;
                  document.getElementById("cashier").innerText = cashier;
                  document.getElementById("payment").innerText = payment;
                  document.getElementById("orderId").innerText = orderId;
                }
                
                function addItem(name, sku, qty, price, amount) {
                  const tr = document.createElement("tr");
                  tr.innerHTML = `
                    <td>${name}<div class="sku">${sku}</div></td>
                    <td class="center mono">${qty}</td>
                    <td class="right mono price-col">${price}</td>
                    <td class="right mono amount-col">${amount}</td>
                  `;
                  document.getElementById("itemsBody").appendChild(tr);
                }
                
                function setTotals(sub, tax, discount, total, paid, rid) {
                  document.getElementById("subtotal").innerText = sub;
                  document.getElementById("tax").innerText = tax;
                  document.getElementById("discount").innerText = discount;
                  document.getElementById("total").innerText = total;
                  document.getElementById("paid").innerText = paid;
                  document.getElementById("receiptId").innerText = rid;
                }
                </script>
                
                </body>
                </html>
                """);
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
        checkoutModalController.calcTaxTotal(updateSubTotalLabel());
    }

}
