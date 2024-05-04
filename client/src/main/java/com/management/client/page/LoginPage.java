package com.management.client.page;

import com.management.client.request.DataResponse;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXPasswordField;

import java.util.HashMap;
import java.util.Map;

import static com.management.client.util.HttpClientUtil.*;

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
        MenuBar menuBar = new MenuBar();
        javafx.scene.control.Menu menu1 = new javafx.scene.control.Menu("按钮");
        MenuItem menuItem1 = new MenuItem("修改密码");
        MenuItem menuItem2 = new MenuItem("退出登录");
        menu1.getItems().addAll(menuItem1, menuItem2);
        menuItem1.setOnAction(event -> {
            Stage modifyPasswordStage=new Stage();
            GridPane gridPane=new GridPane();
            gridPane.getStylesheets().add("dark-theme.css");
            gridPane.setAlignment(javafx.geometry.Pos.CENTER);
            TextField oldPassword = new TextField();
            TextField newPassword = new TextField();
            TextField confirmPassword = new TextField();
            JFXButton confirmButton = new JFXButton("确认");
            JFXButton cancelButton = new JFXButton("取消");
            confirmButton.setOnAction(event1 -> {
                if (!newPassword.getText().equals(confirmPassword.getText())) {
                    showAlert("两次输入的密码不一致", Alert.AlertType.ERROR);
                    return;
                }
                if(oldPassword.getText().equals(newPassword.getText()))
                {
                    showAlert("新密码不能与旧密码相同", Alert.AlertType.ERROR);
                    return;
                }
                Map<String, String> map = new HashMap<>();
                map.put("oldPassword", oldPassword.getText());
                map.put("newPassword", newPassword.getText());
                DataResponse response = request("/modifyPassword", map);
                showAlert(response.getMsg(), response.getCode() == 0 ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
                if (response.getCode() == 0) {
                    modifyPasswordStage.close();
                }
            });
            cancelButton.setOnAction(event1 -> modifyPasswordStage.close());
            gridPane.addColumn(0,new Label("旧密码"),new Label("新密码"),new Label("确认密码"),confirmButton);
            gridPane.addColumn(1,oldPassword,newPassword,confirmPassword,cancelButton);
            Scene scene=new Scene(gridPane, 300, 200);
            modifyPasswordStage.setScene(scene);
            modifyPasswordStage.show();
        });
        menuItem2.setOnAction(e->
                {
                    Stage stage = (Stage) menu.getParent().getScene().getWindow();
                    stage.close();
                    Stage newStage = new Stage();
                    Scene scene = new Scene(new LoginPage(), 300, 400);
                    newStage.setScene(scene);
                    newStage.show();


                    /*Stage stage = (Stage) menu.getParent().getScene().getWindow();
                    Scene scene = new Scene(new LoginPage(), 300, 400);
                    stage.setScene(scene);
                    stage.show();*/
                    //此方案loginpage的大小无法调整
                }
        );
        menuBar.getMenus().addAll(menu1);
        Stage stage = (Stage) this.getScene().getWindow();
        VBox vBox=new VBox();
        vBox.getChildren().addAll(menuBar,menu);
        VBox.setVgrow(menu, Priority.ALWAYS);
        Scene scene = new Scene(vBox, 1400, 800);
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
