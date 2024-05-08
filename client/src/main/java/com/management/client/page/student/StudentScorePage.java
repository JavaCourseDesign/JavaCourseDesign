package com.management.client.page.student;

import com.management.client.customComponents.SearchableTableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.MapValueFactory;
import org.controlsfx.control.tableview2.FilteredTableColumn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.management.client.util.HttpClientUtil.request;

public class StudentScorePage extends SplitPane {
    private Label label = new Label();
    private SearchableTableView scoreTable;
    private ObservableList<Map> observableList = FXCollections.observableArrayList();
    public StudentScorePage() {
        initializeTable();
        displayScores();
        gpa();
    }

    private void displayScores() {
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getStudentScores", null).getData()));
        //System.out.println(observableList);;
        scoreTable.setData(observableList);
    }
    private void gpa()
    {
        double totalCredit=0;
        double totalGradePoint=0;
        for(Map row:observableList)
        {
            if(row.get("mark")==null) continue;
            double mark = Double.parseDouble(row.get("mark").toString());
            double gradePoint = mark<60?0:1+(mark-60)/10;
            Map course= (Map)row.get("course");
            totalCredit+=Double.parseDouble(course.get("credit").toString());
            totalGradePoint+=gradePoint*Double.parseDouble(course.get("credit").toString());
        }
        label.setText("平均学分绩点为:\n"+totalGradePoint/totalCredit);
        label.setStyle("-fx-font-size: 20 ; -fx-text-fill: red;");
    }

    private void initializeTable() {
        FilteredTableColumn<Map,String> courseIdColumn = new FilteredTableColumn<>("课程号");
        FilteredTableColumn<Map,String> courseNameColumn = new FilteredTableColumn<>("课程名");
        FilteredTableColumn<Map, String> absenceColumn = new FilteredTableColumn<>("出勤成绩");
        FilteredTableColumn<Map, String> homeworkMarkColumn = new FilteredTableColumn<>("作业成绩");
        FilteredTableColumn<Map,String> finalMarkColumn = new FilteredTableColumn<>("期末成绩");
        FilteredTableColumn<Map, String> markColumn = new FilteredTableColumn<>("单科总成绩");
        FilteredTableColumn<Map, String> gradePointColumn = new FilteredTableColumn<>("绩点");
        FilteredTableColumn<Map, String> gradeColumn = new FilteredTableColumn<>("绩点对应等级");
        FilteredTableColumn<Map, String> creditColumn = new FilteredTableColumn<>("学分");
        FilteredTableColumn<Map, String> hourColumn = new FilteredTableColumn<>("总学时");
        FilteredTableColumn<Map, String> courseTypeColumn = new FilteredTableColumn<>("课程类型");
        FilteredTableColumn<Map, String> coursePropertyColumn = new FilteredTableColumn<>("课程性质");
        courseIdColumn.setCellValueFactory(
                data -> {
                    Map course= (Map)data.getValue().get("course");
                    String courseId = (String)course.get("courseId");
                    if(courseId==null)
                    {
                        return new SimpleStringProperty("");
                    }
                    return new SimpleStringProperty("sdu"+String.format("%06d",Integer.parseInt(courseId)));
                }
        );
        courseNameColumn.setCellValueFactory(data -> {
            Map course= (Map)data.getValue().get("course");
            if(course.isEmpty()) return new SimpleStringProperty("");
            return new SimpleStringProperty((String)course.get("name"));
        });
        courseTypeColumn.setCellValueFactory(data -> {
            Map course= (Map)data.getValue().get("course");
            if(course.isEmpty()) return new SimpleStringProperty("");
            return new SimpleStringProperty((String)course.get("type"));
        });
        coursePropertyColumn.setCellValueFactory(data -> {
            Map course= (Map)data.getValue().get("course");
            if(course.isEmpty()) return new SimpleStringProperty("");
            return new SimpleStringProperty((String)course.get("property"));
        });
        creditColumn.setCellValueFactory(data -> {
            Map course= (Map)data.getValue().get("course");
            if(course.isEmpty()) return new SimpleStringProperty("");
            return new SimpleStringProperty(course.get("credit")+"");
        });
        hourColumn.setCellValueFactory(new MapValueFactory<>("hours"));
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
        gradeColumn.setCellValueFactory(data -> {
            Map<String, Object> row = data.getValue();
            if(row.get("mark")==null) return new SimpleStringProperty("");
            double mark = Double.parseDouble(row.get("mark").toString());
            if(mark>=95) return new SimpleStringProperty("A+");
            else if(mark>=90) return new SimpleStringProperty("A");
            else if(mark>=85) return new SimpleStringProperty("A-");
            else if(mark>=80) return new SimpleStringProperty("B+");
            else if(mark>=75) return new SimpleStringProperty("B");
            else if(mark>=70) return new SimpleStringProperty("B-");
            else if(mark>=65) return new SimpleStringProperty("C+");
            else if(mark>=60) return new SimpleStringProperty("C");
            else return new SimpleStringProperty("D");
        });

        List<FilteredTableColumn<Map,?>> columns= List.of(courseIdColumn,courseNameColumn,homeworkMarkColumn,absenceColumn,finalMarkColumn,markColumn,gradePointColumn,gradeColumn,creditColumn,hourColumn,courseTypeColumn,coursePropertyColumn);
        scoreTable= new SearchableTableView(observableList,List.of(),columns);
        this.getItems().add(label);
        this.getItems().add(scoreTable);
        this.setOrientation(Orientation.VERTICAL);
    }

}
