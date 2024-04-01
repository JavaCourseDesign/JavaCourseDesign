package com.management.front.controller;



import com.management.front.TestApplication;
import com.management.front.request.DataResponse;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static com.management.front.util.HttpClientUtil.*;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;
    @FXML
    Button loginButton;
    @FXML
    Button registerButton;
    @FXML
    public void initialize() {
        usernameField.setText("why");
        passwordField.setText("123456");

    }
    private void goNext() throws IOException {
        Stage stage=new Stage();
        FXMLLoader fxmlLoader=new FXMLLoader(TestApplication.class.getResource("adminFxml/menu.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 800, 400);
        stage.setScene(scene);//舞台设置场景
        stage.show();//舞台展现
    }

    @FXML
    public void onLoginButton(MouseEvent event) throws IOException, URISyntaxException, InterruptedException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if(login(username,password))
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("登录成功");
            alert.showAndWait();
            goNext();
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("登录失败");
            alert.showAndWait();
        }
    }
    @FXML
    public void onRegisterButton(MouseEvent event) throws IOException {
        Map<String,String> map = new HashMap<>();
        map.put("username",usernameField.getText());
        map.put("password",passwordField.getText());
        DataResponse response=sendAndReceiveDataResponse("/register/teacher",map);
        if(response.getCode()==1)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("注册成功");
            alert.showAndWait();
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("注册失败");
            alert.showAndWait();
        }
    }


}

