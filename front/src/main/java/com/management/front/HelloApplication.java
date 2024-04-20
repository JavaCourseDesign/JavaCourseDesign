package com.management.front;


import com.management.front.controller.LoginPage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

import static com.management.front.util.HttpClientUtil.sendAndReceiveDataResponse;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        sendAndReceiveDataResponse("/test/addTestData",null);

        Scene scene = new Scene(new LoginPage(), 1400, 800);
        //将 CSS 文件添加到场景中
        scene.getStylesheets().add("file:///E:/JavaCourseDesign/front/src/main/resources/com/management/front/loginPageStyle.css");
        stage.setTitle("山东大学学生管理系统-登录界面");
        stage.getIcons().add(new Image("E:\\JavaCourseDesign\\front\\src\\main\\resources\\com\\management\\front\\images\\sduicon.jpg"));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}