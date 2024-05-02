package com.management.client.page.admin;

import com.management.client.customComponents.SearchableTableView;
import com.management.client.request.DataResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.*;

import static com.management.client.util.HttpClientUtil.*;

public class StudentManagementPage extends SplitPane {
    private SearchableTableView studentTable;
    private final GridPane view = new GridPane();
    private final VBox controlPanel = new VBox(10, view);
    private final ObservableList<Map> observableList = FXCollections.observableArrayList();

    private StudentInfoPane studentInfoPane=new StudentInfoPane(this);
    /*private final Button addButton = new Button("添加");
    private final Button deleteButton = new Button("删除");
    private final Button updateButton = new Button("更新");
    //private Button refreshButton = new Button("Refresh");

    private final TextField nameField = new TextField();*/

    /*private final TextField idCardNumField = new TextField();
    private final Label genderLabel = new Label();
    private final Label birthdayLabel = new Label();
    private final Label homeTownLabel = new Label();

    private final TextField studentIdField = new TextField();
    private final TextField deptField = new TextField();
    private final TextField socialField = new TextField();
    private final TextField majorField = new TextField();
    private final TextField highSchoolField = new TextField();
    private final TextField addressField = new TextField();*/




    /*private Map newMapFromFields(Map m) {
        m.put("name", nameField.getText());
        m.put("idCardNum", idCardNumField.getText());
        m.put("studentId", studentIdField.getText());
        m.put("dept", deptField.getText());
        m.put("social", socialField.getText());
        m.put("major", majorField.getText());
        m.put("highSchool", highSchoolField.getText());
        m.put("address", addressField.getText());

        return m;
    }*/

    public StudentManagementPage() {
        this.setDividerPosition(0, 0.7);
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

        studentTable.setOnItemClick(student -> {
            if(student!=null)
            {
                studentInfoPane.setStudent(student);
            }
        });
        this.getItems().add(studentInfoPane.getRoot());
    }

    public void displayStudents(){
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getAllStudents", null).getData()));
        studentTable.setData(observableList);
        //System.out.println(observableList);
    }

    /*private void addStudent() {
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
    }*/
}
