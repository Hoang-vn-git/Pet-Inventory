package com.example.pet_inventory.controller;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class CardCheckoutController {

    @FXML
    private Label status;


    public void initialize() {
        startStatusSequence();
    }
    private void startStatusSequence() {
        // Sau 2s: chuyển sang "Processing..."
        PauseTransition wait1 = new PauseTransition(Duration.seconds(2));
        wait1.setOnFinished(e -> {
            status.setText("Processing...");
            // Sau 2s nữa: chuyển sang "Approved"
            PauseTransition wait2 = new PauseTransition(Duration.seconds(2));
            wait2.setOnFinished(ev -> status.setText("Approved !"));
            wait2.play();
        });
        wait1.play();
    }
}
