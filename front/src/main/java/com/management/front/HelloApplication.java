package com.management.front;


import com.management.front.page.LoginPage;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // 创建背景图片
        Image backgroundImage = new Image(getClass().getResourceAsStream("images/background.png"));
        BackgroundImage background = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT
        );

        // 创建背景面板
        Pane backgroundPane = new Pane();
        backgroundPane.setBackground(new Background(background));
        backgroundPane.setMinSize(1400, 800);

        // 创建登录界面
        LoginPage loginPage = new LoginPage();
        loginPage.setAlignment(Pos.CENTER); // 居中对齐
        loginPage.setStyle("-fx-background-color: rgba(255, 255, 255, 0);"); // 设置背景色及透明度
        loginPage.setPrefSize(1400, 800); // 设置登录界面大小

        // 将登录界面添加到背景面板中
        backgroundPane.getChildren().add(loginPage);

        // 创建场景并设置背景面板为根节点
        Scene scene = new Scene(backgroundPane);

        // 添加 CSS 文件
        scene.getStylesheets().add(getClass().getResource("loginPageStyle.css").toExternalForm());

        stage.setTitle("山东大学学生管理系统-登录界面");
        // 设置窗口图标
        stage.getIcons().add(new Image(getClass().getResourceAsStream("images/sduicon.jpg")));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
