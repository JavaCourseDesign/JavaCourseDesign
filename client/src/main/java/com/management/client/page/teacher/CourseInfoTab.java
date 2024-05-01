package com.management.client.page.teacher;

import com.management.client.customComponents.WeekTimeTable;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.management.client.util.HttpClientUtil.request;

public class CourseInfoTab extends Tab {
    Map course;
    public CourseInfoTab(Map course) {
        this.course = course;
        displayCourseInfo();
        this.setText("课程信息");
    }

    private void displayCourseInfo() {
        VBox courseInfoBox = new VBox();

        WeekTimeTable weekTimeTable = new WeekTimeTable();
        weekTimeTable.setEvents((List<Map<String,Object>>) request("/getLessonsByCourse", course).getData());

        Label courseIdLabel = new Label("课程号: " + course.get("courseId"));
        Label courseNameLabel = new Label("课程名: " + course.get("name"));
        Label referenceLabel = new Label("参考资料: " + course.get("reference"));
        Label capacityLabel = new Label("课容量: " + course.get("capacity"));
        Label creditLabel = new Label("学分: " + course.get("credit"));
        String courseType = switch (course.get("type")+"") {
            case "0" -> "必选";
            case "1" -> "限选";
            case "2" -> "任选";
            default -> "";
        };
        Label typeLabel = new Label("课程类型: " + courseType);
        Label preCoursesLabel = new Label("先修课程: " + course.get("preCourses"));
        Label teacherLabel = new Label("教师: " + ((List<Map>)course.get("persons")).stream()
                .filter(person -> person.containsKey("teacherId"))
                .map(person -> ""+ person.get("name"))
                .collect(Collectors.joining(", ")));
        Label studentLabel = new Label("学生: " + ((List<Map>)course.get("persons")).stream()
                .filter(person -> person.containsKey("studentId"))
                .map(person -> ""+ person.get("name"))
                .collect(Collectors.joining(", ")));

        courseInfoBox.getChildren().addAll(weekTimeTable, courseIdLabel, courseNameLabel, referenceLabel, capacityLabel, creditLabel, typeLabel, preCoursesLabel, teacherLabel, studentLabel);

        this.setContent(courseInfoBox);
    }
}
