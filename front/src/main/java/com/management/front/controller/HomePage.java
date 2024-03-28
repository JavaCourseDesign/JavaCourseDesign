package com.management.front.controller;

import javafx.scene.layout.BorderPane;

public class HomePage extends BorderPane{
    public HomePage(){
        WeekTimeTable weekTimeTable = new WeekTimeTable();
        weekTimeTable.addEvent(1, "线性代数", "办公室", 8.0, 9.5);
        weekTimeTable.addEvent(1, "高等数学（2）", "办公室", 10.1, 12);
        weekTimeTable.addEvent(1, "体育（2）", "办公室", 14.0, 15.5);
        weekTimeTable.addEvent(1, "高级程序开发", "办公室", 16.1, 18.0);
        weekTimeTable.addEvent(1, "创业实务以北斗为例", "办公室", 19.0, 20.5);
        weekTimeTable.addEvent(3, "会议3", "办公室", 10.0, 18.0);
        this.setCenter(weekTimeTable);
    }
}
