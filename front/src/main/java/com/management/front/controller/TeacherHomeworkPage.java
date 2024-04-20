package com.management.front.controller;

import com.management.front.customComponents.SearchableListView;
import com.management.front.customComponents.SearchableTableView;
import com.management.front.request.DataResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.management.front.util.HttpClientUtil.request;

public class TeacherHomeworkPage extends SplitPane {
    private SearchableTableView homeworkTable;
    private VBox controlPanel = new VBox();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();
    private GridPane gridPane;
    private SearchableListView courseListView=new SearchableListView(FXCollections.observableArrayList((ArrayList) request("/getTeacherCourses",null).getData()), List.of("name"));
    private Label homeworkContentLabel = new Label("作业内容");
    private Label deadlineLabel = new Label("截止时间");
    private TextField homeworkContentField = new TextField();
    private TextField deadlineField = new TextField();
    private Button markButton = new Button("批改学生作业");
    private Button addButton = new Button("Add");
    private Button deleteButton = new Button("Delete");


    public TeacherHomeworkPage() {
        this.setWidth(1000);
        initializeTable();
        initializeControlPanel();
        displayHomeworks();
    }
    private Map newMapFromFields(Map m) {
        m.put("homeworkContent", homeworkContentField.getText());
        m.put("deadline", deadlineField.getText());
        m.put("courses", courseListView.getSelectedItems());
        return m;
    }

    private void displayHomeworks() {
        observableList.clear();
        DataResponse r=request("/getTeacherHomework",null);
        ArrayList<Map> list=(ArrayList<Map>)r.getData();
        System.out.println(list);
        for(Map m:list)
        {
            if(m.get("student")!=null)
            {
                Map student=(Map) m.get("student");
                m.put("studentName",student.get("name"));
                m.put("studentId",student.get("studentId"));
            }
            if(m.get("course")!=null) {
                Map course = (Map) m.get("course");
                m.put("courseName", course.get("name"));
            }
        }
        observableList.addAll(FXCollections.observableArrayList(list));
        homeworkTable.setData(observableList);
    }

    private void initializeControlPanel() {
        controlPanel.setMinWidth(100);
        controlPanel.setSpacing(10);
        controlPanel.getChildren().add(courseListView);
        gridPane=new GridPane();
        gridPane.addColumn(0,homeworkContentLabel,deadlineLabel);
        gridPane.addColumn(1,homeworkContentField,deadlineField);
        controlPanel.getChildren().add(gridPane);
        controlPanel.getChildren().add(addButton);
        controlPanel.getChildren().add(deleteButton);
        controlPanel.getChildren().add(markButton);
        addButton.setOnMouseClicked(event -> {
            addHomework();
        });
        deleteButton.setOnMouseClicked(event -> {
            deleteHomework();
        });
        markButton.setOnMouseClicked(event -> {
            markHomework();
        });
        this.getItems().add(controlPanel);
    }

    private void markHomework() {


    }


    private void deleteHomework() {
    }

    private void addHomework() {
        Map m=newMapFromFields(new HashMap());
        List<Map> list=(List<Map>) m.get("courses");
        if(list.isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("请填写完整信息");
            alert.showAndWait();
            return;
        }
        else if(list.size()>1)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("只能选择一个课程");
            alert.showAndWait();
            courseListView.setSelectedItems(List.of());
            return;
        }
        Map course=list.get(0);
        m.put("courseId",course.get("courseId"));
        m.put("course",course);
        m.remove("courses");
        if(m.get("homeworkContent")==null || m.get("deadline")==null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("请填写完整信息");
            alert.showAndWait();
            return;
        }
        DataResponse r=request("/addHomework",m);
        if(r.getCode()!=0)
        {
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setContentText(r.getMsg());
            return;
        }
        else
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("添加成功");
        }
        displayHomeworks();
        courseListView.setSelectedItems(List.of());
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
        this.getItems().add(homeworkTable);
    }

}
