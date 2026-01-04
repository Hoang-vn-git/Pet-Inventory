package com.example.pet_inventory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 * Main entry point for the Pet Inventory JavaFX application.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Load main layout (Sidebar) from FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pet_inventory/fxml/SideBar.fxml"));
        Scene scene = new Scene(loader.load());

        stage.setScene(scene);
        stage.setResizable(false);


        // Handle window close with confirmation
        stage.setOnCloseRequest(e -> {
            e.consume(); // Prevent default close
            confirmExit(stage);
        });

        stage.show();
    }

    /**
     * Show confirmation dialog before closing application.
     *
     * @param stage the main application stage
     */
    private void confirmExit(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Close Application");
        alert.setHeaderText("You're about to close the application");
        alert.setContentText("Do you want to exit?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            stage.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}