package com.management.front.page;

import com.management.front.request.DataResponse;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXPasswordField;

import java.util.HashMap;
import java.util.Map;

import static com.management.front.util.HttpClientUtil.*;

public class LoginPage extends GridPane {
    public static String username;//不知道这样合不合适
    private JFXTextField nameField = new JFXTextField();
    private JFXTextField usernameField = new JFXTextField();
    private JFXPasswordField passwordField = new JFXPasswordField();
    private JFXButton loginButton = new JFXButton("登录");
    private JFXButton switchToRegisterButton = new JFXButton("去注册");
    private JFXButton registerButton = new JFXButton("注册");
    private JFXButton switchToLoginButton = new JFXButton("返回登录");

    public LoginPage() {
        setupUI();
        setupActions();
        this.getStylesheets().add("dark-theme.css");

        sendAndReceiveDataResponse("/register", Map.of("name", "向辉", "username", "100000", "password", "admin"));
        sendAndReceiveDataResponse("/register", Map.of("name", "tst", "username", "201921000", "password", "admin"));
        sendAndReceiveDataResponse("/register", Map.of("name", "wzk", "username", "201921001", "password", "admin"));
        sendAndReceiveDataResponse("/register", Map.of("name", "why", "username", "201921002", "password", "admin"));

        //测试用
        nameField.setText("向辉");
        usernameField.setText("admin");
        passwordField.setText("admin");
    }

    private void setupUI() {
        this.setAlignment(javafx.geometry.Pos.CENTER);
        this.setVgap(10);
        this.add(usernameField, 0, 0);
        this.add(passwordField, 0, 1);
        this.add(loginButton, 0, 2);
        this.add(switchToRegisterButton, 1, 2);
        switchToLoginButton.setVisible(false); // Initially hide switch to login button
        registerButton.setVisible(false); // Initially hide register button
        nameField.setVisible(false); // Initially hide name field
    }

    private void setupActions() {
        loginButton.setOnMouseClicked(event -> attemptLogin());
        switchToRegisterButton.setOnMouseClicked(event -> switchToRegister());
        switchToLoginButton.setOnMouseClicked(event -> switchToLogin());
        registerButton.setOnMouseClicked(event -> registerUser());
    }

    private void attemptLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (login(username, password)) {
            changeScene();
            LoginPage.username = username;
            //showAlert("登录成功", Alert.AlertType.INFORMATION);
        } else {
            showAlert("登录失败", Alert.AlertType.ERROR);
        }
    }

    private void registerUser() {
        Map<String, String> map = new HashMap<>();
        map.put("name", nameField.getText());
        map.put("username", usernameField.getText());
        map.put("password", passwordField.getText());
        DataResponse response = sendAndReceiveDataResponse("/register", map);
        showAlert(response.getMsg(), response.getCode() == 0 ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
    }

    private void switchToRegister() {
        this.getChildren().clear();
        this.add(nameField, 0, 0);
        this.add(usernameField, 0, 1);
        this.add(passwordField, 0, 2);
        this.add(registerButton, 0, 3);
        this.add(switchToLoginButton, 1, 3);
        nameField.setVisible(true);
        registerButton.setVisible(true);
        switchToLoginButton.setVisible(true);
    }

    private void switchToLogin() {
        this.getChildren().clear();
        this.add(usernameField, 0, 0);
        this.add(passwordField, 0, 1);
        this.add(loginButton, 0, 2);
        this.add(switchToRegisterButton, 1, 2);
    }

    private void changeScene() {
        Menu menu = new Menu();
        Scene scene = new Scene(menu, 1400, 800);
        Stage stage = (Stage) this.getScene().getWindow();
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
