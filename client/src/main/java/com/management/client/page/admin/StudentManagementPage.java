package com.management.client.page.admin;

import com.management.client.customComponents.SearchableTableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SplitPane;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.tableview2.FilteredTableColumn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.management.client.util.HttpClientUtil.request;

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
        FilteredTableColumn<Map, String> studentNameColumn = new FilteredTableColumn<>("姓名");
        FilteredTableColumn<Map, String> idCardNumColumn = new FilteredTableColumn<>("身份证号");
        FilteredTableColumn<Map, String> studentGenderColumn = new FilteredTableColumn<>("性别");
        FilteredTableColumn<Map, String> studentBirthdayColumn = new FilteredTableColumn<>("生日");
        FilteredTableColumn<Map, String> studentHomeTownColumn = new FilteredTableColumn<>("籍贯");
        FilteredTableColumn<Map, String> studentIdColumn = new FilteredTableColumn<>("学号");
        FilteredTableColumn<Map, String> studentDeptColumn = new FilteredTableColumn<>("系别");
        FilteredTableColumn<Map, String> studentSocialColumn = new FilteredTableColumn<>("政治面貌");
        FilteredTableColumn<Map, String> studentMajorColumn = new FilteredTableColumn<>("专业");
        FilteredTableColumn<Map, String> studentHighSchoolColumn = new FilteredTableColumn<>("毕业高中");
        FilteredTableColumn<Map, String> studentAddressColumn = new FilteredTableColumn<>("地址");


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

        /*PopupFilter<Map,String> studentNameFilter = new PopupContainsFilter<>(studentNameColumn);
        PopupFilter<Map,String> idCardNumFilter = new PopupContainsFilter<>(idCardNumColumn);
        PopupFilter<Map,String> studentGenderFilter = new PopupContainsFilter<>(studentGenderColumn);
        PopupFilter<Map,String> studentBirthdayFilter = new PopupContainsFilter<>(studentBirthdayColumn);
        PopupFilter<Map,String> studentHomeTownFilter = new PopupContainsFilter<>(studentHomeTownColumn);
        PopupFilter<Map,String> studentIdFilter = new PopupContainsFilter<>(studentIdColumn);
        PopupFilter<Map,String> studentDeptFilter = new PopupContainsFilter<>(studentDeptColumn);
        PopupFilter<Map,String> studentSocialFilter = new PopupContainsFilter<>(studentSocialColumn);
        PopupFilter<Map,String> studentMajorFilter = new PopupContainsFilter<>(studentMajorColumn);
        PopupFilter<Map,String> studentHighSchoolFilter = new PopupContainsFilter<>(studentHighSchoolColumn);
        PopupFilter<Map,String> studentAddressFilter = new PopupContainsFilter<>(studentAddressColumn);



        studentNameColumn.setOnFilterAction(e -> studentNameFilter.showPopup());
        idCardNumColumn.setOnFilterAction(e -> idCardNumFilter.showPopup());
        studentGenderColumn.setOnFilterAction(e -> studentGenderFilter.showPopup());
        studentBirthdayColumn.setOnFilterAction(e -> studentBirthdayFilter.showPopup());
        studentHomeTownColumn.setOnFilterAction(e -> studentHomeTownFilter.showPopup());
        studentIdColumn.setOnFilterAction(e -> studentIdFilter.showPopup());
        studentDeptColumn.setOnFilterAction(e -> studentDeptFilter.showPopup());
        studentSocialColumn.setOnFilterAction(e -> studentSocialFilter.showPopup());
        studentMajorColumn.setOnFilterAction(e -> studentMajorFilter.showPopup());
        studentHighSchoolColumn.setOnFilterAction(e -> studentHighSchoolFilter.showPopup());
        studentAddressColumn.setOnFilterAction(e -> studentAddressFilter.showPopup());*/


        // Create a list of columns
        List<FilteredTableColumn<Map, ?>> columns = new ArrayList<>(List.of(studentNameColumn, idCardNumColumn, studentGenderColumn, studentBirthdayColumn, studentHomeTownColumn, studentIdColumn, studentDeptColumn, studentSocialColumn, studentMajorColumn, studentHighSchoolColumn, studentAddressColumn));
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

}

