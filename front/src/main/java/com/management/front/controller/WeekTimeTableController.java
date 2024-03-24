package com.management.front.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;

public class WeekTimeTableController {
    @FXML
    public GridPane scheduleGrid;
    @FXML
    private AnchorPane day1, day2, day3, day4, day5, day6, day7;
    @FXML
    private BorderPane table;
    @FXML
    private VBox background;

    private static final double HOUR_HEIGHT = 30; // 假设每小时60像素
    private static final double BEGIN_TIME = 6;

    // 其他代码保持不变
    @FXML
    private void initialize() {
        // 初始化时为每天的VBox应用样式
        //applyStyles(day1, day2, day3, day4, day5, day6, day7);
        /*BackgroundImage backgroundImage = new BackgroundImage(new Image("file:D:/23993/OneDrive/IDEA/JavaCourseDesign/front/src/main/resources/com/management/front/images/grid_background.png"),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        table.setBackground(new Background(backgroundImage));*/
        //table.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        //scheduleGrid.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        background.setBackground(new Background(new BackgroundFill(Color.rgb(30,30,30), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private void applyStyles(AnchorPane... days) {
        for (AnchorPane day : days) {
            day.getStyleClass().add("day-box");
        }
    }

    public void addEvent(int day, String eventName, String eventLocation, double startTime, double endTime) {
        Pane eventBlock = new Pane();
        //eventBlock.setStyle("-fx-background-color: "+"#a3d3a5"+"; -fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");
        System.out.println(stringToHexColor(eventName));
        eventBlock.setStyle("-fx-background-color: "+stringToHexColor(eventName)+"; -fx-border-color: black; -fx-border-width: 0; -fx-padding: 0; -fx-background-radius: 5");
        eventBlock.setOpacity(0.7);

        double startY = (startTime-BEGIN_TIME) * HOUR_HEIGHT; // 计算开始时间对应的Y坐标
        double height = (endTime - startTime) * HOUR_HEIGHT; // 计算事件持续时间对应的高度

        System.out.println(height);

        eventBlock.setLayoutY(startY);
        eventBlock.setPrefHeight(height);
        eventBlock.setPrefWidth(75);

        Text name = new Text(eventName);
        name.setTextAlignment(TextAlignment.CENTER);
        name.setFont(Font.font("Verdana", 12));
        name.wrappingWidthProperty().bind(eventBlock.widthProperty());

        Text location = new Text(eventLocation);
        location.setTextAlignment(TextAlignment.CENTER);
        location.setFont(Font.font("Arial", 10));
        location.wrappingWidthProperty().bind(eventBlock.widthProperty());

        VBox eventInfo = new VBox(name, location);
        //Label Info = new Label(eventName + "\n" + location);

        eventBlock.getChildren().add(eventInfo);



        // 鼠标进入事件监听器
        eventBlock.setOnMouseEntered(e -> {
            //eventBlock.setStyle("-fx-background-color: "+stringToHexColor(eventName)+"; -fx-border-color: red; -fx-border-width: 2; -fx-padding: 0;");
            eventBlock.setOpacity(0.9); // 可以通过增加透明度来增强高亮效果
        });

        // 鼠标退出事件监听器
        eventBlock.setOnMouseExited(e -> {
            //eventBlock.setStyle(originalStyle);
            eventBlock.setOpacity(0.7); // 恢复原始透明度
        });

        getDayBox(day).getChildren().add(eventBlock);
    }

    private AnchorPane getDayBox(int day) {
        return switch (day) {
            case 1 -> day1;
            case 2 -> day2;
            case 3 -> day3;
            case 4 -> day4;
            case 5 -> day5;
            case 6 -> day6;
            case 7 -> day7;
            default -> throw new IllegalArgumentException("Invalid day: " + day);
        };
    }

    public static String stringToHexColor(String input) {
        int hashCode = input.hashCode();// 获取输入字符串的哈希码
        int positiveHashCode = Math.abs(hashCode);// 确保是正数
        String hexString = Integer.toHexString(positiveHashCode);// 转换为16进制字符串
        String colorCode = ("000000" + hexString).substring(hexString.length());// 确保结果是6位的，如果不够长，从前面开始补零
        if (colorCode.length() > 6) {// 取最后六位作为颜色代码
            colorCode = colorCode.substring(colorCode.length() - 6);
        }
        return "#" + colorCode;
    }
}
