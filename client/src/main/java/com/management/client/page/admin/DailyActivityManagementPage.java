package com.management.client.page.admin;

import com.management.client.customComponents.SearchableListView;
import com.management.client.customComponents.SearchableTableView;
import com.management.client.customComponents.WeekTimeTable;
import com.management.client.request.DataResponse;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.tableview2.FilteredTableColumn;

import java.util.*;

import static com.management.client.util.HttpClientUtil.request;

public class DailyActivityManagementPage extends SplitPane {
    private SearchableTableView dailyActivityTable;
    private TableView studentTable=new TableView();
    private VBox controlPanel = new VBox();
    private GridPane gridPane = new GridPane();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();
    private ObservableList<Map> studentObservableList = FXCollections.observableArrayList();
    private SearchableListView studentListView;

    private Button addButton = new Button("增加");
    private Button deleteButton = new Button("删除");
    private Button updateButton = new Button("更新");
    private HBox buttonBox=new HBox();
    private TextField nameField = new TextField();
    private ComboBox<String> typeField = new ComboBox<>(FXCollections.observableArrayList(
            "体育活动","外出旅游","文艺演出","聚会"
    ));
    private WeekTimeTable eventPicker=new WeekTimeTable();
    private TextField locationField = new TextField();
    private ObservableList<Map<String,Object>> allStudentsList=FXCollections.observableArrayList((ArrayList) request("/getAllStudents", null).getData());
    public DailyActivityManagementPage() {
        this.setWidth(1000);
        studentListView=new SearchableListView(allStudentsList, List.of("name","studentId"));
        initializeTable();
        initializeStudentTable();
        initializeControlPanel();
        displayDailyActivities();
    }
    private Map newMapFromFields(Map m)
    {
        m.put("studentList",studentListView.getSelectedItems());
        m.put("name",nameField.getText());
        m.put("type",typeField.getValue());

        m.put("location",locationField.getText());
        return m;
    }
    private void displayDailyActivities() {
        observableList.clear();
        observableList.add(Map.of());
        ArrayList<Map> dList=(ArrayList<Map>) request("/getAllDailyActivities", null).getData();
        //System.out.println(dList);
        observableList.addAll(FXCollections.observableArrayList(dList));
        dailyActivityTable.setData(observableList);
    }
    private void addDailyActivity() {
        Map m=newMapFromFields(new HashMap());
        if(m.get("name")==null||m.get("type")==null||m.get("location")==null||eventPicker.getEvents().isEmpty()||
                studentListView.getSelectedItems().isEmpty()||m.get("name").equals("")||m.get("type").equals("")||m.get("location").equals(""))
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("警告");
            alert.setContentText("请填写完整信息");
            alert.showAndWait();
            studentListView.setSelectedItems(List.of());
            return;
        }
        if(eventPicker.getEvents().size()>1)
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("警告");
            alert.setContentText("只能选择一个事件");
            alert.showAndWait();
            return;
        }
        m.put("startDate",eventPicker.getEvents().get(0).get("startDate"));
        m.put("startTime",eventPicker.getEvents().get(0).get("startTime"));
        m.put("endDate",eventPicker.getEvents().get(0).get("endDate"));
        m.put("endTime",eventPicker.getEvents().get(0).get("endTime"));
        DataResponse r=request("/addDailyActivity",m);
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
        displayDailyActivities();
        studentListView.setSelectedItems(List.of());
    }
    private void deleteDailyActivity() {
        if(dailyActivityTable.getSelectedItems().isEmpty())
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("未选择，无法删除");
            alert.showAndWait();
        }
        List<Map> innovationList=new ArrayList<>(dailyActivityTable.getSelectedItems());
        Alert alert=new Alert(Alert.AlertType.CONFIRMATION, "确定要删除吗？");
        alert.setTitle("警告");
        Optional<ButtonType> result=alert.showAndWait();
        if(result.get()==ButtonType.OK)
        {
            DataResponse r=request("/deleteDailyActivities",innovationList);
            if(r.getCode()==0)
            {
                Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                alert1.setContentText("删除成功");
                alert1.showAndWait();
            }
           displayDailyActivities();
        }
        dailyActivityTable.setSelectedItem(0);
    }
    private void updateDailyActivity() {

        if(dailyActivityTable.getSelectedItems().isEmpty()||dailyActivityTable.getSelectedIndex()==0)
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("未选择，无法更新");
            alert.showAndWait();
            return;
        }
        if(dailyActivityTable.getSelectedItems().size()>1)
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("只能选择一个项目");
            alert.showAndWait();
            return;
        }
        Map m=newMapFromFields(dailyActivityTable.getSelectedItems().get(0));
        m.put("startDate",eventPicker.getEvents().get(0).get("startDate"));
        m.put("startTime",eventPicker.getEvents().get(0).get("startTime"));
        m.put("endDate",eventPicker.getEvents().get(0).get("endDate"));
        m.put("endTime",eventPicker.getEvents().get(0).get("endTime"));
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "确定要更新吗？");
        alert.setTitle("警告");
        Optional<ButtonType> result=alert.showAndWait();
        if(result.get()==ButtonType.OK)
        {
            DataResponse r=request("/updateDailyActivity",m);
            displayDailyActivities();
            displayStudents(m);
            if(r.getCode()==0)
            {
                Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                alert1.setContentText("更新成功");
                alert1.showAndWait();
            }
        }
    }
    private void initializeControlPanel() {
        gridPane.addColumn(0,
                new Label("学生"),
                new Label("活动名称"),
                new Label("活动类型"),
                new Label("活动时间"),
                new Label("活动地点")
        );
        gridPane.addColumn(1,
                studentListView,
                nameField,
                typeField,
                eventPicker,
                locationField
        );
        gridPane.getColumnConstraints().addAll(
                new ColumnConstraints(100),
                new ColumnConstraints(500)
        );
        //gridPane.getColumnConstraints().get(1).setHgrow(Priority.ALWAYS);
        buttonBox.setSpacing(20);
        buttonBox.getChildren().addAll(addButton,deleteButton,updateButton);
        addButton.setOnAction(event -> addDailyActivity());
        deleteButton.setOnAction(event ->deleteDailyActivity());
        updateButton.setOnAction(event -> updateDailyActivity());
        dailyActivityTable.setOnItemClick(d->{
            if(dailyActivityTable.getSelectedIndex()==0)
            {
                addButton.setDisable(false);
                studentTable.setVisible(false);
                nameField.setText("");
                typeField.setValue("");
                eventPicker.setEvents(List.of());
                locationField.setText("");
                studentListView.setSelectedItems(List.of());
                return;
            }
            addButton.setDisable(true);
            nameField.setText((String) d.get("name"));
            typeField.setValue((String) d.get("type"));
            eventPicker.setEvents(List.of(d));
            locationField.setText((String) d.get("location"));
            displayStudents(d);
            studentTable.setVisible(true);
        });
        controlPanel.getChildren().add(gridPane);
        controlPanel.getChildren().add(buttonBox);
        studentTable.setVisible(false);
        controlPanel.getChildren().add(studentTable);
        this.getItems().add(controlPanel);
    }
    private void displayStudents(Map m) {
        studentObservableList.clear();
        ArrayList<Map> studentList=(ArrayList<Map>) request("/getStudentsInfoByDailyActivity",m).getData();
        studentObservableList.addAll(FXCollections.observableArrayList(studentList));
        ArrayList<Map> newList=new ArrayList<>();
        for(Map student:allStudentsList)
        {
            for(Map s:studentList)
            {
                if(student.get("studentId").equals(s.get("studentId")))
                {
                    newList.add(student);
                }
            }
        }
        studentListView.setSelectedItems(newList);
        studentTable.setItems(studentObservableList);
    }
    private void initializeStudentTable()
    {
        FilteredTableColumn<Map, String> nameColumn = new FilteredTableColumn<>("学生姓名");
        FilteredTableColumn<Map, String> studentIdColumn = new FilteredTableColumn<>("学号");
        FilteredTableColumn<Map,String> performanceColumn=new FilteredTableColumn<>("评价");
        performanceColumn.setCellValueFactory(new MapValueFactory<>("performance"));
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        studentIdColumn.setCellValueFactory(new MapValueFactory<>("studentId"));
        studentTable.getColumns().addAll(nameColumn,studentIdColumn,performanceColumn);
    }
    private void initializeTable()
    {
        FilteredTableColumn<Map, String> nameColumn = new FilteredTableColumn<>("项目名称");
        FilteredTableColumn<Map, String> typeColumn = new FilteredTableColumn<>("项目类型");
        FilteredTableColumn<Map, String> timeColumn = new FilteredTableColumn<>("时间");
        FilteredTableColumn<Map, String> locationColumn = new FilteredTableColumn<>("地点");
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        typeColumn.setCellValueFactory(new MapValueFactory<>("type"));
        timeColumn.setCellValueFactory(data ->
        {
            if (!data.getValue().isEmpty()) {
                Map<String, Object> event = (Map<String, Object>) data.getValue();
                String time = event.get("startDate") + " " + event.get("startTime") + "-" + event.get("endDate") + " " + event.get("endTime");
                return new SimpleStringProperty(time);
            } else return new SimpleStringProperty("");
        });
        locationColumn.setCellValueFactory(new MapValueFactory<>("location"));
        //搜索使用
        List<FilteredTableColumn<Map, ?>> columns = new ArrayList<>();
        columns.add(nameColumn);
        columns.add(typeColumn);
        columns.add(timeColumn);
        columns.add(locationColumn);
        dailyActivityTable = new SearchableTableView(observableList, List.of("name","type"), columns);
        this.getItems().add(dailyActivityTable);
    }

}
