package com.management.front.controller;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class Menu extends TreeView<String> {
    public Menu() {
        TreeItem<String> root = new TreeItem<>("菜单");
        root.setExpanded(true);
        TreeItem<String> item1 = new TreeItem<>("学生管理");
        TreeItem<String> item2 = new TreeItem<>("课程管理");
        TreeItem<String> item3 = new TreeItem<>("教师管理");
        TreeItem<String> item4 = new TreeItem<>("班级管理");
        root.getChildren().addAll(item1, item2, item3, item4);
        this.setRoot(root);
    }
}
