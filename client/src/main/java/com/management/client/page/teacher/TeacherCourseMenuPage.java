package com.management.client.page.teacher;

import com.management.client.customComponents.SearchableTableView;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import javafx.scene.control.cell.MapValueFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.management.client.util.HttpClientUtil.request;

public class TeacherCourseMenuPage extends SplitPane {
    private ObservableList<Map> observableList = FXCollections.observableArrayList();
    private SearchableTableView courseTable;
    private TabPane courseTabPane = new TabPane();
    public TeacherCourseMenuPage() {
        //this.setWidth(1000);
        this.setDividerPosition(0, 0.1);
        initializeTable();
        initializeTabPane();
        displayCourses();
    }
    private void initializeTable() {
        FilteredTableColumn<Map, String> courseIdColumn = new FilteredTableColumn<>("课程号");
        FilteredTableColumn<Map, String> courseNameColumn = new FilteredTableColumn<>("课程名");
        /*FilteredTableColumn<Map, String> courseCreditColumn = new FilteredTableColumn<>("学分");
        FilteredTableColumn<Map, String> courseReferenceColumn = new FilteredTableColumn<>("参考资料");
        FilteredTableColumn<Map, String> courseCapacityColumn = new FilteredTableColumn<>("课容量");
        FilteredTableColumn<Map, String> preCourseColumn = new FilteredTableColumn<>("先修课程");
        FilteredTableColumn<Map, String> teacherColumn = new FilteredTableColumn<>("教师");
        FilteredTableColumn<Map, String> studentColumn = new FilteredTableColumn<>("学生数");*/

        courseIdColumn.setCellValueFactory(data-> {
            String courseId = (String) data.getValue().get("courseId");
            if(courseId==null)
            {
                return new SimpleStringProperty("");
            }
            return new SimpleStringProperty("sdu"+String.format("%06d",Integer.parseInt(courseId)));
        });
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


        List<FilteredTableColumn<Map,?>> columns = new ArrayList<>();
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
        courseTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        this.getItems().add(courseTabPane);
    }
}
