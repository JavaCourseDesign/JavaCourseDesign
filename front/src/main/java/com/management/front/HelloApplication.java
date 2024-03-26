package com.management.front;

import com.management.front.controller.HomePage;
import com.management.front.controller.WeekTimeTable;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.management.front.util.HttpClientUtil.sendAndReceiveObject;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        
        Scene scene = new Scene(new HomePage());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}