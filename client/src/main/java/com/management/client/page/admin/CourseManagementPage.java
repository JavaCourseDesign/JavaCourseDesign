package com.management.client.page.admin;


import com.jfoenix.controls.JFXComboBox;
import com.management.client.customComponents.SearchableListView;
import com.management.client.customComponents.SearchableTableView;
import com.management.client.customComponents.WeekTimeTable;
import com.management.client.request.DataResponse;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.tableview2.FilteredTableColumn;


import java.util.*;
import java.util.stream.Collectors;

import static com.management.client.util.HttpClientUtil.*;

public class CourseManagementPage extends SplitPane {
    private SearchableTableView courseTable;
    private VBox controlPanel = new VBox();
    private GridPane gridPane = new GridPane();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    private Button addButton = new Button("添加");
    private Button deleteButton = new Button("删除");
    private Button updateButton = new Button("更新");
    private Button openButton = new Button("开放/关闭选课");
    private Button drawLotsButton = new Button("抽签");
    private SearchableListView teacherListView=new SearchableListView(FXCollections.observableArrayList((ArrayList) request("/getAllTeachers", null).getData()), List.of("teacherId", "name"));
    //包含全局所有教师信息的ListView，用于选择教师
    private SearchableListView studentListView=new SearchableListView(FXCollections.observableArrayList((ArrayList) request("/getAllStudents", null).getData()), List.of("studentId", "name"));
    //包含全局所有学生信息的ListView，用于选择学生
    private TextField preCourseField = new TextField();
    private SearchableListView clazzListView = new SearchableListView(FXCollections.observableArrayList((ArrayList) request("/getAllClazz", null).getData()), List.of("name"));
    private TextField courseIdField = new TextField();
    private TextField nameField = new TextField();
    private TextField creditField = new TextField();
    private TextField capacityField = new TextField();
    private JFXComboBox<String> typeField = new JFXComboBox<>();
    //private SelectionGrid selectionGrid = new SelectionGrid();
    private WeekTimeTable weekTimeTable = new WeekTimeTable();

    private Map newMapFromFields(Map m) {
        m.put("courseId", courseIdField.getText());
        m.put("name", nameField.getText());
        m.put("credit", creditField.getText());
        m.put("capacity", capacityField.getText());
        m.put("preCourses", preCourseField.getText());

        List<Map> personsToBeAdded = new ArrayList<>();
        personsToBeAdded.addAll(teacherListView.getSelectedItems());
        personsToBeAdded.addAll(studentListView.getSelectedItems());

        List<Map> studentsToBeAdded = studentListView.getSelectedItems();
        //把行政班中的学生依次添加到students中，避免重复
        List<Map> clazzes = clazzListView.getSelectedItems();
        for (Map<String, Object> clazz : clazzes) {
            List<Map<String, Object>> students = (List<Map<String, Object>>) clazz.get("students");
            for (Map<String, Object> student : students) {
                if (!studentsToBeAdded.contains(student)) {
                    studentsToBeAdded.add(student);
                }
            }
        }
        personsToBeAdded.addAll(studentsToBeAdded);

        m.put("persons", personsToBeAdded);
        m.put("type", typeField.getValue());

        //mark
        //selectionGrid.course=m;//把一部分course的信息给予lesson，注意顺序！
        //System.out.println("selectionGrid.course:"+selectionGrid.course);
        m.put("lessons", weekTimeTable.getEvents());
        //System.out.println("\nlessons:"+weekTimeTable.getEvents());

        return m;
    }

    public CourseManagementPage() {
        this.setWidth(1000);
        this.setDividerPosition(0, 0.65);
        initializeTable();
        initializeControlPanel();
        displayCourses();
    }

    private void initializeTable() {
        FilteredTableColumn<Map, String> courseIdColumn = new FilteredTableColumn<>("课程号");
        FilteredTableColumn<Map, String> courseNameColumn = new FilteredTableColumn<>("课程名");
        FilteredTableColumn<Map, String> courseCreditColumn = new FilteredTableColumn<>("学分");
        FilteredTableColumn<Map, String> courseTypeColumn = new FilteredTableColumn<>("类型");
        FilteredTableColumn<Map, String> courseReferenceColumn = new FilteredTableColumn<>("参考资料");
        FilteredTableColumn<Map, String> courseCapacityColumn = new FilteredTableColumn<>("课容量");
        FilteredTableColumn<Map, String> preCourseColumn = new FilteredTableColumn<>("先修课程");
        FilteredTableColumn<Map, String> teacherColumn = new FilteredTableColumn<>("教师");
        FilteredTableColumn<Map, String> studentColumn = new FilteredTableColumn<>("学生数");
        FilteredTableColumn<Map, String> availableColumn = new FilteredTableColumn<>("是否开放选课");
        courseIdColumn.setCellValueFactory(data-> {
            String courseId = (String) data.getValue().get("courseId");
            if(courseId==null)
            {
                return new SimpleStringProperty("");
            }
            return new SimpleStringProperty("sdu"+String.format("%06d",Integer.parseInt(courseId)));
        });
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        courseCreditColumn.setCellValueFactory(new MapValueFactory<>("credit"));

        courseReferenceColumn.setCellValueFactory(data->
        {
            if(data.getValue().get("reference")==null)
            {
                return new SimpleStringProperty("");
            }
            String reference=data.getValue().get("reference")+"";
            reference=reference.replace(".pdf","");
            return new SimpleStringProperty(reference);
        });
        //coursePropertyColumn.setCellValueFactory(new MapValueFactory<>("property"));
        courseCapacityColumn.setCellValueFactory(new MapValueFactory<>("capacity"));
        preCourseColumn.setCellValueFactory(new MapValueFactory<>("preCourses"));
        /*preCourseColumn.setCellValueFactory(data -> {
            System.out.println("data.getValue:"+data.getValue());
            List<Map<String, Object>> preCourses = (List<Map<String, Object>>) data.getValue().get("preCourses");
            String preCourseNames = preCourses.stream()
                    .map(preCourse -> " +preCourse.get("name"))
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(preCourseNames);
        });*/

        //teacherColumn.setCellValueFactory(new MapValueFactory<>("teacher"));
        courseTypeColumn.setCellValueFactory(new MapValueFactory<>("type"));

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
        });

        availableColumn.setCellValueFactory(data -> {
            if(data.getValue().get("available")==null)
            {
                return new SimpleStringProperty("");
            }
            boolean available = (boolean) data.getValue().get("available");
            return new SimpleStringProperty(available ? "是" : "否");
        });


        List<FilteredTableColumn<Map,?>> columns = new ArrayList<>();
        columns.addAll(List.of(courseIdColumn, courseNameColumn, courseCreditColumn,courseTypeColumn ,courseReferenceColumn, courseCapacityColumn,preCourseColumn ,teacherColumn, studentColumn, availableColumn));
        courseTable=new SearchableTableView(observableList, List.of("courseId","name","persons"), columns);
        this.getItems().add(courseTable);
    }

    private void initializeControlPanel() {
        controlPanel.setMinWidth(200);
        controlPanel.setSpacing(10);

        typeField.getItems().addAll("专业基础课", "学科基础课", "通识核心课", "通识选修课", "创新实践计划", "专业选修课", "通识必修课" );

        courseTable.setOnItemClick(course -> {
            if(course!=null)
            {
                courseIdField. setText((String) course.get("courseId"));
                nameField.     setText((String) course.get("name"));
                creditField.   setText(course.get("credit")==null?"": "" +course.get("credit"));
                capacityField. setText(course.get("capacity")==null?"": "" +course.get("capacity"));
                preCourseField.setText((String) course.get("preCourses"));

                switch (""+course.get("type")) {
                    case "0" -> typeField.getSelectionModel().select(0);
                    case "1" -> typeField.getSelectionModel().select(1);
                    case "2" -> typeField.getSelectionModel().select(2);
                    default -> typeField.getSelectionModel().select(0);
                }
                //preCourseField.setSelectedItems((List<Map>) course.get("preCourses"));

                List<Map> persons = (List<Map>) course.get("persons");
                List<Map> teachers = persons.stream()
                        .filter(person -> person.containsKey("teacherId"))
                        .collect(Collectors.toList());
                teacherListView.setSelectedItems(teachers);

                List<Map> students = persons.stream()
                        .filter(person -> person.containsKey("studentId"))
                        .collect(Collectors.toList());
                studentListView.setSelectedItems(students);//暂时没添加到面板

                //System.out.println("preCourses"+course.get("preCourses"));
                //preCourseListView.setSelectedItems((List<Map>) course.get("preCourses"));
                //preCourseListView.setSelectedItems((List<Map>) request("/getPreCoursesByCourseId", Map.of("courseId", course.get("courseId"))).getData());

                //administrative没法自动选中，只能添加,故设为空
                clazzListView.setSelectedItems(new ArrayList<>());

                //selectionGrid.setSelectedLessons((List<Map>) course.get("lessons"));

                //mark
                //selectionGrid.setSelectedLessons((List<Map>) request("/getLessonsByCourse", Map.of("courseId", ""+course.get("courseId"))).getData());
                weekTimeTable.setEvents((List<Map<String,Object>>) request("/getLessonsByCourse", Map.of("courseId", ""+course.get("courseId"))).getData());
                weekTimeTable.setCourse(course);
                /*if(!students.isEmpty()) {
                    //weekTimeTable.setEntryFactory(param -> null);
                    weekTimeTable.calendar.setReadOnly(true);
                }//如果有学生选了这门课，就不能修改课程时间*/
            }
        });

        addButton.setOnAction(event -> addCourse());
        deleteButton.setOnAction(event -> deleteCourse());
        updateButton.setOnAction(event -> updateCourse());
        HBox buttons = new HBox(addButton, deleteButton, updateButton);

        openButton.setOnAction(event -> openCourses());
        drawLotsButton.setOnAction(event -> drawLots());
        //courseIdField, nameField, creditField,typeField , capacityField, preCourseField, teacherListView, clazzListView
        gridPane.addColumn(0,
                new Label("课程号"),
                new Label("课程名"),
                new Label("学分"),
                new Label("课程类型"),
                new Label("课容量"),
                new Label("先修课程"),
                new Label("教师"),
                new Label("班级"));
        gridPane.addColumn(1, courseIdField, nameField, creditField, typeField, capacityField, preCourseField, teacherListView, clazzListView);

        controlPanel.getChildren().addAll(gridPane,weekTimeTable,buttons, openButton, drawLotsButton);
        this.getItems().add(controlPanel);
    }

    private void displayCourses(){
        observableList.clear();

        observableList.add(Map.of("persons",List.of(),"lessons",List.of())); // 添加一个空行用于添加

        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getAllCourses", new HashMap<>()).getData()));
        courseTable.setData(observableList);
    }

    private void addCourse() {
        Map m = newMapFromFields(new HashMap<>());
        executeTask("/addCourse", m, this::displayCourses);
    }

    private void deleteCourse() {
        Map m = courseTable.getSelectedItem();
        if(m==null) {
            showAlert("未选择，无法删除");
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "确定要删除吗？");
            alert.setTitle("警告");
            Optional<ButtonType> result = alert.showAndWait();
            if(result.get() == ButtonType.OK) {
                executeTask("/deleteCourse", m, this::displayCourses);
            }
        }
    }

    private void updateCourse() {
        Map selected = courseTable.getSelectedItem();
        int index = courseTable.getSelectedIndex();
        if(selected==null) {
            showAlert("未选择，无法更新");
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "确定要更新吗？");
            alert.setTitle("警告");
            Optional<ButtonType> result = alert.showAndWait();
            if(result.get() == ButtonType.OK) {
                executeTask("/updateCourse", newMapFromFields(selected), this::displayCourses);
            }
        }
        courseTable.setSelectedItem(index);
    }

    private void openCourses() {
        List<Map> selectedCourses = courseTable.getSelectedItems();
        if (selectedCourses.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("未选择，无法开放/关闭选课");
            alert.showAndWait();
            return;
        }
        for(Map selectedCourse : selectedCourses) {
            selectedCourse.put("available", !(boolean) selectedCourse.get("available"));
            DataResponse response = request("/updateCourse", selectedCourse);
            if (response.getCode() != 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("开放/关闭选课失败: " + response.getMsg());
                alert.showAndWait();
                return;
            }
        }
        displayCourses();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("开放/关闭选课成功");
        alert.showAndWait();
    }

    private void drawLots() {
        List<Map> selectedCourses = courseTable.getSelectedItems();
        if (selectedCourses.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("未选择，无法抽签");
            alert.showAndWait();
            return;
        }
        for(Map selectedCourse : selectedCourses) {
            //Map<String, Object> requestData = new HashMap<>();
            //requestData.put("courseId", selectedCourse.get("courseId"));
            //requestData.put("capacity", selectedCourse.get("capacity"));
            DataResponse response = request("/drawLots", selectedCourse);
            if (response.getCode() != 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("抽签失败: " + response.getMsg());
                alert.showAndWait();
                return;
            }
        }
        displayCourses();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("抽签成功");
        alert.showAndWait();
    }
}