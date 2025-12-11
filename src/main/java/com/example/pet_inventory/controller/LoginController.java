package com.example.pet_inventory.controller;

import com.example.pet_inventory.dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.ResultSet;
import java.sql.SQLException;

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
    public void login(ActionEvent actionEvent) throws SQLException {
        UserDAO userDao = new UserDAO();

        String userID = userIDLabel.getText();
        String password = passwordLabel.getText();

        ResultSet rs = userDao.authUser(userID, password);

        if (rs.next()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Message");
            alert.setHeaderText(null);
            alert.setContentText("Successfully Logged In!");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid Username or Password!");
            alert.showAndWait();
        }

        rs.getStatement().getConnection().close(); // đóng 3 thứ
    }
}
