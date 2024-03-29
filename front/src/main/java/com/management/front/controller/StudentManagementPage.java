package com.management.front.controller;

import com.management.front.request.DataResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.management.front.util.HttpClientUtil.request;
import static com.management.front.util.HttpClientUtil.sendAndReceiveObject;

public class StudentManagementPage extends SplitPane {
    private TableView<Map> studentTable = new TableView<>();
    private VBox controlPanel = new VBox();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();
    private Button addButton = new Button("Add");
    private Button deleteButton = new Button("Delete");
    private Button updateButton = new Button("Update");
    private Button refreshButton = new Button("Refresh");

    public StudentManagementPage() {
        this.setWidth(1000);
        initializeTable();
        initializeControlPanel();
        try {
            displayStudents();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeTable() {
        TableColumn<Map, String> studentId = new TableColumn<>("ID");
        studentId.setCellValueFactory(new MapValueFactory<>("studentId"));

        TableColumn<Map, String> studentName = new TableColumn<>("Name");
        studentName.setCellValueFactory(new MapValueFactory<>("name"));

        TableColumn<Map, String> studentGender = new TableColumn<>("Gender");
        studentGender.setCellValueFactory(new MapValueFactory<>("gender"));

        TableColumn<Map, String> studentMajor = new TableColumn<>("Major");
        studentMajor.setCellValueFactory(new MapValueFactory<>("major"));

        studentTable.getColumns().addAll(studentId, studentName, studentGender, studentMajor);
        this.getItems().add(studentTable);
    }

    private void initializeControlPanel() {
        controlPanel.setMinWidth(200);
        controlPanel.setSpacing(10);
        addButton.setOnAction(event -> addStudent());
        deleteButton.setOnAction(event -> deleteStudent());
        updateButton.setOnAction(event -> updateStudent());
        refreshButton.setOnAction(event -> refreshStudents());
        controlPanel.getChildren().addAll(addButton, deleteButton, updateButton, refreshButton);
        this.getItems().add(controlPanel);
    }

    private void displayStudents() throws IOException {
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getAllStudents", null).getData()));
        studentTable.setItems(observableList);
    }

    private void addStudent() {
        // 实现添加学生的逻辑
    }

    private void deleteStudent() {
        // 实现删除选中的学生逻辑
    }

    private void updateStudent() {
        // 实现更新学生信息的逻辑
    }

    private void refreshStudents() {
        try {
            displayStudents();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
