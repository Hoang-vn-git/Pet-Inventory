package com.example.pet_inventory.controller;

import com.example.pet_inventory.dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    // FXML UI components
    @FXML
    private TextField userIDLabel;

    @FXML
    private PasswordField passwordLabel;

    @FXML
    private Button loginBtn;

    // Initialize UI
    @FXML
    public void initialize() {
        loginBtn.setDefaultButton(true);
    }

    // Handle login button click
    @FXML
    public void login(ActionEvent actionEvent) throws SQLException, IOException {
        String userID = userIDLabel.getText();
        String password = passwordLabel.getText();

        // Validate input
        if (userID.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please enter Username and Password!");
            return;
        }

        try {
            UserDAO userDao = new UserDAO();
            ResultSet rs = userDao.authUser(userID, password);

            if (rs.next()) {
                showAlert(Alert.AlertType.INFORMATION, "Successfully Logged In!");
                switchToMain(actionEvent);
            } else {
                showAlert(Alert.AlertType.ERROR, "Invalid Username or Password!");
            }

            rs.getStatement().getConnection().close();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "An error occurred: " + e.getMessage());
        }
    }

    // Switch scene to main HomePage
    public void switchToMain(ActionEvent actionEvent) throws IOException {
        Scene scene = ((Node)actionEvent.getSource()).getScene();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pet_inventory/fxml/HomePage.fxml"));
        scene.setRoot(loader.load());
    }

    // Helper to show alert
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type.name());
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}