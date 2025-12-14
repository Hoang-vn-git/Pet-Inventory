package com.example.pet_inventory.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

public class InventoryPageController {

    @FXML private ChoiceBox<String> choiceBox;
    String[] category = {"Dog Food", "Cat Food", "Accessory", "Dog Treat", "Cat Treat"};

    public void initialize() {
        choiceBox.getItems().addAll(category);
    };



}
