package com.management.front.controller;

import com.management.front.customComponents.SearchableListView;
import com.management.front.customComponents.SearchableTableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TeacherHomeworkPage extends SplitPane {
    private SearchableTableView homeworkTable;
    private VBox controlPanel = new VBox();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();
    private GridPane gridPane;
    private SearchableListView courseListView;
    private Label homeworkContentLabel = new Label("作业内容");
    private Label deadlineLabel = new Label("截止时间");
    private TextField homeworkContentField = new TextField();
    private TextField deadlineField = new TextField();


    public TeacherHomeworkPage() {
        this.setWidth(1000);
        initializeTable();
        initializeControlPanel();
        displayHomeworks();
    }

    private void displayHomeworks() {

    }

    private void initializeControlPanel() {
        controlPanel.setMinWidth(200);
        controlPanel.setSpacing(10);
        gridPane=new GridPane();
        gridPane.addColumn(0,homeworkContentLabel,deadlineLabel);
        gridPane.addColumn(1,homeworkContentField,deadlineField);
        controlPanel.getChildren().add(gridPane);
        //controlPanel.getChildren().add();
    }

    private void initializeTable() {
        TableColumn<Map, String> studentNameColumn = new TableColumn<>("学生姓名");
        TableColumn<Map, String> studentIdColumn = new TableColumn<>("学生学号");
        TableColumn<Map,String> courseNameColumn= new TableColumn<>("课程名称");
        TableColumn<Map,String> homeworkContentColumn= new TableColumn<>("作业内容");
        TableColumn<Map,String> deadlineColumn= new TableColumn<>("截止时间");
        TableColumn<Map,String> submitTimeColumn= new TableColumn<>("提交时间");
        TableColumn<Map,String> gradeColumn= new TableColumn<>("成绩");

        studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        studentIdColumn.setCellValueFactory(new MapValueFactory<>("studentId"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        homeworkContentColumn.setCellValueFactory(new MapValueFactory<>("homeworkContent"));
        deadlineColumn.setCellValueFactory(new MapValueFactory<>("deadline"));
        submitTimeColumn.setCellValueFactory(new MapValueFactory<>("submitTime"));
        gradeColumn.setCellValueFactory(new MapValueFactory<>("grade"));
        List<TableColumn<Map, ?>> columns = new ArrayList<>();
        columns.add(studentNameColumn);
        columns.add(studentIdColumn);
        columns.add(courseNameColumn);
        columns.add(homeworkContentColumn);
        columns.add(deadlineColumn);
        columns.add(submitTimeColumn);
        columns.add(gradeColumn);
        homeworkTable=new SearchableTableView(observableList,List.of("studentId","courseName","grade"),columns);
        this.getChildren().add(homeworkTable);
    }
}
