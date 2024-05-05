package com.management.client.page.teacher;

import com.management.client.customComponents.SearchableTableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.VBox;
import org.controlsfx.control.tableview2.FilteredTableColumn;

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
        FilteredTableColumn<Map, String> studentIdColumn = new FilteredTableColumn<>("学号");
        //FilteredTableColumn<Map, String> courseIdColumn = new FilteredTableColumn<>("课程号");
        FilteredTableColumn<Map, String> homeworkMarkColumn = new FilteredTableColumn<>("作业成绩");
        FilteredTableColumn<Map, String> absenceColumn = new FilteredTableColumn<>("出勤成绩");
        FilteredTableColumn<Map, String> finalMarkColumn = new FilteredTableColumn<>("期末成绩");
        FilteredTableColumn<Map, String> markColumn = new FilteredTableColumn<>("单科总成绩");
        FilteredTableColumn<Map, String> gradePointColumn = new FilteredTableColumn<>("绩点");

        // Set cell value factories
        studentIdColumn.setCellValueFactory(new MapValueFactory<>("student"));
        //courseIdColumn.setCellValueFactory(new MapValueFactory<>("course"));
        homeworkMarkColumn.setCellValueFactory(new MapValueFactory<>("homeworkMark"));
        absenceColumn.setCellValueFactory(new MapValueFactory<>("absenceMark"));
        finalMarkColumn.setCellValueFactory(new MapValueFactory<>("finalMark"));
        markColumn.setCellValueFactory(new MapValueFactory<>("mark"));
        //绩点通过成绩计算得到
        gradePointColumn.setCellValueFactory(data -> {
            Map<String, Object> row = data.getValue();
            if(row.get("mark")==null) return new SimpleStringProperty("");
            double mark = Double.parseDouble(row.get("mark").toString());
            double gradePoint = mark<60?0:1+(mark-60)/10;
            return new SimpleStringProperty(gradePoint+"");
        });

        // Add columns to table
        List<FilteredTableColumn<Map,?>> columns= List.of(studentIdColumn,homeworkMarkColumn,absenceColumn,finalMarkColumn,markColumn,gradePointColumn);
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
