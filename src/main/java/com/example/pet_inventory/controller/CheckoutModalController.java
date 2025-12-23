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

    @FXML
    public Label modalSubtotal;
    @FXML
    public Label modalTax;
    @FXML
    public Label modalTotal;
    final private BigDecimal HST = new BigDecimal("0.13");
    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    BigDecimal total;

    @FXML
    private void close(ActionEvent event) throws IOException {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

        stage.close();
    }

    public BigDecimal calcTaxTotal(BigDecimal subTotal){
        BigDecimal hst = subTotal.multiply(HST);
        modalTax.setText(currencyFormat.format(hst));

        total = subTotal.add(hst);
        modalTotal.setText(currencyFormat.format(total));

        modalSubtotal.setText(currencyFormat.format(subTotal));

        return total;
    }

    @FXML
    private void showCashCheckout(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pet_inventory/fxml/CashCheckout.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.setTitle("Checkout");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(
                ((Node)event.getSource()).getScene().getWindow() );
        stage.setResizable(false);
        stage.show();
//         Communicate modal
        CashCheckoutController cashCheckoutModal = loader.getController();
        cashCheckoutModal.calcCashRounding(total);
    }

    public void showCardCheckout(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pet_inventory/fxml/CardCheckout.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.setTitle("Checkout");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(
                ((Node)event.getSource()).getScene().getWindow() );
        stage.setResizable(false);
        stage.show();
    }
}
