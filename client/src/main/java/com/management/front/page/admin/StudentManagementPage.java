package com.management.front.page.admin;

import com.management.front.customComponents.SearchableTableView;
import com.management.front.request.DataResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.*;

import static com.management.front.util.HttpClientUtil.*;

public class StudentManagementPage extends SplitPane {
    private SearchableTableView studentTable;
    private final GridPane view = new GridPane();
    private final VBox controlPanel = new VBox(10, view);
    private final ObservableList<Map> observableList = FXCollections.observableArrayList();

    private final Button addButton = new Button("添加");
    private final Button deleteButton = new Button("删除");
    private final Button updateButton = new Button("更新");
    //private Button refreshButton = new Button("Refresh");

    private final TextField nameField = new TextField();

    private final TextField idCardNumField = new TextField();
    private final Label genderLabel = new Label();
    private final Label birthdayLabel = new Label();
    private final Label homeTownLabel = new Label();

    private final TextField studentIdField = new TextField();
    private final TextField deptField = new TextField();
    private final TextField socialField = new TextField();
    private final TextField majorField = new TextField();
    private final TextField highSchoolField = new TextField();
    private final TextField addressField = new TextField();




    private Map newMapFromFields(Map m) {
        m.put("name", nameField.getText());
        m.put("idCardNum", idCardNumField.getText());
        m.put("studentId", studentIdField.getText());
        m.put("dept", deptField.getText());
        m.put("social", socialField.getText());
        m.put("major", majorField.getText());
        m.put("highSchool", highSchoolField.getText());
        m.put("address", addressField.getText());

        return m;
    }

    public StudentManagementPage() {
        this.setWidth(1000);
        initializeTable();
        initializeControlPanel();
        displayStudents();
    }

    private void initializeTable() {
        // Create columns
        TableColumn<Map, String> studentNameColumn = new TableColumn<>("姓名");
        TableColumn<Map, String> idCardNumColumn = new TableColumn<>("身份证号");
        TableColumn<Map, String> studentGenderColumn = new TableColumn<>("性别");
        TableColumn<Map, String> studentBirthdayColumn = new TableColumn<>("生日");
        TableColumn<Map, String> studentHomeTownColumn = new TableColumn<>("籍贯");
        TableColumn<Map, String> studentIdColumn = new TableColumn<>("学号");
        TableColumn<Map, String> studentDeptColumn = new TableColumn<>("系别");
        TableColumn<Map, String> studentSocialColumn = new TableColumn<>("政治面貌");
        TableColumn<Map, String> studentMajorColumn = new TableColumn<>("专业");
        TableColumn<Map, String> studentHighSchoolColumn = new TableColumn<>("毕业高中");
        TableColumn<Map, String> studentAddressColumn = new TableColumn<>("地址");


        // Set cell value factories
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        idCardNumColumn.setCellValueFactory(new MapValueFactory<>("idCardNum"));
        studentGenderColumn.setCellValueFactory(new MapValueFactory<>("gender"));
        studentBirthdayColumn.setCellValueFactory(new MapValueFactory<>("birthday"));
        studentHomeTownColumn.setCellValueFactory(new MapValueFactory<>("homeTown"));
        studentIdColumn.setCellValueFactory(new MapValueFactory<>("studentId"));
        studentDeptColumn.setCellValueFactory(new MapValueFactory<>("dept"));
        studentSocialColumn.setCellValueFactory(new MapValueFactory<>("social"));
        studentMajorColumn.setCellValueFactory(new MapValueFactory<>("major"));
        studentHighSchoolColumn.setCellValueFactory(new MapValueFactory<>("highSchool"));
        studentAddressColumn.setCellValueFactory(new MapValueFactory<>("address"));


        // Create a list of columns
        List<TableColumn<Map, ?>> columns = new ArrayList<>(List.of(studentNameColumn, idCardNumColumn, studentGenderColumn, studentBirthdayColumn, studentHomeTownColumn, studentIdColumn, studentDeptColumn, studentSocialColumn, studentMajorColumn, studentHighSchoolColumn, studentAddressColumn));
        // Initialize the SearchableTableViewForMap
        studentTable = new SearchableTableView(observableList, List.of("studentId","name"), columns);


        this.getItems().add(studentTable);
    }

    private void initializeControlPanel() {
        view.setHgap(10);
        view.setVgap(10);
        view.add(new Label("姓名"), 0, 0);
        view.add(nameField, 1, 0);
        view.add(new Label("身份证号"), 0, 1);
        view.add(idCardNumField, 1, 1);
        view.add(new Label("性别"), 0, 2);
        view.add(genderLabel, 1, 2);
        view.add(new Label("生日"), 0, 3);
        view.add(birthdayLabel, 1, 3);
        view.add(new Label("籍贯"), 0, 4);
        view.add(homeTownLabel, 1, 4);
        view.add(new Label("学号"), 0, 5);
        view.add(studentIdField, 1, 5);
        view.add(new Label("系别"), 0, 6);
        view.add(deptField, 1, 6);
        view.add(new Label("政治面貌"), 0, 7);
        view.add(socialField, 1, 7);
        view.add(new Label("专业"), 0, 8);
        view.add(majorField, 1, 8);
        view.add(new Label("毕业高中"), 0, 9);
        view.add(highSchoolField, 1, 9);
        view.add(new Label("地址"), 0, 10);
        view.add(addressField, 1, 10);

        controlPanel.getChildren().addAll(addButton, deleteButton, updateButton);

        addButton.setOnAction(event -> addStudent());
        deleteButton.setOnAction(event -> deleteStudent());
        updateButton.setOnAction(event -> updateStudent());

        studentTable.setOnItemClick(student -> {
            if(student!=null)
            {
                nameField.setText((String) student.get("name"));
                idCardNumField.setText((String) student.get("idCardNum"));
                genderLabel.setText((String) student.get("gender"));
                birthdayLabel.setText((String) student.get("birthday"));
                homeTownLabel.setText((String) student.get("homeTown"));
                studentIdField.setText((String) student.get("studentId"));
                deptField.setText((String) student.get("dept"));
                socialField.setText((String) student.get("social"));
                majorField.setText((String) student.get("major"));
                highSchoolField.setText((String) student.get("highSchool"));
                addressField.setText((String) student.get("address"));
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
