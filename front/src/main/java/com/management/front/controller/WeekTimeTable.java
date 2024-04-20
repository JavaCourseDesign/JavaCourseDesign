package com.management.front.controller;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Scale;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WeekTimeTable extends Pane{
    private static final double HOUR_HEIGHT = 30; // 假设每小时60像素
    private static final double DAY_WIDTH = 75; // 每天的宽度
    private static final double LEFT_BAR_WIDTH = 50;
    private static final double TOP_BAR_HEIGHT = 50;
    private static final int BEGIN_TIME = 6;
    private static final double NORMAL_OPACITY = 0.4;

    private static final String originalStyle = "-fx-background-color: #181818; -fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;";

    private static final String[] weekDays = {"一", "二", "三", "四", "五", "六", "日"};
    private static final String[] timeLine = {"06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00",
            "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00"};
    private final List<Map> events=new ArrayList<>();
    private int weekNum = 1;
    private double scrollAccumulator = 0.0; // 滚动累积器
    private static final double SCROLL_THRESHOLD = 100.0; // 滚动阈值，调整这个值来设置滚动敏感度
    private static final LocalDate START_DATE = LocalDate.of(2023, 1, 1); // 开学第一天的日期



    public WeekTimeTable() {
        Scale scale = new Scale();
        this.getTransforms().add(scale);

        // 一旦此Group被添加到父节点，就监听父节点尺寸变化以调整缩放
        this.parentProperty().addListener((obs, oldParent, newParent) -> {
            if (newParent instanceof Pane) {
                Pane parent = (Pane) newParent;

                // 监听宽度变化并相应地调整缩放系数
                parent.widthProperty().addListener((observable, oldValue, newValue) -> {
                    double scaleFactor = newValue.doubleValue() / this.getTotalWidth();
                    scale.setX(scaleFactor);
                });

                // 监听高度变化并相应地调整缩放系数
                parent.heightProperty().addListener((observable, oldValue, newValue) -> {
                    double scaleFactor = newValue.doubleValue() / this.getTotalHeight();
                    scale.setY(scaleFactor);
                });
            }
        });


        Rectangle background = new Rectangle((int) (LEFT_BAR_WIDTH + DAY_WIDTH * 7), (int) (TOP_BAR_HEIGHT + HOUR_HEIGHT * 17));
        background.setFill(Color.web("#181818"));
        this.getChildren().add(background);

        drawTopBar();
        drawLeftBar();
        initScrollHandling();
    }

    private void initScrollHandling() {
        this.setOnScroll(event -> {
            scrollAccumulator += event.getDeltaY(); // 累积滚动量

            // 检查累积的滚动量是否达到阈值
            if (Math.abs(scrollAccumulator) >= SCROLL_THRESHOLD) {
                if (scrollAccumulator < 0) {
                    weekNum++;
                } else {
                    weekNum--;
                }
                displayWeek(); // 刷新显示周信息
                scrollAccumulator = 0; // 重置累积器
            }
        });
    }
    private void drawTopBar() {
        LocalDate weekStartDate = START_DATE.plusWeeks(weekNum - 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d");

        for (int i = 0; i < 7; i++) {
            Pane topBarCell = new Pane();
            topBarCell.setStyle(originalStyle);
            topBarCell.setPrefSize(DAY_WIDTH, TOP_BAR_HEIGHT + HOUR_HEIGHT * 17);
            topBarCell.setLayoutX(LEFT_BAR_WIDTH + i * DAY_WIDTH);
            topBarCell.setLayoutY(0);
            topBarCell.setOpacity(0.3);

            this.getChildren().add(topBarCell);

            // 显示周几和日期
            LocalDate currentDate = weekStartDate.plusDays(i);
            String displayText = "  "+weekDays[i] + "\n " + currentDate.format(formatter);
            Text day = new Text(displayText);
            day.setFont(Font.font("Courier New", 12));
            day.setFill(Color.WHITE);
            day.setLayoutX(LEFT_BAR_WIDTH + i * DAY_WIDTH + DAY_WIDTH / 2 - 20); // 调整位置以居中显示
            day.setLayoutY(TOP_BAR_HEIGHT / 2);
            this.getChildren().add(day);
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

            this.getChildren().add(leftBarCell);

            Text time = new Text(timeLine[i]);
            time.setFont(Font.font("Arial", 12));
            time.setFill(Color.WHITE);
            time.setLayoutX(LEFT_BAR_WIDTH/6);
            time.setLayoutY(TOP_BAR_HEIGHT + i * HOUR_HEIGHT + 5);
            this.getChildren().add(time);
        }
    }
    public void setEvents(List<Map> events) {
        this.events.clear();
        this.events.addAll(events);//不直接赋值，避免引用问题
        //System.out.println("events1:"+this.events);
        displayWeek();
    }

    public void displayWeek() {
        clear();
        for (Map event : events) {
            //System.out.println("event:"+event);
            if (event.get("week").toString().equals(weekNum+"")) {
                //System.out.println("addEvent:"+event);
                addEvent(event);
            }
        }
    }

    private void addEvent(Map event) {

        Pane eventPane = new Pane();//适应文本有问题，可能要换回pane
        eventPane.setLayoutX(LEFT_BAR_WIDTH+(Integer.parseInt((String) event.get("day"))-1)*DAY_WIDTH);
        eventPane.setLayoutY(TOP_BAR_HEIGHT+(transferTime(Double.parseDouble((String) event.get("time")))-BEGIN_TIME)*HOUR_HEIGHT);
        eventPane.setPrefHeight(transferTime(Double.parseDouble((String) event.get("duration")))*HOUR_HEIGHT);
        eventPane.setPrefWidth(DAY_WIDTH);
        //eventPane.setMaxWidth(DAY_WIDTH);
        eventPane.setOpacity(NORMAL_OPACITY);
        eventPane.setOnMouseEntered(e -> eventPane.setOpacity(0.8));
        eventPane.setOnMouseExited(e -> eventPane.setOpacity(0.4));
        eventPane.setStyle("-fx-background-color: "+stringToHexColor((String) event.get("name"))+"; -fx-border-color: black; -fx-border-width: 0; -fx-padding: 0; -fx-background-radius: 5");


        Text name = new Text(event.get("name")+"");
        name.setTextAlignment(TextAlignment.CENTER);
        name.setFont(Font.font("Verdana", 12));
        name.wrappingWidthProperty().bind(eventPane.widthProperty());

        Text location = new Text((String) event.get("location"));
        location.setTextAlignment(TextAlignment.CENTER);
        location.setFont(Font.font("Arial", 10));
        location.wrappingWidthProperty().bind(eventPane.widthProperty());

        VBox eventInfo = new VBox(name, location);
        //eventPane.setGraphic(eventInfo);
        eventPane.getChildren().add(eventInfo);

        this.getChildren().add(eventPane);
    }

    public static double transferTime(double t)
    {
        return (int)t + ((t - (int)t) *10/6);
    }

    public void clear()
    {
        this.getChildren().clear();
        Rectangle background = new Rectangle((int) (LEFT_BAR_WIDTH + DAY_WIDTH * 7), (int) (TOP_BAR_HEIGHT + HOUR_HEIGHT * 17));
        background.setFill(Color.web("#181818"));
        this.getChildren().add(background);
        drawTopBar();
        drawLeftBar();
    }

    public double getTotalWidth()
    {
        return LEFT_BAR_WIDTH + DAY_WIDTH * 7;
    }
    public double getTotalHeight()
    {
        return TOP_BAR_HEIGHT + HOUR_HEIGHT * 17;
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



