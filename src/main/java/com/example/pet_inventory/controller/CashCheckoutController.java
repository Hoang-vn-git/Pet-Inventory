package com.example.pet_inventory.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public class CashCheckoutController {

    // FXML UI components
    @FXML
    private TextField txtCash;

    @FXML
    private Label labelChangeDue;

    @FXML
    private Label labelCashRounding;

    // Formatter for displaying currency
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

    // Rounded total after applying rounding rules
    private BigDecimal cashRounding;

    // Reference to CheckoutPageController to call printReceipt
    private CheckoutPageController checkoutPageController;

    // Setter to inject CheckoutPageController
    public void setCheckoutPageController(CheckoutPageController controller) {
        this.checkoutPageController = controller;
    }

    // Close the cash modal
    @FXML
    private void close(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

    // Parse and validate cash input
    public BigDecimal getCashReceived() {
        String text = txtCash.getText();
        if (text == null || text.isBlank()) {
            return BigDecimal.ZERO;
        }
        text = text.trim();
        if (!text.matches("\\d+(\\.\\d{1,2})?")) {
            throw new IllegalArgumentException("Cash received is not a valid amount");
        }
        return new BigDecimal(text);
    }

    // Calculate rounded cash total and display in label
    public void calcCashRounding(BigDecimal total) {
        cashRounding = total
                .divide(new BigDecimal("0.05"), 0, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("0.05"))
                .setScale(2, RoundingMode.UNNECESSARY);
        labelCashRounding.setText(currencyFormat.format(cashRounding));
    }

    // Calculate change due and display in label
    @FXML
    public BigDecimal calcChangeDue() {
        BigDecimal receivedCash = getCashReceived();
        BigDecimal change = receivedCash.subtract(cashRounding);
        labelChangeDue.setText(currencyFormat.format(change));
        return change;
    }

    // Print receipt and close modal
    @FXML
    public void printReceipt(ActionEvent event) throws IOException {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();

        BigDecimal cashToUse = getCashReceived();

        if (checkoutPageController != null) {
            checkoutPageController.printReceipt(cashToUse);
        }
    }
}