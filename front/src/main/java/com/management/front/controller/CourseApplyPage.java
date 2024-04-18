package com.management.front.controller;

import com.management.front.customComponents.SearchableTableView;
import com.management.front.request.DataResponse;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.management.front.util.HttpClientUtil.request;

public class CourseApplyPage extends SplitPane {
    private SearchableTableView courseTable;
    private ObservableList<Map> observableList = FXCollections.observableArrayList();
    private WeekTimeTable weekTimeTable = new WeekTimeTable();
    private Label creditCount = new Label();

    public CourseApplyPage() {
        this.setWidth(1000);
        this.setDividerPosition(0, 0.7);
        this.setOrientation(Orientation.VERTICAL);
        this.getStylesheets().add("dark-theme.css");
        initializeWeekTimeTable();
        initializeTable();
        displayCourses();
    }
    private void initializeWeekTimeTable() {
        //HBox hbox = new HBox(weekTimeTable, creditCount);
        Label time=new Label(LocalDate.now().toString());
        creditCount.setFont(javafx.scene.text.Font.font(30));
        VBox infoBox = new VBox(time,creditCount);
        infoBox.setAlignment(Pos.CENTER);
        SplitPane splitPane = new SplitPane(weekTimeTable, infoBox);
        this.getItems().add(splitPane);
    }

    private void initializeTable() {
        TableColumn<Map, String> courseIdColumn = new TableColumn<>("课程号");
        TableColumn<Map, String> courseNameColumn = new TableColumn<>("课程名");
        TableColumn<Map, String> courseCreditColumn = new TableColumn<>("学分");
        TableColumn<Map, String> courseTeacherColumn = new TableColumn<>("教师");
        TableColumn<Map, String> courseAvailableColumn = new TableColumn<>("可选");
        TableColumn<Map, String> courseChosenColumn = new TableColumn<>("已选");

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

        courseChosenColumn.setCellValueFactory(data -> {
            //boolean chosen = (boolean) data.getValue().get("chosen");
            List<Map> willingStudents = (List<Map>) data.getValue().get("willingStudents");
            boolean chosen = willingStudents.stream().anyMatch(student -> student.get("studentId").equals(LoginPage.username));
            return new SimpleStringProperty(chosen ? "是" : "否");
        });

        TableColumn<Map, Void> courseApplyColumn = new TableColumn<>("选课");
        courseApplyColumn.setCellFactory(param -> {
            final Button applyButton = new Button("选课");
            TableCell<Map, Void> cell = new TableCell<>() {
                @Override
                public void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        applyButton.setOnAction(event -> applyCourse(getTableView().getItems().get(getIndex())));
                        //if(!(boolean) getTableView().getItems().get(getIndex()).get("available")) applyButton.setDisable(true);
                        setGraphic(applyButton);
                    }
                }
            };
            return cell;
        });

        List<TableColumn<Map, ?>> columns = new ArrayList<>();
        columns.addAll(List.of(courseIdColumn, courseNameColumn, courseCreditColumn, courseTeacherColumn, courseAvailableColumn, courseChosenColumn,courseApplyColumn));
        courseTable = new SearchableTableView(observableList, List.of("courseId", "name"), columns);

        this.getItems().add(courseTable);
    }

    private void displayCourses() {
        List<Map> courses = (ArrayList) request("/getWantedCourses", null).getData();
        //System.out.println("wantedCourses"+courses);

        List<Map> allEvents = new ArrayList<>();
        double credit = 0;
        for (Map course : courses) {
            credit += course.get("credit") == null ? 0 : (double) course.get("credit");
            List<Map> events = (List<Map>) request("/getLessonsByCourseId", Map.of("courseId", course.get("courseId"))).getData();
            //if(events==null) continue;
            allEvents.addAll(events);
        }
        creditCount.setText("已选学分：" + credit);
        weekTimeTable.setEvents(allEvents);

        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getAllCourses", null).getData()));
        courseTable.setData(observableList);
    }

    private void applyCourse(Map selectedCourse) {
        //Map selectedCourse = courseTable.getSelectedItem();
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