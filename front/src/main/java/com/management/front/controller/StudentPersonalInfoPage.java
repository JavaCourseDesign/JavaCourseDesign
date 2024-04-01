package com.management.front.controller;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

import static com.management.front.util.HttpClientUtil.request;

public class StudentPersonalInfoPage extends TabPane {

    public StudentPersonalInfoPage(Map student) {

        System.out.println(student);

        this.getTabs().add(new BasicInfoTab(student));
    }
}
class BasicInfoTab extends Tab {
    Map student=new HashMap<>();
    VBox vBox=new VBox();
    GridPane gridPane=new GridPane();
    public BasicInfoTab(Map student) {
        this.setText("基本信息");
        this.setContent(vBox);
        this.student=student;
        vBox.getChildren().add(gridPane);
        Button saveButton = new Button("保存");
        saveButton.setOnMouseClicked(event -> save());
        vBox.getChildren().add(saveButton);
        refresh();
    }
    public void save() {

        request("/updateStudent", student);
    }
    public void refresh() {
        gridPane.addColumn(0,
                new Label("学号"),
                new Label("姓名"),
                new Label("性别"),
                new Label("专业"));
        gridPane.addColumn(1,
                new TextField(student.get("studentId").toString()),
                new TextField(student.get("name").toString()),
                new TextField(student.get("gender").toString()),
                new TextField(student.get("major").toString()));
    }
}
