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

public class TeacherManagementPage extends SplitPane {
    private TableView<Map> teacherTable = new TableView<>();
    private VBox controlPanel = new VBox();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    private Button addButton = new Button("Add");
    private Button deleteButton = new Button("Delete");
    private Button updateButton = new Button("Update");

    private TextField numField = new TextField();
    private TextField nameField = new TextField();
    private TextField genderField = new TextField();
    private TextField majorField = new TextField();

    private Map newMapFromFields(Map m) {
        m.put("teacherId", numField.getText());
        m.put("name", nameField.getText());
        m.put("gender", genderField.getText());
        m.put("title", majorField.getText());
        return m;
    }

    public TeacherManagementPage() {
        this.setWidth(1000);
        initializeTable();
        initializeControlPanel();
        displayTeachers();
    }

    private void initializeTable() {

        TableColumn<Map, String> teacherId = new TableColumn<>("教师号");
        TableColumn<Map, String> teacherName = new TableColumn<>("姓名");
        TableColumn<Map, String> teacherGender = new TableColumn<>("性别");
        TableColumn<Map, String> teacherMajor = new TableColumn<>("职称");

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



        controlPanel.getChildren().addAll(numField, nameField, genderField, majorField, addButton, deleteButton, updateButton);

        addButton.setOnAction(event -> addTeacher());
        deleteButton.setOnAction(event -> deleteTeacher());
        updateButton.setOnAction(event -> updateTeacher());

        teacherTable.selectionModelProperty().get().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null)
            {
                numField.setText((String) newValue.get("teacherId"));
                nameField.setText((String) newValue.get("name"));
                genderField.setText((String) newValue.get("gender"));
                majorField.setText((String) newValue.get("title"));
            }
        });

        this.getItems().add(controlPanel);
    }

    private void displayTeachers(){
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getAllTeachers", null).getData()));
        teacherTable.setItems(observableList);
    }

    private void addTeacher() {
        Map m=newMapFromFields(new HashMap<>());

        System.out.println(m);

        DataResponse r=request("/addTeacher",m);

        displayTeachers();

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
        Map m = teacherTable.getSelectionModel().getSelectedItem();
        if(m==null)
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("未选择，无法删除");
            alert.showAndWait();
        }
        else
        {
            Alert alert=new Alert(Alert.AlertType.CONFIRMATION, "确定要删除吗？");
            alert.setTitle("警告");
            Optional<ButtonType> result=alert.showAndWait();
            if(result.get()==ButtonType.OK)
            {
                DataResponse r=request("/deleteTeacher",m);
                System.out.println(m);
                System.out.println(r);

                displayTeachers();

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
        Map selected = teacherTable.getSelectionModel().getSelectedItem();
        if(selected==null)
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("未选择，无法更新");
            alert.showAndWait();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "确定要更新吗？");
        alert.setTitle("警告");
        Optional<ButtonType> result=alert.showAndWait();
        if(result.get()==ButtonType.OK)
        {
            DataResponse r=request("/updateTeacher",newMapFromFields(selected));

            displayTeachers();

            if(r.getCode()==0)
            {
                Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                alert1.setContentText("更新成功");
                alert1.showAndWait();
            }
        }
    }
}