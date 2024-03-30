package com.management.front;


import com.management.front.controller.LoginPage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static com.management.front.util.HttpClientUtil.sendAndReceiveDataResponse;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        sendAndReceiveDataResponse("/test/addTestData",null);

        Scene scene = new Scene(new LoginPage(), 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}