package com.example.pet_inventory;

import com.example.pet_inventory.dao.UserDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pet_inventory/fxml/Checkout.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setMinWidth(1500);
            stage.setMinHeight(1000);
            stage.setOnCloseRequest(e -> {
                e.consume();
                logout(stage);
            });
            stage.show();

    }


    public void logout(Stage stage){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Close");

        alert.setHeaderText("You're about to close");

        alert.setContentText("Do you want to save before exiting?");

        if(alert.showAndWait().get() == ButtonType.OK){
            stage.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
