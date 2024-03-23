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

    private static final double HOUR_HEIGHT = 30; // 假设每小时60像素

    public void addEvent(int day, String eventName, String location, double startTime, double endTime) {
        Pane eventBlock = new Pane();
        eventBlock.setStyle("-fx-background-color: #a3d3f5; -fx-border-color: black; -fx-border-width: 2; -fx-padding: 5;");

        double startY = startTime * HOUR_HEIGHT; // 计算开始时间对应的Y坐标
        double height = (endTime - startTime) * HOUR_HEIGHT; // 计算事件持续时间对应的高度

        eventBlock.setLayoutY(startY);
        eventBlock.setPrefHeight(height);

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
}
