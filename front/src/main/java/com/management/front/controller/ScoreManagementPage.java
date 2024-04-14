package com.management.front.controller;

import com.management.front.customComponents.SearchableTableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.MapValueFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.management.front.util.HttpClientUtil.request;

public class ScoreManagementPage extends SplitPane {
    SearchableTableView scoreTable;
    ObservableList<Map> observableList= FXCollections.observableArrayList();
    TextField studentIdField = new TextField();
    TextField courseIdField = new TextField();
    TextField regularMarkField = new TextField();
    TextField finalMarkField = new TextField();
    TextField markField = new TextField();
    Button addButton = new Button("Add");
    Button deleteButton = new Button("Delete");
    Button updateButton = new Button("Update");
    public Map newMapFromFields() {
        Map m = new HashMap();
        m.put("studentId", studentIdField.getText());
        m.put("courseId", courseIdField.getText());
        m.put("regularMark", regularMarkField.getText());
        m.put("finalMark", finalMarkField.getText());//比例在course里，需要好好设计教师界面
        return null;
    }
    public ScoreManagementPage() {
        this.setWidth(1000);
        initializeTable();
        displayScores();
    }
    public void initializeTable() {
        // Create columns
        TableColumn<Map, String> studentIdColumn = new TableColumn<>("学号");
        TableColumn<Map, String> courseIdColumn = new TableColumn<>("课程号");
        TableColumn<Map, String> regularMarkColumn = new TableColumn<>("平时成绩");
        TableColumn<Map, String> finalMarkColumn = new TableColumn<>("期末成绩");
        TableColumn<Map, String> markColumn = new TableColumn<>("总成绩");

        // Set cell value factories
        studentIdColumn.setCellValueFactory(new MapValueFactory<>("studentId"));
        courseIdColumn.setCellValueFactory(new MapValueFactory<>("courseId"));
        regularMarkColumn.setCellValueFactory(new MapValueFactory<>("regularMark"));
        finalMarkColumn.setCellValueFactory(new MapValueFactory<>("finalMark"));
        markColumn.setCellValueFactory(new MapValueFactory<>("mark"));

        // Add columns to table
        List<TableColumn<Map,?>> columns= List.of(studentIdColumn,courseIdColumn,regularMarkColumn,finalMarkColumn,markColumn);
        scoreTable= new SearchableTableView(observableList,List.of("studentId","courseId"),columns);
        this.getItems().add(scoreTable);
    }

    public void initializeControlPanel() {

    }

    public void displayScores() {
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList)request("/getAllScore", null).getData()));//请求所有成绩
        scoreTable.setData(observableList);
    }
}
