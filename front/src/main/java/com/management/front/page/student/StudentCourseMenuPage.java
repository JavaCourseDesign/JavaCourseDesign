package com.management.front.page.student;

import com.management.front.customComponents.SearchableTableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.MapValueFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.management.front.util.HttpClientUtil.request;

public class StudentCourseMenuPage extends SplitPane {
    private ObservableList<Map> observableList = FXCollections.observableArrayList();
    private SearchableTableView courseTable;
    private TabPane courseTabPane = new TabPane();

    public StudentCourseMenuPage() {
        this.setDividerPosition(0, 0.1);
        initializeTable();
        initializeTabPane();
        displayCourses();
    }

    private void initializeTable() {
        TableColumn<Map, String> courseIdColumn = new TableColumn<>("课程号");
        TableColumn<Map, String> courseNameColumn = new TableColumn<>("课程名");

        courseIdColumn.setCellValueFactory(new MapValueFactory<>("courseId"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("name"));

        List<TableColumn<Map,?>> columns = new ArrayList<>();
        columns.addAll(List.of(courseIdColumn, courseNameColumn));
        courseTable=new SearchableTableView(observableList, List.of("courseId","name"), columns);

        courseTable.setOnItemClick(item -> {
            courseTabPane.getTabs().clear();
            // 这里可以根据学生的需求添加不同的Tab
            // courseTabPane.getTabs().add(new StudentCourseInfoTab(item));
        });

        this.getItems().add(courseTable);
    }

    private void displayCourses(){
        observableList.clear();
        // 这里的请求路径可能需要根据实际情况进行修改
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getStudentCourses", new HashMap<>()).getData()));
        courseTable.setData(observableList);
    }

    private void initializeTabPane() {
        courseTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        this.getItems().add(courseTabPane);
    }
}