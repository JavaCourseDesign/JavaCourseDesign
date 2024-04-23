package com.management.front.page.admin;

import com.management.front.customComponents.SearchableTableView;
import com.management.front.request.DataResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.util.*;

import static com.management.front.util.HttpClientUtil.*;

public class StudentManagementPage extends SplitPane {
    private SearchableTableView studentTable;
    private VBox controlPanel = new VBox();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    private Button addButton = new Button("Add");
    private Button deleteButton = new Button("Delete");
    private Button updateButton = new Button("Update");
    //private Button refreshButton = new Button("Refresh");

    private TextField studentIdField = new TextField();
    private TextField nameField = new TextField();
    private TextField genderField = new TextField();

    private TextField majorField = new TextField();

    private Map newMapFromFields(Map m) {
        m.put("studentId", studentIdField.getText());
        m.put("name", nameField.getText());
        m.put("gender", genderField.getText());
        m.put("major", majorField.getText());
        return m;
    }

    public StudentManagementPage() {
        this.setWidth(1000);
        initializeTable();
        initializeControlPanel();
        displayStudents();
        // 添加背景图片
        Image backgroundImage = new Image("E:\\JavaCourseDesign\\front\\src\\main\\resources\\com\\management\\front\\images\\menupage.jpg");
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        this.setBackground(new Background(background));
    }

    private void initializeTable() {
        // Create columns
        TableColumn<Map, String> studentIdColumn = new TableColumn<>("学号");
        TableColumn<Map, String> studentNameColumn = new TableColumn<>("姓名");
        TableColumn<Map, String> studentGenderColumn = new TableColumn<>("性别");
        TableColumn<Map, String> studentMajorColumn = new TableColumn<>("专业");

        // Set cell value factories
        studentIdColumn.setCellValueFactory(new MapValueFactory<>("studentId"));
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        studentGenderColumn.setCellValueFactory(new MapValueFactory<>("gender"));
        studentMajorColumn.setCellValueFactory(new MapValueFactory<>("major"));


        // Create a list of columns
        List<TableColumn<Map, ?>> columns = new ArrayList<>();
        columns.add(studentIdColumn);
        columns.add(studentNameColumn);
        columns.add(studentGenderColumn);
        columns.add(studentMajorColumn);
        // Initialize the SearchableTableViewForMap
        studentTable = new SearchableTableView(observableList, List.of("studentId","name"), columns);


        this.getItems().add(studentTable);
    }

    private void initializeControlPanel() {
        controlPanel.setMinWidth(200);
        controlPanel.setSpacing(10);

        controlPanel.getChildren().addAll(studentIdField, nameField, genderField, majorField, addButton, deleteButton, updateButton);

        addButton.setOnAction(event -> addStudent());
        deleteButton.setOnAction(event -> deleteStudent());
        updateButton.setOnAction(event -> updateStudent());

        studentTable.setOnItemClick(student -> {
            if(student!=null)
            {
                System.out.println("student:::"+student);
                studentIdField.setText((String) student.get("studentId"));
                nameField.setText((String) student.get("name"));
                genderField.setText((String) student.get("gender"));
                majorField.setText((String) student.get("major"));
            }
        });
        this.getItems().add(controlPanel);
    }

    private void displayStudents(){
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getAllStudents", null).getData()));
        studentTable.setData(observableList);
        //System.out.println(observableList);
    }

    private void addStudent() {
        Map m=newMapFromFields(new HashMap<>());
        System.out.println(m);
        DataResponse r=request("/addStudent",m);
        displayStudents();
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

    private void deleteStudent() {
        Map m = studentTable.getSelectedItem();
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
                DataResponse r=request("/deleteStudent",m);
                System.out.println(m);
                System.out.println(r);

                displayStudents();

                if(r.getCode()==0)
                {
                    Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                    alert1.setContentText("删除成功");
                    alert1.showAndWait();
                }
            }
        }
    }

    private void updateStudent() {
        Map selected = studentTable.getSelectedItem();
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
            DataResponse r=request("/updateStudent",newMapFromFields(selected));

            displayStudents();

            if(r.getCode()==0)
            {
                Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                alert1.setContentText("更新成功");
                alert1.showAndWait();
            }
        }
    }
}
