package com.management.front.controller;

import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class Menu extends SplitPane {
    TreeView<String> menu = new TreeView<>();
    public Menu(){
        TreeItem<String> root = new TreeItem<>("菜单");
        root.setExpanded(true);
        TreeItem<String> item1 = new TreeItem<>("学生管理");
        TreeItem<String> item2 = new TreeItem<>("课程管理");
        TreeItem<String> item3 = new TreeItem<>("教师管理");
        TreeItem<String> item4 = new TreeItem<>("班级管理");
        root.getChildren().addAll(item1, item2, item3, item4);
        menu.setRoot(root);
        this.getItems().add(menu);
        this.getItems().add(new Pane());
        menu.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue.getValue()) {
                case "学生管理": this.getItems().set(1,new WeekTimeTable());
                    break;
                case "课程管理": this.getItems().set(1,new HomePage());
                    break;
                case "教师管理": this.getItems().set(1,new StudentManagementPage());
                    break;
                case "班级管理":
                    break;
            }
        });
    }
}