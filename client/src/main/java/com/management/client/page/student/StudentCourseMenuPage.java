package com.management.client.page.student;

import com.management.client.customComponents.SearchableTableView;
import com.management.client.page.teacher.CourseInfoTab;
import com.management.client.page.teacher.HomeworkManagementTab;
import com.management.client.page.teacher.ScoreManagementTab;
import com.management.client.page.teacher.StudentAbsenceManagementTab;
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

//本页面主要用于查看课程信息，如课程资料下载等，成绩考虑单开页面展示，单独在本页面显示意义不大
//考虑是否要实现教师上传作业内容文件，然后在本页面显示
//studentHomeworkPage仍有存在的必要，提供一个快捷的提交作业的途径，考虑集成在homePage中
public class StudentCourseMenuPage extends SplitPane {
    private ObservableList<Map> observableList = FXCollections.observableArrayList();
    private SearchableTableView courseTable;
    private TabPane courseTabPane = new TabPane();

    public StudentCourseMenuPage() {
        this.setDividerPosition(0, 0.13);
        initializeTable();
        initializeTabPane();
        displayCourses();
    }

    private void initializeTable() {
        FilteredTableColumn<Map, String> courseIdColumn = new FilteredTableColumn<>("课程号");
        FilteredTableColumn<Map, String> courseNameColumn = new FilteredTableColumn<>("课程名");

        courseIdColumn.setCellValueFactory(data-> {
            String courseId = (String) data.getValue().get("courseId");
            if(courseId==null)
            {
                return new SimpleStringProperty("");
            }
            return new SimpleStringProperty("sdu"+String.format("%06d",Integer.parseInt(courseId)));
        });
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("name"));

        List<FilteredTableColumn<Map,?>> columns = new ArrayList<>();
        columns.addAll(List.of(courseIdColumn, courseNameColumn));
        courseTable=new SearchableTableView(observableList, List.of("courseId","name"), columns);

        courseTable.setOnItemClick(item -> {
            courseTabPane.getTabs().clear();
            // 这里可以根据学生的需求添加不同的Tab
            // courseTabPane.getTabs().add(new StudentCourseInfoTab(item));
        });
        courseTable.setOnItemClick(item -> {
            courseTabPane.getTabs().clear();
            courseTabPane.getTabs().add(new StudentCourseInfoTab(item));
            courseTabPane.getTabs().add(new StudentHomeworkTab());
           /* courseTabPane.getTabs().add(new );
            courseTabPane.getTabs().add(new HomeworkManagementTab(item));
            courseTabPane.getTabs().add(new StudentAbsenceManagementTab(item));*/
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