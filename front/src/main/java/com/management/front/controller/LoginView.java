package com.management.front.controller;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.management.front.util.HttpClientUtil.login;
import static com.management.front.util.HttpClientUtil.request;

public class LoginView extends GridPane{

    boolean loginStatue = false;
    public LoginView() throws Exception {
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(40, 40, 40, 40));
        this.setHgap(10);
        this.setVgap(10);

        Label userNameLabel = new Label("Username:");
        this.add(userNameLabel, 0, 1);

        TextField userNameTextField = new TextField();
        this.add(userNameTextField, 1, 1);

        Label passwordLabel = new Label("Password:");
        this.add(passwordLabel, 0, 2);

        PasswordField passwordField = new PasswordField();
        this.add(passwordField, 1, 2);

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            String username = userNameTextField.getText();
            String password = passwordField.getText();
            // 实现与后端的通信
            try {
                loginStatue = login(username, password);
                if (loginStatue) {
                    System.out.println("Login successful");
                    // 登录成功后跳转到主页面
                    Menu menu = new Menu();
                    Scene scene = new Scene(menu, 800, 600);
                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                } else {
                    System.out.println("Login failed");
                }
            } catch (IOException | URISyntaxException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }

            //测试通讯
            //System.out.println(request("/getAllStudents", null).getData());

        });
        this.add(loginButton, 1, 3);
    }
    public boolean getLoginStatue() {
        return loginStatue;
    }
}
