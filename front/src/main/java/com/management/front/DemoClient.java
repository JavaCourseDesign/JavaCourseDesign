package com.management.front;

// JavaFX Application
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DemoClient extends Application {
    @Override
    public void start(Stage primaryStage) {
        String helloWorld = getHelloWorldFromServer();
        Label label = new Label(helloWorld);
        StackPane root = new StackPane();
        root.getChildren().add(label);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }

    private String getHelloWorldFromServer() {
        try {
            URL url = new URL("http://localhost:9090/helloworld");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            return content.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error fetching data";
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

