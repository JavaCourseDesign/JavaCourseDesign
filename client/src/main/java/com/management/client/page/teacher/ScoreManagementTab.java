package com.management.client.page.teacher;

import com.management.client.customComponents.SearchableTableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.management.client.util.HttpClientUtil.request;

public class ScoreManagementTab extends Tab {//成绩录入界面
    SplitPane splitPane = new SplitPane();
    SearchableTableView scoreTable;
    ObservableList<Map> observableList= FXCollections.observableArrayList();
    VBox controlPanel = new VBox();
    Spinner homeworkWeightField = new Spinner(0,1,0.3,0.1);
    Spinner absenceWeightField = new Spinner(0,1,0.2,0.1);
    TextField finalMarkField = new TextField();
    Button fillButton = new Button("填充平时成绩");
    Button saveButton = new Button("保存本行成绩");
    Map course;
    public Map newMapFromFields() {
        Map m = new HashMap();
        m.put("homeworkWeight", homeworkWeightField.getValue());
        m.put("absenceWeight", absenceWeightField.getValue());
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
        TableColumn<Map, String> homeworkMarkColumn = new TableColumn<>("作业成绩");
        TableColumn<Map, String> absenceColumn = new TableColumn<>("出勤成绩");
        TableColumn<Map, String> finalMarkColumn = new TableColumn<>("期末成绩");
        TableColumn<Map, String> markColumn = new TableColumn<>("总成绩");

        // Set cell value factories
        studentIdColumn.setCellValueFactory(new MapValueFactory<>("student"));
        //courseIdColumn.setCellValueFactory(new MapValueFactory<>("course"));
        homeworkMarkColumn.setCellValueFactory(new MapValueFactory<>("homeworkMark"));
        absenceColumn.setCellValueFactory(new MapValueFactory<>("absenceMark"));
        finalMarkColumn.setCellValueFactory(new MapValueFactory<>("finalMark"));
        markColumn.setCellValueFactory(new MapValueFactory<>("mark"));

        // Add columns to table
        List<TableColumn<Map,?>> columns= List.of(studentIdColumn,homeworkMarkColumn,absenceColumn,finalMarkColumn,markColumn);
        scoreTable= new SearchableTableView(observableList,List.of("studentId"),columns);

        splitPane.getItems().add(scoreTable);
    }

    public void initializeControlPanel() {
        fillButton.setOnMouseClicked(e -> {
            request("/fillCourseScores", newMapFromFields());
            displayScores();
        });

        saveButton.setOnMouseClicked(e -> {
            Map score = scoreTable.getSelectedItem();
            score.put("finalMark", finalMarkField.getText());
            request("/uploadFinalScore", score);
            displayScores();
        });
        controlPanel.getChildren().addAll(homeworkWeightField,absenceWeightField,fillButton,finalMarkField,saveButton);
        splitPane.getItems().add(controlPanel);
    }

    public void displayScores() {
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList)request("/getCourseScores", course).getData()));//请求所有成绩
        scoreTable.setData(observableList);
    }
}
