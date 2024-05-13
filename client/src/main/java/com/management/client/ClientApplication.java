package com.management.client;


import com.management.client.page.LoginPage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.burningwave.core.classes.Modules;

import static com.management.client.util.HttpClientUtil.sendAndReceiveDataResponse;

public class ClientApplication extends Application {
    @Override
    public void start(Stage stage) {
        //sendAndReceiveDataResponse("/test/addTestData",null);
        Scene scene = new Scene(new LoginPage(), 300, 400);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Modules.create().exportAllToAll();
        launch();
    }
}