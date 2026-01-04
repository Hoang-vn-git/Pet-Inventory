package com.example.pet_inventory.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class CheckoutModalController {

    // FXML UI components
    @FXML
    private Label modalSubtotal;

    @FXML
    private Label modalTax;

    @FXML
    private Label modalTotal;

    // Tax rate
    private final BigDecimal HST = new BigDecimal("0.13");

    // Formatter for displaying currency
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

    // Total including tax
    private BigDecimal total;

    // Reference to CheckoutPageController to communicate with main checkout
    private CheckoutPageController checkoutPageController;

    // Setter to inject CheckoutPageController
    public void setCheckoutPageController(CheckoutPageController controller) {
        this.checkoutPageController = controller;
    }

    // Close this modal window
    @FXML
    public void close(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

    // Calculate tax and total, update modal labels
    public BigDecimal calcTaxTotal(BigDecimal subTotal) {
        BigDecimal hst = subTotal.multiply(HST);
        modalTax.setText(currencyFormat.format(hst));

        total = subTotal.add(hst);
        modalTotal.setText(currencyFormat.format(total));

        modalSubtotal.setText(currencyFormat.format(subTotal));

        return total;
    }

    // Open Cash Checkout modal and pass necessary data
    @FXML
    private void showCashCheckout(ActionEvent event) throws IOException {
        close(event);

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pet_inventory/fxml/CashCheckout.fxml"));
        Parent root = loader.load();

        stage.setScene(new Scene(root));
        stage.setTitle("Checkout");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((Node)event.getSource()).getScene().getWindow());
        stage.setResizable(false);
        stage.show();

        // Pass data and controller reference to CashCheckout modal
        CashCheckoutController cashController = loader.getController();
        cashController.setCheckoutPageController(checkoutPageController);
        cashController.calcCashRounding(total);
    }

    // Open Card Checkout modal
    @FXML
    public void showCardCheckout(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pet_inventory/fxml/CardCheckout.fxml"));
        Parent root = loader.load();

        stage.setScene(new Scene(root));
        stage.setTitle("Checkout");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((Node)event.getSource()).getScene().getWindow());
        stage.setResizable(false);
        stage.show();
    }
}