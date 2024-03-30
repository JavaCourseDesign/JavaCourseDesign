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

import static com.management.front.util.HttpClientUtil.*;

public class StudentManagementPage extends SplitPane {
    private TableView<Map> studentTable = new TableView<>();
    private VBox controlPanel = new VBox();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    private Button addButton = new Button("Add");
    private Button deleteButton = new Button("Delete");
    private Button updateButton = new Button("Update");
    private Button refreshButton = new Button("Refresh");

    private TextField numField = new TextField("studentId");
    private TextField nameField = new TextField("name");
    private TextField genderField = new TextField("gender");
    private TextField majorField = new TextField("major");

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

        //建立列
        TableColumn<Map, String> studentId = new TableColumn<>("studentID");
        TableColumn<Map, String> studentName = new TableColumn<>("Name");
        TableColumn<Map, String> studentGender = new TableColumn<>("Gender");
        TableColumn<Map, String> studentMajor = new TableColumn<>("Major");

        //把map填入单元格
        studentId.setCellValueFactory(new MapValueFactory<>("studentId"));
        studentName.setCellValueFactory(new MapValueFactory<>("name"));
        studentGender.setCellValueFactory(new MapValueFactory<>("gender"));
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

        studentTable.selectionModelProperty().get().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null)
            {
                numField.setText((String) newValue.get("studentId"));
                nameField.setText((String) newValue.get("name"));
                genderField.setText((String) newValue.get("gender"));
                majorField.setText((String) newValue.get("major"));
            }
        });

        controlPanel.getChildren().addAll(numField, nameField, genderField, majorField, addButton, deleteButton, updateButton, refreshButton);
        this.getItems().add(controlPanel);
    }

    private void displayStudents() throws IOException {
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getAllStudents", null).getData()));
        studentTable.setItems(observableList);
    }

    private void addStudent() {
        // 实现添加学生的逻辑
        Map<String,String> m=new HashMap<>();
        m.put("studentId",numField.getText());//必须跟后端属性命名一致
        m.put("name",nameField.getText());
        m.put("gender",genderField.getText());
        m.put("major",majorField.getText());

        System.out.println(m);

        DataResponse r=request("/addStudent",m);

        refreshStudents();

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

    private void deleteStudent() {
        // 实现删除选中的学生逻辑
        Map form = studentTable.getSelectionModel().getSelectedItem();
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
                DataResponse r=request("/deleteStudent",form);
                System.out.println(form);
                System.out.println(r);

                refreshStudents();

                if(r.getCode()==0)
                {
                    Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                    alert1.setContentText("删除成功");
                    alert1.showAndWait();
                }
            }
        }
    }

    private void updateStudent() {
        // 实现更新学生信息的逻辑
        Map form = studentTable.getSelectionModel().getSelectedItem();
        if(form==null)
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("未选择，无法更新");
            alert.showAndWait();
        }
        else
        {

            form.put("studentId",numField.getText());
            form.put("name",nameField.getText());
            form.put("gender",genderField.getText());
            form.put("major",majorField.getText());

            Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("警告");
            alert.setContentText("确定要更新吗？");
            Optional<ButtonType> result=alert.showAndWait();
            if(result.get()==ButtonType.OK)
            {
                DataResponse r=request("/updateStudent",form);
                System.out.println(form);
                System.out.println(r);

                refreshStudents();

                if(r.getCode()==0)
                {
                    Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                    alert1.setContentText("更新成功");
                    alert1.showAndWait();
                }
            }
        }
    }

    private void refreshStudents() {
        try {
            displayStudents();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
