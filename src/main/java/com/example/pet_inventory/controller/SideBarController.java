package com.example.pet_inventory.controller;


import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SideBarController {
    @FXML
    public AnchorPane content;
    @FXML
    public AnchorPane sidebar;
    @FXML
    private Label currentDate;


    public void initialize() {

        loadView("/com/example/pet_inventory/fxml/HomePage.fxml");
        // Source - https://stackoverflow.com/a
        // Posted by Shekhar Rai, modified by community. See post 'Timeline' for change history
        // Retrieved 2025-12-12, License - CC BY-SA 4.0
        AnimationTimer dateAndTime = new AnimationTimer() {
            @Override
            public void handle(long now) {
                currentDate.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM-dd-yyyy HH:mm:ss")));
            }
        };
        dateAndTime.start();
    }

    private void loadView(String fxml) {
        try {
            StackPane view = FXMLLoader.load(
                    getClass().getResource(fxml)
            );
            content.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    @FXML
    private void showInventory() {
        loadView("/com/example/pet_inventory/fxml/Inventory.fxml");
    }

    @FXML
    private void showDashboard() {
        loadView("/com/example/pet_inventory/fxml/HomePage.fxml");
    }

    @FXML
    private void showCheckout(ActionEvent event) throws IOException {
        loadView("/com/example/pet_inventory/fxml/Checkout.fxml");
    }

}
