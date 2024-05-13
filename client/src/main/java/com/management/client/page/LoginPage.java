package com.management.client.page;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.management.client.request.DataResponse;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

import static com.management.client.util.HttpClientUtil.login;
import static com.management.client.util.HttpClientUtil.sendAndReceiveDataResponse;

public class LoginPage extends GridPane {
    private Stage testStage = new Stage();
    public static String username;//不知道这样合不合适
    private JFXTextField nameField = new JFXTextField();
    private Label namelabel = new Label("姓名");
    private Label usernamelabel = new Label("学工号");
    private Label passwordlabel = new Label("密码");
    private JFXTextField usernameField = new JFXTextField();
    private JFXPasswordField passwordField = new JFXPasswordField();
    private JFXButton loginButton = new JFXButton("登录");
    private JFXButton switchToRegisterButton = new JFXButton("去注册");
    private JFXButton registerButton = new JFXButton("注册");
    private JFXButton switchToLoginButton = new JFXButton("返回登录");

    public LoginPage() {
        setupUI();
        setupActions();
        setupTestStage();
        this.getStylesheets().add("dark-theme.css");

        sendAndReceiveDataResponse("/register", Map.of("name", "向辉", "username", "100000", "password", "admin"));
        sendAndReceiveDataResponse("/register", Map.of("name", "tst", "username", "201921000", "password", "admin"));
        sendAndReceiveDataResponse("/register", Map.of("name", "wzk", "username", "201921001", "password", "admin"));
        sendAndReceiveDataResponse("/register", Map.of("name", "why", "username", "201921002", "password", "admin"));

    }

    public void setupTestStage() {
        //另外显示一个页面用于添加测试数据

        testStage.setX(100);
        VBox testVBox = new VBox();
        Button testButton = new Button("添加测试数据");

        testButton.setOnAction(event -> {
            sendAndReceiveDataResponse("/test/addTestData",null);
        });

        Button registerButton = new Button("注册测试账户");
        registerButton.setOnAction(event -> {
            sendAndReceiveDataResponse("/register", Map.of("name", "向辉", "username", "199900100000", "password", "123456"));
            sendAndReceiveDataResponse("/register", Map.of("name", "李学庆", "username", "199900100001", "password", "123456"));
            sendAndReceiveDataResponse("/register", Map.of("name", "谭绍庭", "username", "202300300000", "password", "123456"));
            sendAndReceiveDataResponse("/register", Map.of("name", "王志凯", "username", "202300300001", "password", "123456"));
            sendAndReceiveDataResponse("/register", Map.of("name", "张小三", "username", "202300300002", "password", "123456"));
        });

        Button adminButton = new Button("填入管理员账号");
        adminButton.setOnAction(event -> {
            usernameField.setText("admin");
            passwordField.setText("123456");
        });

        Button teacherButton1 = new Button("填入教师账号1");
        teacherButton1.setOnAction(event -> {
            nameField.setText("向辉");
            usernameField.setText("199900100000");
            passwordField.setText("123456");
        });

        Button teacherButton2 = new Button("填入教师账号2");
        teacherButton2.setOnAction(event -> {
            nameField.setText("李学庆");
            usernameField.setText("199900100001");
            passwordField.setText("123456");
        });

        Button studentButton1 = new Button("填入学生账号1");
        studentButton1.setOnAction(event -> {
            nameField.setText("谭绍庭");
            usernameField.setText("202300300000");
            passwordField.setText("123456");
        });

        Button studentButton2 = new Button("填入学生账号2");
        studentButton2.setOnAction(event -> {
            nameField.setText("王志凯");
            usernameField.setText("202300300001");
            passwordField.setText("123456");
        });

        Button studentButton3 = new Button("填入学生账号3");
        studentButton3.setOnAction(event -> {
            nameField.setText("张小三");
            usernameField.setText("202300300002");
            passwordField.setText("123456");
        });


        testVBox.getChildren().addAll(testButton, registerButton, adminButton, teacherButton1, teacherButton2, studentButton1, studentButton2, studentButton3);
        testVBox.setAlignment(javafx.geometry.Pos.CENTER);
        testStage.setScene(new Scene(testVBox, 300, 200));
        testStage.show();
        testStage.setAlwaysOnTop(true);
    }

    private void setupUI() {
        this.setAlignment(javafx.geometry.Pos.CENTER);
        this.setVgap(10);
       this.addColumn(0, usernamelabel,passwordlabel);
        this.addColumn(1, usernameField,passwordField);
        this.addRow(2, loginButton,switchToRegisterButton);
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
            testStage.close();
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
        this.addColumn(0, namelabel,usernamelabel,passwordlabel);
        this.addColumn(1, nameField,usernameField,passwordField);
        this.addRow(3, registerButton,switchToLoginButton);
        nameField.setVisible(true);
        registerButton.setVisible(true);
        switchToLoginButton.setVisible(true);
    }

    private void switchToLogin() {
        this.getChildren().clear();
        this.addColumn(0, usernamelabel,passwordlabel);
        this.addColumn(1, usernameField,passwordField);
        this.addRow(2, loginButton,switchToRegisterButton);
    }

    private void changeScene() {
        Menu menu = new Menu();
        Stage stage = (Stage) this.getScene().getWindow();
        Scene scene = new Scene(menu, 1400, 800);
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
