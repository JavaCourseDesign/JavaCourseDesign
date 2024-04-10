package com.management.front.controller;

import com.management.front.request.DataResponse;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.management.front.util.HttpClientUtil.*;

public class LoginPage extends VBox {
    public static String personId;
    private TextField idField=new TextField();
    private TextField usernameField=new TextField();
    private PasswordField passwordField=new PasswordField();
    private Button loginButton=new Button("登录");
    private Button registerButton=new Button("注册");

    public LoginPage() throws IOException {
        idField.setText("");
        usernameField.setText("admin");
        passwordField.setText("admin");

        this.setAlignment(javafx.geometry.Pos.CENTER);

        this.getChildren().addAll(idField,usernameField,passwordField,loginButton,registerButton);

        loginButton.setOnMouseClicked(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            if(login(username,password))
            {
                Map<String,String> map = new HashMap<>();
                map.put("username",username);
                DataResponse r=request("/findPersonIdByUsername",map);
                personId=(String)r.getData();
                Menu menu = new Menu();
                Scene scene = new Scene(menu, 1400, 800);
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("登录成功");
                alert.showAndWait();
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("登录失败");
                alert.showAndWait();
            }
        });

        registerButton.setOnMouseClicked(event -> {
            Map<String,String> map = new HashMap<>();
            map.put("id",idField.getText());
            map.put("username",usernameField.getText());
            map.put("password",passwordField.getText());
            DataResponse response=sendAndReceiveDataResponse("/register",map);
            if(response.getCode()==0)
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(response.getMsg());
                alert.showAndWait();
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(response.getMsg());
                alert.showAndWait();
            }
        });
    }
}
