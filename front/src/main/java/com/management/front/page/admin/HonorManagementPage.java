package com.management.front.page.admin;

import com.management.front.customComponents.SearchableListView;
import com.management.front.customComponents.SearchableTableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.management.front.util.HttpClientUtil.request;

public class HonorManagementPage extends SplitPane {

    private SearchableTableView honorTable;
    private TableView studentTable=new TableView();
    private GridPane gridPane = new GridPane();
    private VBox controlPanel = new VBox();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();
    private ObservableList<Map> studentObservableList = FXCollections.observableArrayList();
    private SearchableListView studentListView= new SearchableListView(FXCollections.observableArrayList((ArrayList) request("/getAllStudents", null).getData()), List.of("name", "studentId"));
    private SearchableListView eventListView=new SearchableListView(FXCollections.observableArrayList((ArrayList) request("/getAllEventsExceptLessons",null).getData()), List.of("name","time"));
    private Button addButton = new Button("Add");
    private Button deleteButton = new Button("Delete");
    private Button updateButton = new Button("Update");
    private DatePicker awardTimePicker=new DatePicker();
    private TextField nameField = new TextField("优秀团员");
    private TextField departmentField = new TextField("软院团支部");

    public HonorManagementPage() {
        this.setWidth(1000);
        initializeTable();
        initializeStudentTable();
        initializeControlPanel();
        displayHonors();
    }
    private Map newMapFromFields(Map m)
    {
        m.put("studentList",studentListView.getSelectedItems());
        m.put("name",nameField.getText());
        m.put("department",departmentField.getText());
        m.put("awardTime",awardTimePicker.getValue()+"");
        m.put("event",eventListView.getSelectedItems().get(0));
        return m;
    }
    private void displayHonors() {
        observableList.clear();
        observableList.add(Map.of());
        ArrayList<Map> hList=(ArrayList<Map>) request("/getAllHonors", null).getData();
        System.out.println(hList);
        observableList.addAll(FXCollections.observableArrayList(hList));
        honorTable.setData(observableList);
    }
    private void initializeControlPanel() {
        gridPane.addColumn(0,
                new Label("学生"),
                new Label("荣誉名称"),
                new Label("颁奖部门"),
                new Label("颁奖时间"),
                new Label("获奖事件")
        );
        gridPane.addColumn(1,
                studentListView,
                nameField,
                departmentField,
                awardTimePicker,
                eventListView
        );
        gridPane.addRow(6, addButton, deleteButton, updateButton);
        addButton.setOnAction(event -> addHonor());
        deleteButton.setOnAction(event ->deleteHonors());
        updateButton.setOnAction(event -> updateHonor());

        honorTable.setOnItemClick(h->{
            if(honorTable.getSelectedIndex()==0)
            {
                addButton.setDisable(false);
                studentTable.setVisible(false);
                nameField.setText("");
                departmentField.setText("");
                awardTimePicker.setValue(null);
                eventListView.setSelectedItems(List.of());
                studentListView.setSelectedItems(List.of());
                return;
            }
            addButton.setDisable(true);
            nameField.setText((String) h.get("name"));
            departmentField.setText((String) h.get("department"));
            awardTimePicker.setValue(LocalDate.parse((String) h.get("awardTime")));
            displayStudents(h);
            studentTable.setVisible(true);
        });
        controlPanel.getChildren().add(gridPane);
        studentTable.setVisible(false);
        controlPanel.getChildren().add(studentTable);
        this.getItems().add(controlPanel);
    }

    private void updateHonor() {
    }

    private void deleteHonors() {

    }

    private void addHonor() {

    }

    private void initializeTable(){
        TableColumn<Map, String> nameColumn = new TableColumn<>("荣誉名称");
        TableColumn<Map, String> awardTimeColumn = new TableColumn<>("颁奖时间");
        TableColumn<Map, String> departmentColumn = new TableColumn<>("颁奖部门");
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        awardTimeColumn.setCellValueFactory(new MapValueFactory<>("awardTime"));
        departmentColumn.setCellValueFactory(new MapValueFactory<>("department"));
        List<TableColumn<Map, ?>> columns = new ArrayList<>();
        columns.add(nameColumn);
        columns.add(awardTimeColumn);
        columns.add(departmentColumn);
        honorTable = new SearchableTableView(observableList, List.of("name","department","awardTime"),columns);
        this.getItems().add(honorTable);
    }
    private void initializeStudentTable()
    {
        TableColumn<Map, String> nameColumn = new TableColumn<>("学生姓名");
        TableColumn<Map, String> studentIdColumn = new TableColumn<>("学号");
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        studentIdColumn.setCellValueFactory(new MapValueFactory<>("studentId"));
        studentTable.getColumns().addAll(nameColumn,studentIdColumn);
    }
    private void displayStudents(Map m) {
        studentObservableList.clear();
        ArrayList<Map> studentlist=(ArrayList<Map>) request("/getStudentsByHonor",m).getData();
        studentListView.setSelectedItems(studentlist);
        studentObservableList.addAll(FXCollections.observableArrayList(studentlist));
        studentTable.setItems(studentObservableList);
    }

}
