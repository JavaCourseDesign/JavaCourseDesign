package com.management.front.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class WeekTimeTableController {
    @FXML
    public GridPane scheduleGrid;
    @FXML
    private AnchorPane day1, day2, day3, day4, day5, day6, day7;
    @FXML
    private VBox time;

    private static final double HOUR_HEIGHT = 26; // 假设每小时60像素
    private static final double BEGIN_TIME = 6; // 假设每小时60像素

    // 其他代码保持不变
    @FXML
    private void initialize() {
        // 初始化时为每天的VBox应用样式
        applyStyles(day1, day2, day3, day4, day5, day6, day7);

    }

    private void applyStyles(AnchorPane... days) {
        for (AnchorPane day : days) {
            day.getStyleClass().add("day-box");
        }
    }

    public void addEvent(int day, String eventName, String location, double startTime, double endTime) {
        startTime-=BEGIN_TIME;
        Pane eventBlock = new Pane();
        //eventBlock.setStyle("-fx-background-color: "+"#a3d3a5"+"; -fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");
        System.out.println(stringToHexColor(eventName));
        eventBlock.setStyle("-fx-background-color: "+stringToHexColor(eventName)+"; -fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");

        double startY = startTime * HOUR_HEIGHT; // 计算开始时间对应的Y坐标
        double height = (endTime - startTime) * HOUR_HEIGHT; // 计算事件持续时间对应的高度

        eventBlock.setLayoutY(startY);
        eventBlock.setPrefHeight(height);
        eventBlock.setPrefWidth(50);

        Text eventInfo = new Text("\n"+eventName + "\n" + location);
        eventBlock.getChildren().add(eventInfo);

        getDayBox(day).getChildren().add(eventBlock);
    }

    private AnchorPane getDayBox(int day) {
        switch (day) {
            case 1: return day1;
            case 2: return day2;
            case 3: return day3;
            case 4: return day4;
            case 5: return day5;
            case 6: return day6;
            case 7: return day7;
            default: throw new IllegalArgumentException("Invalid day: " + day);
        }
    }

    public static String stringToHexColor(String input) {
        // 获取输入字符串的哈希码
        int hashCode = input.hashCode();

        // 确保是正数
        int positiveHashCode = Math.abs(hashCode);

        // 转换为16进制字符串
        String hexString = Integer.toHexString(positiveHashCode);

        // 确保结果是6位的，如果不够长，从前面开始补零
        String colorCode = ("000000" + hexString).substring(hexString.length());

        // 取最后六位作为颜色代码
        if (colorCode.length() > 6) {
            colorCode = colorCode.substring(colorCode.length() - 6);
        }

        return "#" + colorCode;
    }
}
