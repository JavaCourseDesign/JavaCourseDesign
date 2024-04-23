package com.management.front.page.teacher;

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
import java.util.stream.Collectors;

import static com.management.front.util.HttpClientUtil.request;

public class TeacherCourseMenuPage extends SplitPane {
    private ObservableList<Map> observableList = FXCollections.observableArrayList();
    private SearchableTableView courseTable;
    private TabPane courseTabPane = new TabPane();
    public TeacherCourseMenuPage() {
        this.setWidth(1000);
        initializeTable();
        initializeTabPane();
        displayCourses();
    }
    private void initializeTable() {
        TableColumn<Map, String> courseIdColumn = new TableColumn<>("课程号");
        TableColumn<Map, String> courseNameColumn = new TableColumn<>("课程名");
        /*TableColumn<Map, String> courseCreditColumn = new TableColumn<>("学分");
        TableColumn<Map, String> courseReferenceColumn = new TableColumn<>("参考资料");
        TableColumn<Map, String> courseCapacityColumn = new TableColumn<>("课容量");
        TableColumn<Map, String> preCourseColumn = new TableColumn<>("先修课程");
        TableColumn<Map, String> teacherColumn = new TableColumn<>("教师");
        TableColumn<Map, String> studentColumn = new TableColumn<>("学生数");*/

        courseIdColumn.setCellValueFactory(new MapValueFactory<>("courseId"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        /*courseCreditColumn.setCellValueFactory(new MapValueFactory<>("credit"));
        courseReferenceColumn.setCellValueFactory(new MapValueFactory<>("reference"));
        courseCapacityColumn.setCellValueFactory(new MapValueFactory<>("capacity"));

        preCourseColumn.setCellValueFactory(new MapValueFactory<>("preCourses"));

        teacherColumn.setCellValueFactory(data -> {
            List<Map<String, Object>> persons = (List<Map<String, Object>>) data.getValue().get("persons");
            String personNames = persons.stream()
                    .filter(person -> person.containsKey("teacherId"))
                    .map(person -> ""+ person.get("name"))
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(personNames);
        });

        //studentColumn.setCellValueFactory(new MapValueFactory<>("student"));
        studentColumn.setCellValueFactory(data -> {
            List<Map<String, Object>> persons = (List<Map<String, Object>>) data.getValue().get("persons");
            long studentCount = persons.stream()
                    // 过滤出有studentId属性的Map对象
                    .filter(person -> person.containsKey("studentId"))
                    // 统计符合条件的项的数量
                    .count();
            return new SimpleStringProperty(studentCount!=0?studentCount + "":"");
        });*/


        List<TableColumn<Map,?>> columns = new ArrayList<>();
        columns.addAll(List.of(courseIdColumn, courseNameColumn));
        courseTable=new SearchableTableView(observableList, List.of("courseId","name"), columns);

        courseTable.setOnItemClick(item -> {
            courseTabPane.getTabs().clear();
            courseTabPane.getTabs().add(new CourseInfoTab(item));
            courseTabPane.getTabs().add(new ScoreManagementTab(item));
            courseTabPane.getTabs().add(new HomeworkManagementTab(item));
            courseTabPane.getTabs().add(new StudentAbsenceManagementTab(item));
        });

        this.getItems().add(courseTable);
    }

    private void displayCourses(){
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getTeacherCourses", new HashMap<>()).getData()));
        courseTable.setData(observableList);
    }

    private void initializeTabPane() {
        this.getItems().add(courseTabPane);
    }
}
