package com.management.front.controller;

import com.management.front.request.DataResponse;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.management.front.util.HttpClientUtil.*;

public class LoginPage extends StackPane {
    public static String personId;
    private TextField idField = new TextField();
    private TextField usernameField = new TextField();
    private PasswordField passwordField = new PasswordField();
    private Button loginButton = new Button("登录");
    private Button registerButton = new Button("注册");

    public LoginPage() throws IOException {
        idField.setText("201921000");
        usernameField.setText("");
        passwordField.setText("admin");

        // 加载背景图片
        Image backgroundImage = new Image("file:E:/JavaCourseDesign/front/src/main/resources/com/management/front/images/background.png");
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setFitWidth(1400); // 设置图片宽度以填充整个界面
        backgroundImageView.setFitHeight(800); // 设置图片高度以填充整个界面
        backgroundImageView.setOpacity(1.0); // 设置图片透明度
        getChildren().add(backgroundImageView);

        // 创建垂直布局容器
        VBox loginBox = new VBox(10); // 设置垂直间距
        loginBox.setMaxSize(400, 300); // 设置最大尺寸
        loginBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5); -fx-padding: 20px; -fx-border-radius: 10px;"); // 设置背景色和样式

        // 添加登录界面元素到垂直布局容器中
        loginBox.getChildren().addAll(idField, usernameField, passwordField, loginButton, registerButton);

        // 设置垂直布局容器居中显示
        StackPane.setAlignment(loginBox, javafx.geometry.Pos.CENTER);

        // 将垂直布局容器添加到 StackPane 中
        getChildren().add(loginBox);

        loginButton.setOnMouseClicked(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            // 检查用户名是否为 "admin"，如果是则直接显示滑块验证窗口
            if ("admin".equals(username)) {
                showSliderVerificationDialog();
            } else {
                // 模拟用户名密码验证过程
                if (login(username, password)) {
                    // 用户名密码验证通过，显示滑块验证窗口
                    showSliderVerificationDialog();
                } else {
                    // 用户名密码验证失败
                    Alert loginAlert = new Alert(Alert.AlertType.ERROR);
                    loginAlert.setContentText("用户名或密码错误");
                    loginAlert.showAndWait();
                }
            }
        });

        registerButton.setOnMouseClicked(event -> {
            Map<String, String> map = new HashMap<>();
            map.put("id", idField.getText());
            map.put("username", usernameField.getText());
            map.put("password", passwordField.getText());
            DataResponse response = sendAndReceiveDataResponse("/register", map);
            if (response.getCode() == 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(response.getMsg());
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(response.getMsg());
                alert.showAndWait();
            }
        });
        // 添加样式类
        idField.getStyleClass().add("text-field");
        usernameField.getStyleClass().add("text-field");
        passwordField.getStyleClass().add("password-field");
        loginButton.getStyleClass().add("button");
        registerButton.getStyleClass().add("button");
    }

    // 显示滑块验证窗口的方法
    private void showSliderVerificationDialog() {
        WebView webView = new WebView();
        webView.getEngine().load(getClass().getResource("/com/management/front/HuaKuaiYanZhen.html").toExternalForm());
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Slider Verification");
        dialog.getDialogPane().setContent(webView);

        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType);

        AtomicBoolean verified = new AtomicBoolean(false); // 标志是否已验证通过

        // 监听滑块值的变化
        webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                // 获取滑块值
                String sliderValueStr = (String) webView.getEngine().executeScript("document.getElementById('slider').value");
                if (!"undefined".equals(sliderValueStr)) {
                    int sliderValue = Integer.parseInt(sliderValueStr);
                    if (sliderValue >= 90) {
                        verified.set(true); // 验证通过
                    }
                }
            }
        });


        // 当用户点击登录按钮时进行验证
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                if (verified.get()) {
                    // 滑块验证通过
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("滑块验证通过");
                    alert.showAndWait();

                    // 关闭对话框
                    dialog.close();

                    // 执行登录成功后的操作
                    Menu menu = new Menu();
                    Scene scene = new Scene(menu, 1400, 800);
                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    stage.setTitle("山东大学学生管理系统");
                    stage.getIcons().add(new Image("E:\\JavaCourseDesign\\front\\src\\main\\resources\\com\\management\\front\\images\\sduicon.jpg"));
                    stage.setScene(scene);
                    stage.show();

                    Alert loginAlert = new Alert(Alert.AlertType.INFORMATION);
                    loginAlert.setContentText("登录成功");
                    loginAlert.showAndWait();
                } else {
                    // 滑块验证未通过
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("请完成滑块验证");
                    alert.showAndWait();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    }

