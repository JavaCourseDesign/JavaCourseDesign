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
    //private Button refreshButton = new Button("Refresh");

    private TextField studentIdField = new TextField();
    private TextField nameField = new TextField();
    private TextField genderField = new TextField();
    private TextField majorField = new TextField();

    private Map newMapFromFields(Map m) {
        m.put("studentId", studentIdField.getText());
        m.put("name", nameField.getText());
        m.put("gender", genderField.getText());
        m.put("major", majorField.getText());
        return m;
    }

    public StudentManagementPage() {
        this.setWidth(1000);
        initializeTable();
        initializeControlPanel();
        displayStudents();
    }

    private void initializeTable() {

        //建立列
        TableColumn<Map, String> studentIdColumn = new TableColumn<>("学号");
        TableColumn<Map, String> studentNameColumn = new TableColumn<>("姓名");
        TableColumn<Map, String> studentGenderColumn = new TableColumn<>("性别");
        TableColumn<Map, String> studentMajorColumn = new TableColumn<>("专业");

        //把map填入单元格
        studentIdColumn.setCellValueFactory(new MapValueFactory<>("studentId"));//与后端属性一致
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        studentGenderColumn.setCellValueFactory(new MapValueFactory<>("gender"));
        studentMajorColumn.setCellValueFactory(new MapValueFactory<>("major"));

        studentTable.getColumns().addAll(studentIdColumn, studentNameColumn, studentGenderColumn, studentMajorColumn);
        this.getItems().add(studentTable);
    }

    private void initializeControlPanel() {
        controlPanel.setMinWidth(200);
        controlPanel.setSpacing(10);

        controlPanel.getChildren().addAll(studentIdField, nameField, genderField, majorField, addButton, deleteButton, updateButton);

        addButton.setOnAction(event -> addStudent());
        deleteButton.setOnAction(event -> deleteStudent());
        updateButton.setOnAction(event -> updateStudent());
        //refreshButton.setOnAction(event -> displayStudents());

        studentTable.selectionModelProperty().get().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null)
            {
                studentIdField.setText((String) newValue.get("studentId"));
                nameField.setText((String) newValue.get("name"));
                genderField.setText((String) newValue.get("gender"));
                majorField.setText((String) newValue.get("major"));
            }
        });

        this.getItems().add(controlPanel);
    }

    private void displayStudents(){
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getAllStudents", null).getData()));
        studentTable.setItems(observableList);
        System.out.println(observableList);
    }

    private void addStudent() {
        Map m=newMapFromFields(new HashMap<>());

        System.out.println(m);

        DataResponse r=request("/addStudent",m);

        displayStudents();

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
        Map m = studentTable.getSelectionModel().getSelectedItem();
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
                DataResponse r=request("/deleteStudent",m);
                System.out.println(m);
                System.out.println(r);

                displayStudents();

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
        Map selected = studentTable.getSelectionModel().getSelectedItem();
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
            DataResponse r=request("/updateStudent",newMapFromFields(selected));

            displayStudents();

            if(r.getCode()==0)
            {
                Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                alert1.setContentText("更新成功");
                alert1.showAndWait();
            }
        }
    }
}

//[
//        {personId=1.0, num=null, name=tst, type=null, dept=null, card=null, gender=男, birthday=null, email=null, phone=null, address=null, introduce=null, absences=[], honors=[], dormitory=null, events=[], courses=[{name=软件工程, courseId=1, reference=null, capacity=null, credit=null, lessons=[], persons=[{personId=1.0, num=null, name=tst, type=null, dept=null, card=null, gender=男, birthday=null, email=null, phone=null, address=null, introduce=null, absences=[], honors=[], dormitory=null, events=[], courses=[软件工程], studentId=2019210000, major=软件工程, className=软工1班}, {personId=2.0, num=null, name=wzk, type=null, dept=null, card=null, gender=男, birthday=null, email=null, phone=null, address=null, introduce=null, absences=[], honors=[], dormitory=null, events=[], courses=[软件工程], studentId=2019210001, major=软件工程, className=软工2班}, {personId=3.0, num=null, name=why, type=null, dept=null, card=null, gender=null, birthday=null, email=null, phone=null, address=null, introduce=null, absences=[], honors=[], dormitory=null, events=[], courses=[软件工程], teacherId=100000, degree=null, title=null}]}], studentId=2019210000, major=软件工程, className=软工1班},
//        {personId=2.0, num=null, name=wzk, type=null, dept=null, card=null, gender=男, birthday=null, email=null, phone=null, address=null, introduce=null, absences=[], honors=[], dormitory=null, events=[], courses=[软件工程], studentId=2019210001, major=软件工程, className=软工2班}
//
//        ]

