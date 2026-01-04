package com.example.pet_inventory.controller;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SideBarController {

    // FXML UI components
    @FXML
    private AnchorPane content;
    @FXML
    private AnchorPane sidebar;
    @FXML
    private Label currentDate;

    // References to child controllers
    private HomePageController homePageController;
    private CheckoutPageController checkoutPageController;
    private InventoryPageController inventoryPageController;
    private AssistantController assistantController;

    // Initialize UI
    public void initialize() {
        // Load HomePage by default
        loadView("/com/example/pet_inventory/fxml/HomePage.fxml");

        // Update current date/time every frame
        AnimationTimer dateAndTime = new AnimationTimer() {
            @Override
            public void handle(long now) {
                currentDate.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM-dd-yyyy HH:mm:ss")));
            }
        };
        dateAndTime.start();
    }

    // Load FXML into content pane and store controller reference
    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            StackPane view = loader.load();

            Object controller = loader.getController();
            // Store references for later communication
            if (controller instanceof HomePageController) {
                homePageController = (HomePageController) controller;
            } else if (controller instanceof CheckoutPageController) {
                checkoutPageController = (CheckoutPageController) controller;
                checkoutPageController.setHomePageController(homePageController);
            } else if (controller instanceof InventoryPageController) {
                inventoryPageController = (InventoryPageController) controller;
            } else if (controller instanceof AssistantController) {
                assistantController = (AssistantController) controller;
            }

            content.getChildren().setAll(view);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Show Inventory page
    @FXML
    private void showInventory() {
        loadView("/com/example/pet_inventory/fxml/Inventory.fxml");
    }

    // Show Home/Dashboard page
    @FXML
    private void showDashboard() {
        loadView("/com/example/pet_inventory/fxml/HomePage.fxml");
    }

    // Show Checkout page
    @FXML
    private void showCheckout(ActionEvent event) {
        loadView("/com/example/pet_inventory/fxml/Checkout.fxml");
    }

    // Show Assistant page
    @FXML
    private void showAssistant(ActionEvent event) {
        loadView("/com/example/pet_inventory/fxml/Assistant.fxml");
    }

    // Getters for controllers if needed externally
    public HomePageController getHomePageController() {
        return homePageController;
    }

    public CheckoutPageController getCheckoutPageController() {
        return checkoutPageController;
    }

    public InventoryPageController getInventoryPageController() {
        return inventoryPageController;
    }

    public AssistantController getAssistantController() {
        return assistantController;
    }
}