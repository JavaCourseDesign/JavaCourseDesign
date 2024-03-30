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
import java.util.Optional;

import static com.management.front.util.HttpClientUtil.request;

public class TeacherManagementPage extends SplitPane {
    private TableView<Map> teacherTable = new TableView<>();
    private VBox controlPanel = new VBox();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    private Button addButton = new Button("Add");
    private Button deleteButton = new Button("Delete");
    private Button updateButton = new Button("Update");
    private Button refreshButton = new Button("Refresh");

    private TextField numField = new TextField("teacherId");
    private TextField nameField = new TextField("name");
    private TextField genderField = new TextField("gender");
    private TextField majorField = new TextField("title");

    public TeacherManagementPage() {
        this.setWidth(1000);
        initializeTable();
        initializeControlPanel();
        try {
            displayTeachers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeTable() {

        //建立列
        TableColumn<Map, String> teacherId = new TableColumn<>("teacherID");
        TableColumn<Map, String> teacherName = new TableColumn<>("Name");
        TableColumn<Map, String> teacherGender = new TableColumn<>("Gender");
        TableColumn<Map, String> teacherMajor = new TableColumn<>("Title");

        //把map填入单元格
        teacherId.setCellValueFactory(new MapValueFactory<>("teacherId"));
        teacherName.setCellValueFactory(new MapValueFactory<>("name"));
        teacherGender.setCellValueFactory(new MapValueFactory<>("gender"));
        teacherMajor.setCellValueFactory(new MapValueFactory<>("title"));

        teacherTable.getColumns().addAll(teacherId, teacherName, teacherGender, teacherMajor);
        this.getItems().add(teacherTable);
    }

    private void initializeControlPanel() {
        controlPanel.setMinWidth(200);
        controlPanel.setSpacing(10);

        addButton.setOnAction(event -> addTeacher());
        deleteButton.setOnAction(event -> deleteTeacher());
        updateButton.setOnAction(event -> updateTeacher());
        refreshButton.setOnAction(event -> refreshTeachers());

        teacherTable.selectionModelProperty().get().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null)
            {
                numField.setText((String) newValue.get("teacherId"));
                nameField.setText((String) newValue.get("name"));
                genderField.setText((String) newValue.get("gender"));
                majorField.setText((String) newValue.get("title"));
            }
        });

        controlPanel.getChildren().addAll(numField, nameField, genderField, majorField, addButton, deleteButton, updateButton, refreshButton);
        this.getItems().add(controlPanel);
    }

    private void displayTeachers() throws IOException {
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getAllTeachers", null).getData()));
        teacherTable.setItems(observableList);
    }

    private void addTeacher() {
        // 实现添加教师的逻辑
        Map<String,String> m=new HashMap<>();
        m.put("teacherId",numField.getText());//必须跟后端属性命名一致
        m.put("name",nameField.getText());
        m.put("gender",genderField.getText());
        m.put("title",majorField.getText());

        System.out.println(m);

        DataResponse r=request("/addTeacher",m);

        refreshTeachers();

        if(r.getCode()==-1)
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("警告");
            alert.setContentText(r.getMsg());
            alert.showAndWait();
        }
        else
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(r.getMsg());
            alert.showAndWait();
        }
    }

    private void deleteTeacher() {
        // 实现删除选中的教师逻辑
        Map form = teacherTable.getSelectionModel().getSelectedItem();
        if(form==null)
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("未选择，无法删除");
            alert.showAndWait();
        }
        else
        {
            Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("警告");
            alert.setContentText("确定要删除吗？");
            Optional<ButtonType> result=alert.showAndWait();
            if(result.get()==ButtonType.OK)
            {
                DataResponse r=request("/deleteTeacher",form);
                System.out.println(form);
                System.out.println(r);

                refreshTeachers();

                if(r.getCode()==0)
                {
                    Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                    alert1.setContentText("删除成功");
                    alert1.showAndWait();
                }
            }
        }
    }

    private void updateTeacher() {
        // 实现更新教师信息的逻辑
        Map form = teacherTable.getSelectionModel().getSelectedItem();
        if(form==null)
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("未选择，无法更新");
            alert.showAndWait();
        }
        else
        {

            form.put("teacherId",numField.getText());
            form.put("name",nameField.getText());
            form.put("gender",genderField.getText());
            form.put("title",majorField.getText());

            Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("警告");
            alert.setContentText("确定要更新吗？");
            Optional<ButtonType> result=alert.showAndWait();
            if(result.get()==ButtonType.OK)
            {
                DataResponse r=request("/updateTeacher",form);
                System.out.println(form);
                System.out.println(r);

                refreshTeachers();

                if(r.getCode()==0)
                {
                    Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                    alert1.setContentText("更新成功");
                    alert1.showAndWait();
                }
            }
        }
    }

    private void refreshTeachers() {
        try {
            displayTeachers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}