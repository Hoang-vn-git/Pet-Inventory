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

/**
 * Controller for Login page.
 * Handles user authentication and scene switching.
 */
public class LoginController {

    // ==================== FXML Components ====================
    @FXML private TextField userIDLabel;
    @FXML private PasswordField passwordLabel;
    @FXML private Button loginBtn;

    // ==================== Initialize ====================
    @FXML
    public void initialize() {
        // Set default button to enable Enter key login
        loginBtn.setDefaultButton(true);
    }

    // ==================== Event Handlers ====================
    /** Handle login button click */
    @FXML
    public void login(ActionEvent event) {
        String userID = userIDLabel.getText().trim();
        String password = passwordLabel.getText().trim();

        // Validate input fields
        if (userID.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please enter both Username and Password!");
            return;
        }

        try {
            UserDAO userDao = new UserDAO();
            ResultSet rs = userDao.authUser(userID, password);

            if (rs.next()) {
                showAlert(Alert.AlertType.INFORMATION, "Login successful!");
                switchToMain(event);
            } else {
                showAlert(Alert.AlertType.ERROR, "Invalid Username or Password!");
            }

            rs.getStatement().getConnection().close();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "An error occurred: " + e.getMessage());
        }
    }

    // ==================== Navigation ====================
    /** Switch scene to main HomePage */
    private void switchToMain(ActionEvent event) throws IOException {
        Scene scene = ((Node) event.getSource()).getScene();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pet_inventory/fxml/SideBar.fxml"));
        scene.setRoot(loader.load());
    }

    // ==================== Helper Methods ====================
    /** Show alert dialog */
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type.name());
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}