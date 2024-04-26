package com.management.front.page;

import com.jfoenix.controls.JFXTreeView;
import com.management.front.page.admin.*;
import com.management.front.page.student.CourseApplyPage;
import com.management.front.page.student.StudentHomeworkPage;
import com.management.front.page.student.StudentPersonalInfoPage;
import com.management.front.page.teacher.TeacherCourseMenuPage;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Pane;

public class Menu extends SplitPane {
    JFXTreeView<String> menu = new JFXTreeView<>();
    TreeItem<String> root = new TreeItem<>("菜单");
    TreeItem<String> item1 = new TreeItem<>("学生管理");
    TreeItem<String> item2 = new TreeItem<>("教师管理");
    TreeItem<String> item3 = new TreeItem<>("课程管理");
    TreeItem<String> item4 = new TreeItem<>("班级管理");
    TreeItem<String> item5 = new TreeItem<>("学生个人信息");
    TreeItem<String> item6 = new TreeItem<>("课程表");
    TreeItem<String> item7 = new TreeItem<>("创新实践管理");
    TreeItem<String> item8 = new TreeItem<>("课程申请");
    TreeItem<String> item9 = new TreeItem<>("学生日志信息管理");
    TreeItem<String> item10 = new TreeItem<>("测试上传文件");
    TreeItem<String> item11 = new TreeItem<>("学生作业界面");
    TreeItem<String> item13 = new TreeItem<>("成绩管理");
    TreeItem<String> item14 = new TreeItem<>("教师课程");

    public Menu() {
        //this.getStylesheets().add("dark-theme.css");

        root.setExpanded(true);
        menu.setShowRoot(false);
        menu.setPrefWidth(70);
        this.setDividerPositions(0.1);

        root.getChildren().addAll(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11, item13, item14);
        menu.setRoot(root);

        this.getItems().add(menu);
        this.getItems().add(new Pane());
        menu.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue.getValue()) {
                case "学生管理":
                    this.getItems().set(1, new StudentManagementPage());
                    break;
                case "教师管理":
                    this.getItems().set(1, new TeacherManagementPage());
                    break;
                case "课程管理":
                    this.getItems().set(1, new CourseManagementPage());
                    break;
                case "班级管理":
                    this.getItems().set(1, new ClazzManagementPage());
                    break;
                case "学生个人信息":
                    this.getItems().set(1, new StudentPersonalInfoPage());
                    break;
                case "课程表":
                    this.getItems().set(1, null);
                    break;
                case "创新实践管理":
                    this.getItems().set(1, new InnovationManagementPage());
                    break;
                case "课程申请":
                    this.getItems().set(1, new CourseApplyPage());
                    break;
                case "学生日志信息管理":
                    this.getItems().set(1, new StudentLogManagementPage());
                    break;
                case "测试上传文件":
                    this.getItems().set(1, new FileUploadPage());
                    break;
                case "学生作业界面":
                    this.getItems().set(1, new StudentHomeworkPage());
                    break;
                case "成绩管理":
                    this.getItems().set(1, null);
                    break;
                case "教师课程":
                    this.getItems().set(1, new TeacherCourseMenuPage());
                    break;
            }
        });
    }
}
