package com.management.front.controller;

import com.management.front.request.DataResponse;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.management.front.util.HttpClientUtil.*;

public class CourseManagementPage extends SplitPane {
    private TableView<Map> courseTable = new TableView<>();
    private VBox controlPanel = new VBox();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    private Button addButton = new Button("Add");
    private Button deleteButton = new Button("Delete");
    private Button updateButton = new Button("Update");

    private TextField courseIdField = new TextField();
    private TextField nameField = new TextField();
    private TextField referenceField = new TextField();
    private TextField capacityField = new TextField();

    private Map newMapFromFields(Map m) {
        m.put("courseId", courseIdField.getText());
        m.put("name", nameField.getText());
        m.put("reference", referenceField.getText());
        m.put("capacity", capacityField.getText());
        return m;
    }

    public CourseManagementPage() {
        this.setWidth(1000);
        initializeTable();
        initializeControlPanel();
        displayCourses();
    }

    private void initializeTable() {

        TableColumn<Map, String> courseId = new TableColumn<>("课程号");
        TableColumn<Map, String> courseName = new TableColumn<>("课程名");
        TableColumn<Map, String> courseReference = new TableColumn<>("参考资料");
        TableColumn<Map, String> courseCapacity = new TableColumn<>("课容量");
        TableColumn<Map, String> teacherColumn = new TableColumn<>("教师");
        TableColumn<Map, String> studentColumn = new TableColumn<>("学生数");

        courseId.setCellValueFactory(new MapValueFactory<>("courseId"));
        courseName.setCellValueFactory(new MapValueFactory<>("name"));
        courseReference.setCellValueFactory(new MapValueFactory<>("reference"));
        courseCapacity.setCellValueFactory(new MapValueFactory<>("capacity"));

        teacherColumn.setCellValueFactory(new MapValueFactory<>("persons"));
        teacherColumn.setCellValueFactory(data -> {
            List<Map<String, Object>> persons = (List<Map<String, Object>>) data.getValue().get("persons");
            String personNames = persons.stream()
                    .filter(person -> person.containsKey("teacherId"))
                    .map(person -> (String) person.get("name"))
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(personNames);
        });

        studentColumn.setCellValueFactory(new MapValueFactory<>("persons"));
        studentColumn.setCellValueFactory(data -> {
            List<Map<String, Object>> persons = (List<Map<String, Object>>) data.getValue().get("persons");
            long studentCount = persons.stream()
                    // 过滤出有studentId属性的Map对象
                    .filter(person -> person.containsKey("studentId"))
                    // 统计符合条件的项的数量
                    .count();
            return new SimpleStringProperty(studentCount + "");
        });

        courseTable.getColumns().addAll(courseId, courseName, courseReference, courseCapacity, teacherColumn ,studentColumn);
        this.getItems().add(courseTable);
    }

    private void initializeControlPanel() {
        controlPanel.setMinWidth(200);
        controlPanel.setSpacing(10);

        controlPanel.getChildren().addAll(courseIdField, nameField, referenceField, capacityField, addButton, deleteButton, updateButton);

        addButton.setOnAction(event -> addCourse());
        deleteButton.setOnAction(event -> deleteCourse());
        updateButton.setOnAction(event -> updateCourse());

        courseTable.selectionModelProperty().get().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null)
            {
                courseIdField.setText((String) newValue.get("courseId"));
                nameField.setText((String) newValue.get("name"));
                referenceField.setText((String) newValue.get("reference"));
                capacityField.setText((String) newValue.get("capacity"));
            }
        });

        this.getItems().add(controlPanel);
    }

    private void displayCourses(){
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getAllCourses", null).getData()));
        courseTable.setItems(observableList);
    }

    private void addCourse() {
        Map m=newMapFromFields(new HashMap<>());

        System.out.println(m);

        DataResponse r=request("/addCourse",m);

        displayCourses();

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

    private void deleteCourse() {
        Map m = courseTable.getSelectionModel().getSelectedItem();
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
                DataResponse r=request("/deleteCourse",m);
                System.out.println(m);
                System.out.println(r);

                displayCourses();

                if(r.getCode()==0)
                {
                    Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                    alert1.setContentText("删除成功");
                    alert1.showAndWait();
                }
            }
        }
    }

    private void updateCourse() {
        Map selected = courseTable.getSelectionModel().getSelectedItem();
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
            DataResponse r=request("/updateCourse",newMapFromFields(selected));

            displayCourses();

            if(r.getCode()==0)
            {
                Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                alert1.setContentText("更新成功");
                alert1.showAndWait();
            }
        }
    }
}