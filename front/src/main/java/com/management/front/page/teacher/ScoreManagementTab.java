package com.management.front.page.teacher;

import com.management.front.customComponents.SearchableListView;
import com.management.front.customComponents.SearchableTableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.management.front.util.HttpClientUtil.request;

public class ScoreManagementTab extends Tab {//成绩录入界面
    SplitPane splitPane = new SplitPane();
    SearchableTableView scoreTable;
    ObservableList<Map> observableList= FXCollections.observableArrayList();
    VBox controlPanel = new VBox();
    TextField regularWeightField = new TextField();
    Button createButton = new Button("创建课程成绩");
    Map course;
    public Map newMapFromFields() {
        Map m = new HashMap();
        m.put("regularWeight", regularWeightField.getText());
        m.put("courseId", course.get("courseId"));
        return m;
    }
    public ScoreManagementTab(Map course) {
        this.course = course;
        this.setText("成绩录入");
        initializeTable();
        initializeControlPanel();
        displayScores();
        this.setContent(splitPane);
    }
    public void initializeTable() {
        // Create columns
        TableColumn<Map, String> studentIdColumn = new TableColumn<>("学号");
        //TableColumn<Map, String> courseIdColumn = new TableColumn<>("课程号");
        TableColumn<Map, String> regularMarkColumn = new TableColumn<>("平时成绩");
        TableColumn<Map, String> finalMarkColumn = new TableColumn<>("期末成绩");
        TableColumn<Map, String> markColumn = new TableColumn<>("总成绩");

        // Set cell value factories
        studentIdColumn.setCellValueFactory(new MapValueFactory<>("student"));
        //courseIdColumn.setCellValueFactory(new MapValueFactory<>("course"));
        regularMarkColumn.setCellValueFactory(new MapValueFactory<>("regularMark"));
        finalMarkColumn.setCellValueFactory(new MapValueFactory<>("finalMark"));
        markColumn.setCellValueFactory(new MapValueFactory<>("mark"));

        // Add columns to table
        List<TableColumn<Map,?>> columns= List.of(studentIdColumn,regularMarkColumn,finalMarkColumn,markColumn);
        scoreTable= new SearchableTableView(observableList,List.of("studentId"),columns);
        splitPane.getItems().add(scoreTable);
    }

    public void initializeControlPanel() {
        createButton.setOnMouseClicked(e -> {
            request("/addCourseScores", newMapFromFields());
            displayScores();
        });
        controlPanel.getChildren().addAll(regularWeightField,createButton);
        splitPane.getItems().add(controlPanel);
    }

    public void displayScores() {
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList)request("/getCourseScores", course).getData()));//请求所有成绩
        scoreTable.setData(observableList);
    }
}
