package com.management.front.controller;

import com.management.front.customComponents.SearchableListView;
import com.management.front.customComponents.SearchableTableView;
import com.management.front.request.DataResponse;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

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

    private Map newMapFromFields(Map m) {
        m.put("courseId", courseIdField.getText());
        m.put("name", nameField.getText());
        m.put("reference", referenceField.getText());
        m.put("capacity", capacityField.getText());
        m.put("personIds", teacherListView.getSelectedItems());
        m.put("lessonTimes", selectionGrid.getSelectedCoordinates());
        return m;
    }

    public CourseManagementPage() {
        this.setWidth(1000);
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
                //selectionGrid.setSelectedCoordinates((List<String>) course.get("lessons"));
                System.out.println("lessons:"+course.get("lessons"));
                List<String> lessonTimes = new ArrayList<>();
                for (Map lesson : (List<Map>) course.get("lessons")) {
                    lessonTimes.add((String) lesson.get("time"));
                }
                selectionGrid.setSelectedCoordinates(lessonTimes);
            }
        });

        controlPanel.getChildren().addAll(courseIdField, nameField, referenceField, capacityField, teacherListView, selectionGrid, addButton, deleteButton, updateButton);

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
    private final int rows = 5;
    private final int cols = 7;
    private CheckBox[][] checkBoxes = new CheckBox[rows][cols];

    public SelectionGrid() {
        super();
        initGrid();
    }

    private void initGrid() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                CheckBox checkBox = new CheckBox();
                checkBoxes[i][j] = checkBox;
                this.add(checkBox, j, i); // 添加到GridPane
            }
        }
    }

    public List<String> getSelectedCoordinates() {
        List<String> selected = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (checkBoxes[i][j].isSelected()) {
                    selected.add("-1,"+(j+1)+","+transferCoordinateToTime(i)+",1.50"); // 储存坐标 i节数 j天数
                }
            }
        }
        return selected;
    }

    public void setSelectedCoordinates(List<String> selected) {//-1,6,8.00,1.50  //需要写一个方法把Course的Lessons（Events）属性中的时间段转换成坐标，反之亦然
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                checkBoxes[i][j].setSelected(false);
            }
        }
        for (String s : selected) {
            int i = transferTimeToCoordinate(s.split(",")[2]);
            int j = Integer.parseInt(s.split(",")[1])-1;
            checkBoxes[i][j].setSelected(true);
        }
    }

    private int transferTimeToCoordinate(String time) {
        return switch (time) {
            case "8.00" -> 0;
            case "10.10" -> 1;
            case "14.00" -> 2;
            case "14.10" -> 3;
            case "19.00" -> 4;
            default -> 0;
        };
    }

    private String transferCoordinateToTime(int i) {
        return switch (i) {
            case 0 -> "8.00";
            case 1 -> "10.10";
            case 2 -> "14.00";
            case 3 -> "14.10";
            case 4 -> "19.00";
            default -> "8.00";
        };
    }
}