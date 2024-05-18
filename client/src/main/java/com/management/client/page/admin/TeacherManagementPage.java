package com.management.client.page.admin;

import com.management.client.customComponents.SearchableTableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SplitPane;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.VBox;
import org.controlsfx.control.tableview2.FilteredTableColumn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.management.client.util.HttpClientUtil.request;

public class TeacherManagementPage extends SplitPane {
    private SearchableTableView teacherTable;
    private VBox controlPanel = new VBox();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    private TeacherInfoPane teacherInfoPane=new TeacherInfoPane(this);
    /*private Button addButton = new Button("Add");
    private Button deleteButton = new Button("Delete");
    private Button updateButton = new Button("Update");

    private TextField teacherIdField = new TextField();
    private TextField nameField = new TextField();
    private TextField genderField = new TextField();
    private TextField titleField = new TextField();

    private Map newMapFromFields(Map m) {
        m.put("teacherId", teacherIdField.getText());
        m.put("name", nameField.getText());
        m.put("gender", genderField.getText());
        m.put("title", titleField.getText());
        return m;
    }*/

    public TeacherManagementPage() {
        this.setDividerPosition(0, 0.7);
        initializeTable();
        initializeControlPanel();
        displayTeachers();
    }

    private void initializeTable() {

        // Create columns
        FilteredTableColumn<Map, String> teacherNameColumn = new FilteredTableColumn<>("姓名");
        FilteredTableColumn<Map, String> idCardNumColumn = new FilteredTableColumn<>("身份证号");
        FilteredTableColumn<Map, String> teacherGenderColumn = new FilteredTableColumn<>("性别");
        FilteredTableColumn<Map, String> teacherBirthdayColumn = new FilteredTableColumn<>("生日");
        FilteredTableColumn<Map, String> teacherHomeTownColumn = new FilteredTableColumn<>("籍贯");
        FilteredTableColumn<Map, String> teacherIdColumn = new FilteredTableColumn<>("工号");
        FilteredTableColumn<Map, String> teacherDeptColumn = new FilteredTableColumn<>("系别");
        FilteredTableColumn<Map, String> teacherSocialColumn = new FilteredTableColumn<>("政治面貌");
        FilteredTableColumn<Map, String> teacherDegreeColumn = new FilteredTableColumn<>("学位");
        FilteredTableColumn<Map, String> teacherTitleColumn = new FilteredTableColumn<>("职称");
        FilteredTableColumn<Map, String> teacherAddressColumn = new FilteredTableColumn<>("地址");


        // Set cell value factories
        teacherNameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        idCardNumColumn.setCellValueFactory(new MapValueFactory<>("idCardNum"));
        teacherGenderColumn.setCellValueFactory(new MapValueFactory<>("gender"));
        teacherBirthdayColumn.setCellValueFactory(new MapValueFactory<>("birthday"));
        teacherHomeTownColumn.setCellValueFactory(new MapValueFactory<>("homeTown"));
        teacherIdColumn.setCellValueFactory(new MapValueFactory<>("teacherId"));
        teacherDeptColumn.setCellValueFactory(new MapValueFactory<>("dept"));
        teacherSocialColumn.setCellValueFactory(new MapValueFactory<>("social"));
        teacherDegreeColumn.setCellValueFactory(new MapValueFactory<>("degree"));
        teacherTitleColumn.setCellValueFactory(new MapValueFactory<>("title"));
        teacherAddressColumn.setCellValueFactory(new MapValueFactory<>("address"));

        /*PopupFilter<Map,String> teacherNameFilter = new PopupContainsFilter<>(teacherNameColumn);
        PopupFilter<Map,String> idCardNumFilter = new PopupContainsFilter<>(idCardNumColumn);
        PopupFilter<Map,String> teacherGenderFilter = new PopupContainsFilter<>(teacherGenderColumn);
        PopupFilter<Map,String> teacherBirthdayFilter = new PopupContainsFilter<>(teacherBirthdayColumn);
        PopupFilter<Map,String> teacherHomeTownFilter = new PopupContainsFilter<>(teacherHomeTownColumn);
        PopupFilter<Map,String> teacherIdFilter = new PopupContainsFilter<>(teacherIdColumn);
        PopupFilter<Map,String> teacherDeptFilter = new PopupContainsFilter<>(teacherDeptColumn);
        PopupFilter<Map,String> teacherSocialFilter = new PopupContainsFilter<>(teacherSocialColumn);
        PopupFilter<Map,String> teacherMajorFilter = new PopupContainsFilter<>(teacherMajorColumn);
        PopupFilter<Map,String> teacherHighSchoolFilter = new PopupContainsFilter<>(teacherHighSchoolColumn);
        PopupFilter<Map,String> teacherAddressFilter = new PopupContainsFilter<>(teacherAddressColumn);



        teacherNameColumn.setOnFilterAction(e -> teacherNameFilter.showPopup());
        idCardNumColumn.setOnFilterAction(e -> idCardNumFilter.showPopup());
        teacherGenderColumn.setOnFilterAction(e -> teacherGenderFilter.showPopup());
        teacherBirthdayColumn.setOnFilterAction(e -> teacherBirthdayFilter.showPopup());
        teacherHomeTownColumn.setOnFilterAction(e -> teacherHomeTownFilter.showPopup());
        teacherIdColumn.setOnFilterAction(e -> teacherIdFilter.showPopup());
        teacherDeptColumn.setOnFilterAction(e -> teacherDeptFilter.showPopup());
        teacherSocialColumn.setOnFilterAction(e -> teacherSocialFilter.showPopup());
        teacherMajorColumn.setOnFilterAction(e -> teacherMajorFilter.showPopup());
        teacherHighSchoolColumn.setOnFilterAction(e -> teacherHighSchoolFilter.showPopup());
        teacherAddressColumn.setOnFilterAction(e -> teacherAddressFilter.showPopup());*/


        // Create a list of columns
        List<FilteredTableColumn<Map, ?>> columns = new ArrayList<>(List.of(teacherNameColumn, idCardNumColumn, teacherGenderColumn, teacherBirthdayColumn, teacherHomeTownColumn, teacherIdColumn, teacherDeptColumn, teacherSocialColumn, teacherDegreeColumn, teacherTitleColumn, teacherAddressColumn));
        // Initialize the SearchableTableViewForMap
        teacherTable = new SearchableTableView(observableList, List.of("teacherId","name"), columns);


        this.getItems().add(teacherTable);
    }

    private void initializeControlPanel() {
        teacherTable.setOnItemClick(teacher -> {
            if(teacher!=null)
            {
                teacherInfoPane.setTeacher(teacher);
            }
        });
        this.getItems().add(teacherInfoPane.getRoot());
    }

    public void displayTeachers(){
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getAllTeachers", null).getData()));
        teacherTable.setData(observableList);

        //System.out.println(observableList);

    }

    /*private void addTeacher() {
        Map m=newMapFromFields(new HashMap<>());

        System.out.println(m);

        DataResponse r=request("/addTeacher",m);

        displayTeachers();

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

    private void deleteTeacher() {
        Map m = teacherTable.getSelectedItem();
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
                DataResponse r=request("/deleteTeacher",m);
                System.out.println(m);
                System.out.println(r);

                displayTeachers();

                if(r.getCode()==0)
                {
                    Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                    alert1.setContentText("删除成功");
                    alert1.showAndWait();
                }
            }
        }
    }

    private void updateTeacher() {
        Map selected = teacherTable.getSelectedItem();
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
            DataResponse r=request("/updateTeacher",newMapFromFields(selected));

            displayTeachers();

            if(r.getCode()==0)
            {
                Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                alert1.setContentText("更新成功");
                alert1.showAndWait();
            }
        }
    }*/
}