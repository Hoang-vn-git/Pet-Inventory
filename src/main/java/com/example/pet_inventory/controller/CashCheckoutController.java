package com.example.pet_inventory.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public class CashCheckoutController {

    @FXML
    public TextField txtCash;
    @FXML
    public Label labelChangeDue;
    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    @FXML
    public Label labelCashRounding;
    private BigDecimal cashRounding;
    private CheckoutPageController checkoutPageController;

    public void setCheckoutPageController(CheckoutPageController controller) {
        this.checkoutPageController = controller;
    }
//    public void initialize() {
//        setupCashFormatter(txtCash);
//    }
    @FXML
    private void close(ActionEvent event) throws IOException {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

        stage.close();
    }

    public BigDecimal getCashReceived() {

        String text = txtCash.getText();

        // 1. Rỗng hoặc null → 0
        if (text == null || text.isBlank()) {
            return BigDecimal.ZERO;
        }

        // 2. Trim khoảng trắng
        text = text.trim();

        // 3. Chỉ cho phép số + dấu chấm
        if (!text.matches("\\d+(\\.\\d{1,2})?")) {
            throw new IllegalArgumentException("Cash received is not a valid amount");
        }

        return new BigDecimal(text);
    }
    public void calcCashRounding(BigDecimal total){
        cashRounding = total
                .divide(new BigDecimal("0.05"), 0, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("0.05"))
                .setScale(2, RoundingMode.UNNECESSARY);

        labelCashRounding.setText(currencyFormat.format(cashRounding));


    }
    @FXML
    public BigDecimal calcChangeDue() {
        BigDecimal receivedCash = new BigDecimal(txtCash.getText());

        BigDecimal change = receivedCash.subtract(cashRounding);

        labelChangeDue.setText(currencyFormat.format(change));

        return change;

    }

    @FXML
    public void printReceipt(ActionEvent event) throws IOException {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pet_inventory/fxml/Checkout.fxml"));
        loader.load();
        setCheckoutPageController(checkoutPageController);
        String text = txtCash.getText();

        // 1. Rỗng hoặc null → 0
        if (text == null || text.isBlank()) {
            checkoutPageController.printReceipt(BigDecimal.ZERO);
        }
        // 3. Chỉ cho phép số + dấu chấm
         else if (!text.matches("\\d+(\\.\\d{1,2})?")) {
            throw new IllegalArgumentException("Cash received is not a valid amount");
        } else {
            text = text.trim();
            checkoutPageController.printReceipt(new BigDecimal(text));
        }

    }
}
