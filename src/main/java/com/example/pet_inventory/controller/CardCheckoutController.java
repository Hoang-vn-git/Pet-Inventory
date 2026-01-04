package com.example.pet_inventory.controller;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * Controller for the card checkout modal.
 * Simulates card processing with status updates.
 */
public class CardCheckoutController {

    @FXML
    private Label status; // Label to show card payment status

    // Initialize JavaFX controller
    public void initialize() {
        startStatusSequence();
    }

    /**
     * Start the sequence of status messages for card processing:
     * 1. Wait 2s, then show "Processing..."
     * 2. Wait another 2s, then show "Approved!"
     */
    private void startStatusSequence() {
        // Step 1: Wait 2 seconds
        PauseTransition wait1 = new PauseTransition(Duration.seconds(2));
        wait1.setOnFinished(e -> {
            status.setText("Processing...");

            // Step 2: Wait another 2 seconds, then approve
            PauseTransition wait2 = new PauseTransition(Duration.seconds(2));
            wait2.setOnFinished(ev -> status.setText("Approved!"));
            wait2.play();
        });
        wait1.play();
    }
}