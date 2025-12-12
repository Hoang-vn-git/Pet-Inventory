package com.example.pet_inventory.controller;

import com.example.pet_inventory.dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class LoginController {
    @FXML
    public TextField userIDLabel;
    @FXML
    public PasswordField passwordLabel;
    @FXML
    public Button loginBtn;

    @FXML
    public void initialize() {
        loginBtn.setDefaultButton(true);
    }
    public void login(ActionEvent actionEvent) throws SQLException, IOException {
        String userID = userIDLabel.getText();
        String password = passwordLabel.getText();

        if (userID.isEmpty() || password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please enter Username and Password!");
            alert.showAndWait();
            return;
        }

        try {
            UserDAO userDao = new UserDAO();
            ResultSet rs = userDao.authUser(userID, password);
            if (rs.next()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Message");
                alert.setHeaderText(null);
                alert.setContentText("Successfully Logged In!");
                alert.showAndWait();
                switchToMain(actionEvent);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Invalid Username or Password!");
                alert.showAndWait();
            }

            rs.getStatement().getConnection().close();
        }
        catch (SQLException | IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void switchToMain(ActionEvent actionEvent) throws IOException {
        Scene scene = ((Node)actionEvent.getSource()).getScene();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/pet_inventory/fxml/HomePage.fxml"));
        scene.setRoot(loader.load());
    }
}
