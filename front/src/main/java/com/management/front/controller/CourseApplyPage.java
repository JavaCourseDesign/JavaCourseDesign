package com.management.front.controller;

import com.management.front.customComponents.SearchableTableView;
import com.management.front.request.DataResponse;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.management.front.util.HttpClientUtil.request;

public class CourseApplyPage extends SplitPane {
    private SearchableTableView courseTable;
    private VBox controlPanel = new VBox();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();
    private WeekTimeTable weekTimeTable = new WeekTimeTable();

    private Button applyButton = new Button("Apply");

    public CourseApplyPage() {
        this.setWidth(1000);
        this.setDividerPosition(0, 0.7);
        this.setOrientation(Orientation.VERTICAL);
        initializeWeekTimeTable();
        initializeTable();
        initializeControlPanel();
        displayCourses();
    }
    private void initializeWeekTimeTable() {

        this.getItems().add(weekTimeTable);
    }

    private void initializeTable() {
        TableColumn<Map, String> courseIdColumn = new TableColumn<>("课程号");
        TableColumn<Map, String> courseNameColumn = new TableColumn<>("课程名");
        TableColumn<Map, String> courseCreditColumn = new TableColumn<>("学分");
        TableColumn<Map, String> courseTeacherColumn = new TableColumn<>("教师");
        TableColumn<Map, String> courseAvailableColumn = new TableColumn<>("可选");

        courseIdColumn.setCellValueFactory(new MapValueFactory<>("courseId"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        courseCreditColumn.setCellValueFactory(new MapValueFactory<>("credit"));

        courseTeacherColumn.setCellValueFactory(data -> {
            List<Map<String, Object>> persons = (List<Map<String, Object>>) data.getValue().get("persons");
            String personNames = persons.stream()
                    .filter(person -> person.containsKey("teacherId"))
                    .map(person -> (String) person.get("name"))
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(personNames);
        });

        courseAvailableColumn.setCellValueFactory(data -> {
            boolean available = (boolean) data.getValue().get("available");
            return new SimpleStringProperty(available ? "是" : "否");
        });

        List<TableColumn<Map, ?>> columns = new ArrayList<>();
        columns.addAll(List.of(courseIdColumn, courseNameColumn, courseCreditColumn, courseTeacherColumn, courseAvailableColumn));
        courseTable = new SearchableTableView(observableList, List.of("courseId", "name"), columns);

        this.getItems().add(courseTable);
    }

    private void initializeControlPanel() {
        controlPanel.setMinWidth(200);
        controlPanel.setSpacing(10);

        applyButton.setOnAction(event -> applyCourse());

        controlPanel.getChildren().addAll(applyButton);

        this.getItems().add(controlPanel);
    }

    private void displayCourses() {
        List<Map> courses = (ArrayList) request("/getWantedCourses", null).getData();
        System.out.println("wantedCourses"+courses);
        weekTimeTable.clear();

        List<Map> allEvents = new ArrayList<>();
        for (Map course : courses) {
            List<Map> events = (List<Map>) request("/getLessonsByCourseId", Map.of("courseId", course.get("courseId"))).getData();
            //if(events==null) continue;
            allEvents.addAll(events);
        }
        weekTimeTable.setEvents(allEvents);

        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getAllCourses", null).getData()));
        courseTable.setData(observableList);
    }

    private void applyCourse() {
        Map selectedCourse = courseTable.getSelectedItem();
        if (selectedCourse == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("未选择，无法申请");
            alert.showAndWait();
            return;
        }

        // Create a map to hold the request data
        //Map<String, String> requestData = new HashMap<>();
        //requestData.put("courseId", (String) selectedCourse.get("courseId"));
        //requestData.put("personId", null);//personId问题待修改

        // Send the request to the server
        DataResponse response = request("/applyCourse", Map.of("courseId", (String) selectedCourse.get("courseId")));

        // Check the response and show an alert
        if (response.getCode() == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("申请成功");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("申请失败: " + response.getMsg());
            alert.showAndWait();
        }
        displayCourses();
    }
}