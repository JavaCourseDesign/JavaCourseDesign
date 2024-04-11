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

    private Button addButton = new Button("Add");
    private Button deleteButton = new Button("Delete");
    private Button updateButton = new Button("Update");
    private SearchableListView teacherListView=new SearchableListView(FXCollections.observableArrayList((ArrayList) request("/getAllTeachers", null).getData()), List.of("teacherId", "name"));
    //包含全局所有教师信息的ListView，用于选择教师
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
        m.put("personIds", teacherListView.getSelectedItems());
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
        TableColumn<Map, String> teacherColumn = new TableColumn<>("教师");
        TableColumn<Map, String> studentColumn = new TableColumn<>("学生数");

        courseIdColumn.setCellValueFactory(new MapValueFactory<>("courseId"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        courseReferenceColumn.setCellValueFactory(new MapValueFactory<>("reference"));
        courseCapacityColumn.setCellValueFactory(new MapValueFactory<>("capacity"));

        teacherColumn.setCellValueFactory(new MapValueFactory<>("teacher"));
        teacherColumn.setCellValueFactory(data -> {
            List<Map<String, Object>> persons = (List<Map<String, Object>>) data.getValue().get("persons");
            String personNames = persons.stream()
                    .filter(person -> person.containsKey("teacherId"))
                    .map(person -> (String) person.get("name"))
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(personNames);
        });

        studentColumn.setCellValueFactory(new MapValueFactory<>("student"));
        studentColumn.setCellValueFactory(data -> {
            List<Map<String, Object>> persons = (List<Map<String, Object>>) data.getValue().get("persons");
            long studentCount = persons.stream()
                    // 过滤出有studentId属性的Map对象
                    .filter(person -> person.containsKey("studentId"))
                    // 统计符合条件的项的数量
                    .count();
            return new SimpleStringProperty(studentCount + "");
        });

        List<TableColumn<Map,?>> columns = new ArrayList<>();
        columns.addAll(List.of(courseIdColumn, courseNameColumn, courseReferenceColumn, courseCapacityColumn, teacherColumn, studentColumn));
        courseTable=new SearchableTableView(observableList, List.of("courseId","name","persons"), columns);

        this.getItems().add(courseTable);
    }

    private void initializeControlPanel() {
        controlPanel.setMinWidth(200);
        controlPanel.setSpacing(10);

        addButton.setOnAction(event -> addCourse());
        deleteButton.setOnAction(event -> deleteCourse());
        updateButton.setOnAction(event -> updateCourse());

        courseTable.setOnItemClick(course -> {
            if(course!=null)
            {
                courseIdField.setText((String) course.get("courseId"));
                nameField.setText((String) course.get("name"));
                referenceField.setText((String) course.get("reference"));
                capacityField.setText(""+course.get("capacity"));
                teacherListView.setSelectedItems((List<Map>) course.get("persons"));

                /*List<String> lessonTimes = new ArrayList<>();
                for (Map lesson : (List<Map>) course.get("lessons")) {
                    lessonTimes.add((String) lesson.get("time"));
                }
                selectionGrid.setSelectedCoordinates(lessonTimes);*/

                selectionGrid.setSelectedLessons((List<Map>) course.get("lessons"));
                selectionGrid.course=course;

                weekTimeTable.clear();
                System.out.println(course.get("lessons"));
                for (Map lesson : (List<Map>) course.get("lessons")) {
                    weekTimeTable.addEvent(""+lesson.get("name"),""+lesson.get("location"),""+lesson.get("time"));
                }

                System.out.println(course);

            }
        });

        Pane p=new Pane();
        p.getChildren().add(weekTimeTable);

        controlPanel.getChildren().addAll(courseIdField, nameField, referenceField, capacityField, teacherListView, selectionGrid, addButton, deleteButton, updateButton, p);

        this.getItems().add(controlPanel);
    }

    private void displayCourses(){
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getAllCourses", null).getData()));
        courseTable.setData(observableList);

        System.out.println(observableList);

    }

    private void addCourse() {
        Map m=newMapFromFields(new HashMap<>());

        System.out.println(m);

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

            System.out.println("eventWeek:"+s.get("eventWeek"));
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