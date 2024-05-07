package com.management.front;


import com.management.front.page.LoginPage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static com.management.front.util.HttpClientUtil.sendAndReceiveDataResponse;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {

        sendAndReceiveDataResponse("/test/addTestData",null);
        Scene scene = new Scene(new LoginPage(), 300, 400);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}