package com.example.pet_inventory.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
    final private String METHOD = "CASH";

    public void initialize() {
        setupCashFormatter(txtCash);
    }
    @FXML
    private void close(ActionEvent event) throws IOException {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

        stage.close();
    }

    public void calcCashRounding(BigDecimal total){
        cashRounding = total
                .divide(new BigDecimal("0.05"), 0, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("0.05"))
                .setScale(2, RoundingMode.UNNECESSARY);

        labelCashRounding.setText(currencyFormat.format(cashRounding));


    }
    private void setupCashFormatter(TextField tf) {

        TextFormatter<String> formatter = new TextFormatter<>(change -> {

            if (!change.isContentChange()) {
                return change;
            }

            // Chỉ cho gõ số và backspace
            String newText = change.getText();
            if (!newText.matches("[0-9]*")) {
                return null;
            }

            // Lấy text hiện tại (KHÔNG dùng controlNewText)
            String currentDigits = tf.getText().replaceAll("[^0-9]", "");

            if (change.isDeleted()) {
                // Xóa 1 digit cuối
                if (!currentDigits.isEmpty()) {
                    currentDigits = currentDigits.substring(0, currentDigits.length() - 1);
                }
            } else {
                // Append digit
                currentDigits += newText;
            }

            if (currentDigits.isEmpty()) {
                currentDigits = "0";
            }

            long value = Long.parseLong(currentDigits);
            String formatted = String.format("%.2f", value / 100.0);

            // Replace toàn bộ
            change.setRange(0, tf.getText().length());
            change.setText(formatted);

            // Luôn đưa caret về cuối
            change.selectRange(formatted.length(), formatted.length());

            return change;
        });

        tf.setTextFormatter(formatter);
        tf.setText("0.00");

        // ⚠️ CỰC QUAN TRỌNG: bỏ select-all khi focus
        tf.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                tf.deselect();
                tf.positionCaret(tf.getText().length());
            }
        });
    }

    @FXML
    public void calcChangeDue(ActionEvent actionEvent) {
        BigDecimal receivedCash = new BigDecimal(txtCash.getText());

        BigDecimal change = receivedCash.subtract(cashRounding);

        labelChangeDue.setText(currencyFormat.format(change));

    }
}
