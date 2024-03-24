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

import static com.management.front.util.HttpClientUtil.sendAndReceiveObject;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("addStudent.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("week_time_table.fxml"));
        Parent root = fxmlLoader.load();
        Map<String,String> test=new HashMap<>();
        test.put("personId","1");
        System.out.println(sendAndReceiveObject("/getAllEvents", test));

        WeekTimeTableController controller = fxmlLoader.getController();
        /*controller.addEvent(1, "会议", "办公室", 6.0, 22.0);
        controller.addEvent(2, "语文", "办公室", 7.0, 21.0);
        controller.addEvent(3, "会议2", "办公室", 8.0, 20.0);
        controller.addEvent(3, "测试会议", "办公室", 9.0, 11.0);
        controller.addEvent(4, "语文", "办公室", 9.0, 19.0);
        controller.addEvent(5, "会议3", "办公室", 10.0, 18.0);*/
        controller.addEvent(1, "线性代数", "办公室", 8.0, 9.83);
        controller.addEvent(1, "高等数学（2）", "办公室", 10.17, 12);
        controller.addEvent(1, "体育（2）", "办公室", 14.0, 15.83);
        controller.addEvent(1, "高级程序开发", "办公室", 16.17, 18.0);
        controller.addEvent(1, "创业实务以北斗为例", "办公室", 19.0, 20.83);
        controller.addEvent(2, "会议3", "办公室", 10.0, 18.0);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}