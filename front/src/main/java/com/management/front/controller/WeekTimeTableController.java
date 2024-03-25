package com.management.front.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;


import java.util.ArrayList;

public class WeekTimeTableController {
    @FXML
    private Group table;
    private static final double HOUR_HEIGHT = 30; // 假设每小时60像素
    private static final double DAY_WIDTH = 75; // 每天的宽度
    private static final double LEFT_BAR_WIDTH = 50;
    private static final double TOP_BAR_HEIGHT = 50;
    private static final int BEGIN_TIME = 6;

    private static final String originalStyle = "-fx-background-color: #181818; -fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;";

    private static final String[] weekDays = {"一", "二", "三", "四", "五", "六", "日"};
    private static final String[] timeLine = {"06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00",
            "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00"};

    // 其他代码保持不变
    @FXML
    private void initialize() {
        Rectangle background = new Rectangle((int) (LEFT_BAR_WIDTH + DAY_WIDTH * 7), (int) (TOP_BAR_HEIGHT + HOUR_HEIGHT * 17));
        background.setFill(Color.web("#181818"));
        table.getChildren().add(background);

        drawTopBar();
        drawLeftBar();
    }
    private void drawTopBar() {
        for (int i = 0; i < 7; i++) {
            Pane topBarCell = new Pane();
            topBarCell.setStyle(originalStyle);
            topBarCell.setPrefSize(DAY_WIDTH, TOP_BAR_HEIGHT+HOUR_HEIGHT*17);
            topBarCell.setLayoutX(LEFT_BAR_WIDTH + i * DAY_WIDTH);
            topBarCell.setLayoutY(0);
            topBarCell.setOpacity(0.3);

            table.getChildren().add(topBarCell);

            Text day = new Text(weekDays[i]);
            day.setFont(Font.font("Courier New", 12));
            day.setFill(Color.WHITE);
            day.setLayoutX(LEFT_BAR_WIDTH + i * DAY_WIDTH+ DAY_WIDTH/2-5);
            day.setLayoutY(TOP_BAR_HEIGHT/3);
            table.getChildren().add(day);
        }
    }
    private void drawLeftBar() {
        for (int i = 0; i < 17; i++) {
            Pane leftBarCell = new Pane();
            leftBarCell.setStyle(originalStyle);
            leftBarCell.setPrefSize(LEFT_BAR_WIDTH+DAY_WIDTH*7, HOUR_HEIGHT);
            leftBarCell.setLayoutX(0);
            leftBarCell.setLayoutY(TOP_BAR_HEIGHT + i * HOUR_HEIGHT);
            leftBarCell.setOpacity(0.8);

            table.getChildren().add(leftBarCell);

            Text time = new Text(timeLine[i]);
            time.setFont(Font.font("Arial", 12));
            time.setFill(Color.WHITE);
            time.setLayoutX(LEFT_BAR_WIDTH/6);
            time.setLayoutY(TOP_BAR_HEIGHT + i * HOUR_HEIGHT + 5);
            table.getChildren().add(time);
        }
    }

    public void addEvent(int day, String eventName, String eventLocation, double startTime, double endTime) {
        startTime = transferTime(startTime);
        endTime = transferTime(endTime);

        Pane event = new Pane();
        event.setStyle("-fx-background-color: "+stringToHexColor(eventName)+"; -fx-border-color: black; -fx-border-width: 0; -fx-padding: 0; -fx-background-radius: 5");

        double startY = (startTime-BEGIN_TIME) * HOUR_HEIGHT; // 计算开始时间对应的Y坐标
        double height = (endTime - startTime) * HOUR_HEIGHT; // 计算事件持续时间对应的高度

        event.setLayoutX(LEFT_BAR_WIDTH + (day-1) * DAY_WIDTH);
        event.setLayoutY(TOP_BAR_HEIGHT+startY);
        event.setPrefHeight(height);
        event.setPrefWidth(DAY_WIDTH);

        Text name = new Text(eventName);
        name.setTextAlignment(TextAlignment.CENTER);
        name.setFont(Font.font("Verdana", 12));
        name.wrappingWidthProperty().bind(event.widthProperty());

        Text location = new Text(eventLocation);
        location.setTextAlignment(TextAlignment.CENTER);
        location.setFont(Font.font("Arial", 10));
        location.wrappingWidthProperty().bind(event.widthProperty());

        VBox eventInfo = new VBox(name, location);
        event.getChildren().add(eventInfo);

        event.setOnMouseEntered(e -> event.setOpacity(0.8));
        event.setOnMouseExited(e -> event.setOpacity(0.4));

        table.getChildren().add(event);
    }

    public static double transferTime(double t)
    {
        return (int)t + ((t - (int)t) *10/6);
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



