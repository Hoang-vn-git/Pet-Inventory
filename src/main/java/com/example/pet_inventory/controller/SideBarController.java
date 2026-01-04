package com.example.pet_inventory.controller;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller for the Sidebar.
 * Handles navigation between different views and displays current date/time.
 */
public class SideBarController {

    // ==================== FXML Components ====================
    @FXML private AnchorPane content;
    @FXML private AnchorPane sidebar;
    @FXML private Label currentDate;

    // ==================== Child Controllers ====================
    private HomePageController homePageController;
    private CheckoutPageController checkoutPageController;
    private InventoryPageController inventoryPageController;
    private AssistantController assistantController;
    private HistoryController historyController;

    // ==================== Initialize ====================
    @FXML
    public void initialize() {
        // Load HomePage by default
        loadView("/com/example/pet_inventory/fxml/HomePage.fxml");

        // Update current date/time continuously
        AnimationTimer dateAndTime = new AnimationTimer() {
            @Override
            public void handle(long now) {
                currentDate.setText(
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM-dd-yyyy HH:mm:ss"))
                );
            }
        };
        dateAndTime.start();
    }

    // ==================== Helper Methods ====================
    /** Load FXML into content pane and store controller reference */
    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            StackPane view = loader.load();

            // Store controller reference for external use if needed
            Object controller = loader.getController();
            if (controller instanceof HomePageController) homePageController = (HomePageController) controller;
            else if (controller instanceof CheckoutPageController) checkoutPageController = (CheckoutPageController) controller;
            else if (controller instanceof InventoryPageController) inventoryPageController = (InventoryPageController) controller;
            else if (controller instanceof AssistantController) assistantController = (AssistantController) controller;
            else if (controller instanceof HistoryController) historyController = (HistoryController) controller;

            content.getChildren().setAll(view);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ==================== Event Handlers ====================
    @FXML private void showInventory() {
        loadView("/com/example/pet_inventory/fxml/Inventory.fxml");
    }

    @FXML private void showDashboard() {
        loadView("/com/example/pet_inventory/fxml/HomePage.fxml");
    }

    @FXML private void showCheckout(ActionEvent event) {
        loadView("/com/example/pet_inventory/fxml/Checkout.fxml");
    }

    @FXML private void showAssistant(ActionEvent event) {
        loadView("/com/example/pet_inventory/fxml/Assistant.fxml");
    }

    @FXML private void showHistory(ActionEvent event) {
        loadView("/com/example/pet_inventory/fxml/History.fxml");
    }

    // ==================== Getters for controllers ====================
    public HomePageController getHomePageController() { return homePageController; }
    public CheckoutPageController getCheckoutPageController() { return checkoutPageController; }
    public InventoryPageController getInventoryPageController() { return inventoryPageController; }
    public AssistantController getAssistantController() { return assistantController; }
    public HistoryController getHistoryController() { return historyController; }
}