package com.management.front.controller;


import com.management.front.customComponents.SearchableListView;
import com.management.front.customComponents.SearchableTableView;
import com.management.front.request.DataResponse;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.controlsfx.property.BeanPropertyUtils;


import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.management.front.util.HttpClientUtil.*;

public class CourseManagementPage extends SplitPane {
    private SearchableTableView courseTable;
    private VBox controlPanel = new VBox();
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
    ObservableList<Map<String, Object>> originalPreCourseData = FXCollections.observableArrayList((ArrayList) request("/getAllCourses", null).getData());
    ObservableList<Map<String, Object>> filteredPreCourseData = originalPreCourseData.stream()
            .map(item -> {
                Map<String, Object> newItem = new HashMap<>(item);
                newItem.remove("preCourses");
                return newItem;
            })
            .collect(Collectors.toCollection(FXCollections::observableArrayList));

    private SearchableListView preCourseListView = new SearchableListView(filteredPreCourseData, List.of("courseId", "name"));//存储的course不包含preCourses属性，防止递归
    private SearchableListView administrativeClassListView = new SearchableListView(FXCollections.observableArrayList((ArrayList) request("/getAllAdministrativeClasses", null).getData()), List.of("name"));
    private TextField courseIdField = new TextField();
    private TextField nameField = new TextField();
    private TextField referenceField = new TextField();
    private TextField capacityField = new TextField();
    private SelectionGrid selectionGrid = new SelectionGrid();
    private WeekTimeTable weekTimeTable = new WeekTimeTable();//临时测试用

    private Map newMapFromFields(Map m) {
        m.put("courseId", courseIdField.getText());
        m.put("name", nameField.getText());
        m.put("reference", referenceField.getText());
        m.put("capacity", capacityField.getText());
        m.put("teachers", teacherListView.getSelectedItems());
        //m.put("students", studentListView.getSelectedItems());
        List<Map> studentsToBeAdded = studentListView.getSelectedItems();
        //把行政班中的学生依次添加到students中，避免重复
        List<Map> administrativeClasses = administrativeClassListView.getSelectedItems();
        for (Map<String, Object> administrativeClass : administrativeClasses) {
            List<Map<String, Object>> students = (List<Map<String, Object>>) administrativeClass.get("students");
            for (Map<String, Object> student : students) {
                if (!studentsToBeAdded.contains(student)) {
                    studentsToBeAdded.add(student);
                }
            }
        }
        m.put("students", studentsToBeAdded);

        m.put("preCourses", preCourseListView.getSelectedItems());

        selectionGrid.course=m;//把一部分course的信息给予lesson，注意顺序！
        System.out.println("selectionGrid.course:"+selectionGrid.course);
        m.put("lessons", selectionGrid.getSelectedLesson());

        return m;
    }

    public CourseManagementPage() {
        this.setWidth(1000);
        this.setDividerPosition(0, 0.7);
        initializeTable();
        initializeControlPanel();
        displayCourses();
    }

    private void initializeTable() {
        TableColumn<Map, String> courseIdColumn = new TableColumn<>("课程号");
        TableColumn<Map, String> courseNameColumn = new TableColumn<>("课程名");
        TableColumn<Map, String> courseReferenceColumn = new TableColumn<>("参考资料");
        TableColumn<Map, String> courseCapacityColumn = new TableColumn<>("课容量");
        TableColumn<Map, String> preCourseColumn = new TableColumn<>("先修课程");
        TableColumn<Map, String> teacherColumn = new TableColumn<>("教师");
        TableColumn<Map, String> studentColumn = new TableColumn<>("学生数");
        TableColumn<Map, String> availableColumn = new TableColumn<>("是否开放选课");

        courseIdColumn.setCellValueFactory(new MapValueFactory<>("courseId"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        courseReferenceColumn.setCellValueFactory(new MapValueFactory<>("reference"));
        courseCapacityColumn.setCellValueFactory(new MapValueFactory<>("capacity"));

        preCourseColumn.setCellValueFactory(new MapValueFactory<>("preCourse"));
        preCourseColumn.setCellValueFactory(data -> {

            System.out.println(data);
            System.out.println(data.getValue().get("preCourses")+"\n");

            List<Map<String, Object>> preCourses = (List<Map<String, Object>>) data.getValue().get("preCourses");
            String preCourseNames = preCourses.stream()
                    .map(preCourse -> (String) preCourse.get("name"))
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(preCourseNames);
        });

        //teacherColumn.setCellValueFactory(new MapValueFactory<>("teacher"));
        teacherColumn.setCellValueFactory(data -> {
            List<Map<String, Object>> persons = (List<Map<String, Object>>) data.getValue().get("persons");
            String personNames = persons.stream()
                    .filter(person -> person.containsKey("teacherId"))
                    .map(person -> (String) person.get("name"))
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


        List<TableColumn<Map,?>> columns = new ArrayList<>();
        columns.addAll(List.of(courseIdColumn, courseNameColumn, courseReferenceColumn, courseCapacityColumn,preCourseColumn ,teacherColumn, studentColumn, availableColumn));
        courseTable=new SearchableTableView(observableList, List.of("courseId","name","persons"), columns);

        this.getItems().add(courseTable);
    }

    private void initializeControlPanel() {
        controlPanel.setMinWidth(200);
        controlPanel.setSpacing(10);

        courseTable.setOnItemClick(course -> {
            if(course!=null)
            {
                courseIdField.setText((String) course.get("courseId"));
                nameField.setText((String) course.get("name"));
                referenceField.setText((String) course.get("reference"));
                capacityField.setText(course.get("capacity")==null?"": "" +course.get("capacity"));

                List<Map> persons = (List<Map>) course.get("persons");
                List<Map> teachers = persons.stream()
                        .filter(person -> person.containsKey("teacherId"))
                        .collect(Collectors.toList());
                teacherListView.setSelectedItems(teachers);

                List<Map> students = persons.stream()
                        .filter(person -> person.containsKey("studentId"))
                        .collect(Collectors.toList());
                studentListView.setSelectedItems(students);

                //System.out.println("preCourses"+course.get("preCourses"));
                preCourseListView.setSelectedItems((List<Map>) course.get("preCourses"));

                //administrative没法自动选中，只能添加,故设为空
                administrativeClassListView.setSelectedItems(new ArrayList<>());

                selectionGrid.setSelectedLessons((List<Map>) course.get("lessons"));
                //selectionGrid.course=course;//有问题，更新可以拿到，但添加拿到的是空的

                weekTimeTable.clear();
                //System.out.println(course.get("lessons"));
                for (Map lesson : (List<Map>) course.get("lessons")) {
                    weekTimeTable.addEvent(""+lesson.get("name"),""+lesson.get("location"),""+lesson.get("time"));
                }

            }
        });

        Pane p=new Pane();
        p.getChildren().add(weekTimeTable);//测试用

        addButton.setOnAction(event -> addCourse());
        deleteButton.setOnAction(event -> deleteCourse());
        updateButton.setOnAction(event -> updateCourse());
        HBox buttons = new HBox(addButton, deleteButton, updateButton);

        openButton.setOnAction(event -> openCourses());
        drawLotsButton.setOnAction(event -> drawLots());

        controlPanel.getChildren().addAll(courseIdField, nameField, referenceField, capacityField, preCourseListView, teacherListView, administrativeClassListView, selectionGrid, buttons, openButton, drawLotsButton);

        this.getItems().add(controlPanel);
    }

    private void displayCourses(){
        observableList.clear();

        observableList.add(Map.of("persons",List.of(),"lessons",List.of(),"preCourses",List.of())); // 添加一个空行用于添加

        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getAllCourses", null).getData()));
        courseTable.setData(observableList);

        System.out.println(observableList);

    }

    private void addCourse() {
        Map m=newMapFromFields(new HashMap<>());

        //System.out.println(m);

        DataResponse r=request("/addCourse",m);

        displayCourses();

        if(r.getCode()==-1)
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("警告");
            alert.setContentText(r.getMsg());
            alert.showAndWait();
        }
        else
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(r.getMsg());
            alert.showAndWait();
        }
    }

    private void deleteCourse() {
        Map m = courseTable.getSelectedItem();
        if(m==null)
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("未选择，无法删除");
            alert.showAndWait();
        }
        else
        {
            Alert alert=new Alert(Alert.AlertType.CONFIRMATION, "确定要删除吗？");
            alert.setTitle("警告");
            Optional<ButtonType> result=alert.showAndWait();
            if(result.get()==ButtonType.OK)
            {
                DataResponse r=request("/deleteCourse",m);
                System.out.println(m);
                System.out.println(r);

                displayCourses();

                if(r.getCode()==0)
                {
                    Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                    alert1.setContentText("删除成功");
                    alert1.showAndWait();
                }
            }
        }
    }

    private void updateCourse() {
        Map selected = courseTable.getSelectedItem();
        int index = courseTable.getSelectedIndex();
        if(selected==null)
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("未选择，无法更新");
            alert.showAndWait();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "确定要更新吗？");
        alert.setTitle("警告");
        Optional<ButtonType> result=alert.showAndWait();
        if(result.get()==ButtonType.OK)
        {
            DataResponse r=request("/updateCourse",newMapFromFields(selected));

            displayCourses();

            if(r.getCode()==0)
            {
                Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                alert1.setContentText("更新成功");
                alert1.showAndWait();
            }
        }
        courseTable.setSelectedItem(index); // 重新选中更新前的行 可能需要加一个空行用于添加
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
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("courseId", selectedCourse.get("courseId"));
            requestData.put("capacity", selectedCourse.get("capacity"));
            DataResponse response = request("/drawLots", requestData);
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

class SelectionGrid extends GridPane {
    Map course = new HashMap();
    private final int rows = 5;
    private final int cols = 7;
    private LessonBox[][] lessonBoxes = new LessonBox[rows][cols];

    public SelectionGrid() {
        super();
        initGrid();
    }

    private void initGrid() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                LessonBox lessonBox = new LessonBox();
                lessonBoxes[i][j] = lessonBox;
                this.add(lessonBox, j, i); // 添加到GridPane
            }
        }
    }

    public List<Map> getSelectedLesson(){
        List<Map> selected = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (lessonBoxes[i][j].checkBox.isSelected()) {
                    Map<String, Object> lesson = new HashMap<>();
                    lesson.put("name",course.get("name"));//course还没写初始化
                    lesson.put("location",lessonBoxes[i][j].locationField.getText());

                    //time的格式： 7,8.00,1.50
                    boolean[] eventWeek=new boolean[20];
                    if (lessonBoxes[i][j].singleWeek.isSelected())
                    {
                        for (int k = lessonBoxes[i][j].startWeek.getValue(); k <= lessonBoxes[i][j].endWeek.getValue(); k+=2) {
                            eventWeek[k-1]=true;
                        }
                    }
                    if (lessonBoxes[i][j].doubleWeek.isSelected())
                    {
                        for (int k = lessonBoxes[i][j].startWeek.getValue()+1; k <= lessonBoxes[i][j].endWeek.getValue(); k+=2) {
                            eventWeek[k-1]=true;
                        }
                    }//单双周逻辑有待完善（用户填入的开始和结束周不一定从哪开始）
                    lesson.put("eventWeek",eventWeek);
                    lesson.put("time", (j+1)+","+transferCoordinateToTime(i)+",1.50");

                    selected.add(lesson);
                }
            }
        }
        return selected;
    }

    public void setSelectedLessons(List<Map> lessons) {//-1,6,8.00,1.50  //需要写一个方法把Course的Lessons（Events）属性中的时间段转换成坐标，反之亦然
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                lessonBoxes[i][j].checkBox.setSelected(false);
                lessonBoxes[i][j].locationField.setText("");
                lessonBoxes[i][j].singleWeek.setSelected(false);
                lessonBoxes[i][j].doubleWeek.setSelected(false);
                lessonBoxes[i][j].startWeek.getValueFactory().setValue(1);
                lessonBoxes[i][j].endWeek.getValueFactory().setValue(20);
            }
        }
        for (Map s : lessons) {
            int i = transferTimeToCoordinate((s.get("time")+"").split(",")[1]);
            int j = Integer.parseInt((s.get("time")+"").split(",")[0])-1;
            lessonBoxes[i][j].checkBox.setSelected(true);
            lessonBoxes[i][j].locationField.setText((String) s.get("location"));

            //System.out.println("eventWeek:"+s.get("eventWeek"));
            List<Boolean> eventWeekList = (List<Boolean>) s.get("eventWeek");
            boolean[] eventWeek = new boolean[eventWeekList.size()];
            for (int k = 0; k < eventWeekList.size(); k++) {
                eventWeek[k] = eventWeekList.get(k);
            }
            int start = -1, end = -1;
            boolean isSingleWeek = false, isDoubleWeek = false;

            for (int k = 0; k < eventWeek.length; k++) {
                if (eventWeek[k]) {
                    if (start == -1) {
                        start = k + 1; // 保存开始周数
                    }
                    end = k + 1; // 更新结束周数

                    if ((k + 1) % 2 == 0) {
                        isDoubleWeek = true; // 如果在偶数周有课，则是双周
                    } else {
                        isSingleWeek = true; // 如果在奇数周有课，则是单周
                    }
                }
            }

            lessonBoxes[i][j].singleWeek.setSelected(isSingleWeek);
            lessonBoxes[i][j].doubleWeek.setSelected(isDoubleWeek);
            lessonBoxes[i][j].startWeek.getValueFactory().setValue(start);
            lessonBoxes[i][j].endWeek.getValueFactory().setValue(end);
        }
    }

    private int transferTimeToCoordinate(String time) {
        return switch (time) {
            case "8.00" -> 0;
            case "10.10" -> 1;
            case "14.00" -> 2;
            case "16.10" -> 3;
            case "19.00" -> 4;
            default -> 0;
        };
    }

    private String transferCoordinateToTime(int i) {
        return switch (i) {
            case 0 -> "8.00";
            case 1 -> "10.10";
            case 2 -> "14.00";
            case 3 -> "16.10";
            case 4 -> "19.00";
            default -> "8.00";
        };
    }
}

class LessonBox extends MenuButton {//选中课程时需要相应构造课程信息
    CheckBox checkBox = new CheckBox();
    TextField locationField = new TextField();
    CheckBox singleWeek = new CheckBox();
    CheckBox doubleWeek = new CheckBox();
    Spinner<Integer> startWeek = new Spinner<>(1, 20, 1);
    Spinner<Integer> endWeek = new Spinner<>(1, 20, 1);
    LessonBox(){
        this.setGraphic(checkBox);

        MenuItem item1 = new MenuItem("地点");
        item1.setGraphic(locationField);
        this.getItems().add(item1);

        MenuItem item2 = new MenuItem("单周");
        item2.setGraphic(singleWeek);
        this.getItems().add(item2);

        MenuItem item3 = new MenuItem("双周");
        item3.setGraphic(doubleWeek);
        this.getItems().add(item3);

        MenuItem item4 = new MenuItem("开始周");
        item4.setGraphic(startWeek);
        this.getItems().add(item4);

        MenuItem item5 = new MenuItem("结束周");
        item5.setGraphic(endWeek);
        this.getItems().add(item5);
    }
}