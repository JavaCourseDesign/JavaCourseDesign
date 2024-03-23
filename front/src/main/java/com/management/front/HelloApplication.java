package com.management.front;

import com.management.front.controller.WeekTimeTableController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.management.front.util.HttpClientUtil.sendAndReceive;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("addStudent.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("week_time_table.fxml"));
        Parent root = fxmlLoader.load();
        Map<String,String> test=new HashMap<>();
        test.put("personId","1");
        System.out.println(sendAndReceive("/getAllEvents", test));

        WeekTimeTableController controller = fxmlLoader.getController();
        controller.addEvent(1, "会议", "办公室", 6.0, 22.0);
        controller.addEvent(2, "语文", "办公室", 7.0, 21.0);
        controller.addEvent(3, "会议", "办公室", 12.0, 15.0);
        controller.addEvent(4, "语文", "办公室", 9.0, 12.0);
        controller.addEvent(7, "会议", "办公室", 7.0, 13.0);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}