package com.management.client.page.student;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class StudentHomeworkTab extends Tab{
    private VBox vBox=new StudentHomeworkPage();
    public StudentHomeworkTab() {
        this.setText("作业管理");
        vBox.setPrefHeight(1000);
        this.setContent(vBox);
    }
}
