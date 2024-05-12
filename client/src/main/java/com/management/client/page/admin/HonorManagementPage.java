package com.management.client.page.admin;

import com.management.client.customComponents.SearchableListView;
import com.management.client.customComponents.SearchableTableView;
import com.management.client.customComponents.WeekTimeTable;
import com.management.client.request.DataResponse;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.tableview2.FilteredTableColumn;

import java.time.LocalDate;
import java.util.*;

import static com.management.client.util.HttpClientUtil.request;

public class HonorManagementPage extends SplitPane {

    private SearchableTableView honorTable;
    private TableView studentTable=new TableView();
    private GridPane gridPane = new GridPane();
    private VBox controlPanel = new VBox();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();
    private ObservableList<Map> studentObservableList = FXCollections.observableArrayList();
    //private SearchableListView studentListView= new SearchableListView(FXCollections.observableArrayList((ArrayList) request("/getAllStudents", null).getData()), List.of("name", "studentId"));
    private SearchableListView studentListView= new SearchableListView(FXCollections.observableArrayList(List.of()), List.of());
    private WeekTimeTable eventView=new WeekTimeTable();
    private Button displayButton=new Button("学生(点击以查看):");
    private Button addButton = new Button("增加");
    private Button deleteButton = new Button("删除");
    private Button updateButton = new Button("更新");
    private HBox buttonBox=new HBox();
    private DatePicker awardDatePicker=new DatePicker();
    private TextField nameField = new TextField();
    private TextField departmentField = new TextField();

    List<Map<String, Object>> events = (List<Map<String, Object>>) request("/getAllEventsExceptLessons", null).getData();
    public HonorManagementPage() {
        eventView.calendar.setReadOnly(true);
        eventView.setEvents(events);
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
        m.put("awardDate",awardDatePicker.getValue()+"");
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
                new Label("获奖事件"),
                displayButton,
                new Label("荣誉名称"),
                new Label("颁奖部门"),
                new Label("颁奖时间")

        );
        gridPane.addColumn(1,
                eventView,
                studentListView,
                nameField,
                departmentField,
                awardDatePicker
        );
        gridPane.getColumnConstraints().addAll(
                new ColumnConstraints(130),
                new ColumnConstraints(500)
        );
        //gridPane.getColumnConstraints().get(1).setHgrow(Priority.ALWAYS);
        buttonBox.setSpacing(20);
        buttonBox.getChildren().addAll(addButton,deleteButton,updateButton);

        addButton.setOnAction(event -> addHonor());
        deleteButton.setOnAction(event ->deleteHonors());
        updateButton.setOnAction(event -> updateHonor());
        displayButton.setOnMouseClicked(e->
        {
            if(eventView.getSelectedEvents().isEmpty())
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("请选择获奖事件");
                alert.showAndWait();
                return;
            }
            for (Iterator<Node> it = gridPane.getChildren().iterator(); it.hasNext(); ) {
                Node node = it.next();
                if(GridPane.getRowIndex(node) == 1&&GridPane.getColumnIndex(node)==1) {
                    it.remove();
                }
            }
            studentListView=new SearchableListView(FXCollections.observableArrayList((ArrayList) request("/getStudentsByEvent",eventView.getSelectedEvents().get(0)).getData()), List.of("name","studentId"));
            gridPane.add(studentListView,1,1);
        });
        honorTable.setOnItemClick(h->{
            if(honorTable.getSelectedIndex()==0)
            {
                addButton.setDisable(false);
                studentTable.setVisible(false);
                nameField.setText("");
                departmentField.setText("");
                awardDatePicker.setValue(null);
                eventView.setEvents(events);
                studentListView.setSelectedItems(List.of());
                return;
            }
            addButton.setDisable(true);
            nameField.setText((String) h.get("name"));
            departmentField.setText((String) h.get("department"));
            awardDatePicker.setValue(LocalDate.parse((String) h.get("awardDate")));
            //eventView.setSelectedEvent((Map<String, Object>) h.get("event"));
            System.out.println((Map<String, Object>) h.get("event"));
            displayStudents(h);
            studentTable.setVisible(true);
        });
        controlPanel.getChildren().add(gridPane);
        controlPanel.getChildren().add(buttonBox);
        studentTable.setVisible(false);
        controlPanel.getChildren().add(studentTable);
        this.getItems().add(controlPanel);
    }

    private void updateHonor() {
        if(honorTable.getSelectedItem()==null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("请选择要修改的荣誉");
            alert.showAndWait();
            return;
        }
        if(eventView.getSelectedEvents().size()>1)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("只能选择一个获奖事件");
            alert.showAndWait();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "确定要更新吗？");
        alert.setTitle("警告");
        Optional<ButtonType> result=alert.showAndWait();
        if(result.get()==ButtonType.OK)
        {
            Map  m=newMapFromFields(honorTable.getSelectedItem());
            m.put("event",eventView.getSelectedEvents().get(0));
            DataResponse r=request("/updateHonor",m);
            displayHonors();
            displayStudents(m);
            if(r.getCode()==0)
            {
                Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                alert1.setContentText("更新成功");
                alert1.showAndWait();
            }
        }

    }

    private void deleteHonors() {
        if(honorTable.getSelectedItems().size()==0)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("请选择要删除的荣誉");
            alert.showAndWait();
            return;
        }
        ArrayList<Map> honorList=new ArrayList<>(honorTable.getSelectedItems());
        Alert alert=new Alert(Alert.AlertType.CONFIRMATION, "确定要删除吗？");
        alert.setTitle("警告");
        Optional<ButtonType> result=alert.showAndWait();
        if(result.get()==ButtonType.OK)
        {
            DataResponse r=request("/deleteHonors",honorList);
            if(r.getCode()==0)
            {
                Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                alert1.setContentText("删除成功");
                alert1.showAndWait();
            }
            displayHonors();
        }
    }

    private void addHonor() {
        if(eventView.getSelectedEvents().size()>1)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("只能选择一个获奖事件");
            alert.showAndWait();
            //eventListView.setSelectedItems(List.of());
            return;
        }
        if(eventView.getSelectedEvents().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("请选择获奖事件");
            alert.showAndWait();
            return;
        }
        Map m=newMapFromFields(new HashMap());
        m.put("event",eventView.getSelectedEvents().get(0));
        if(m.get("name").equals("")||m.get("department").equals("")||m.get("awardDate").equals("")||studentListView.getSelectedItems().isEmpty()
        ||m.get("name")==null||m.get("department")==null||m.get("awardDate").equals("null"))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("请填写完整信息");
            alert.showAndWait();
            return;
        }
        DataResponse r=request("/addHonor",m);
        if(r.getCode()!=0)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(r.getMsg());
            alert.showAndWait();
            return;
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(r.getMsg());
            alert.showAndWait();
        }
        displayHonors();
        studentListView.setSelectedItems(List.of());
       // eventListView.setSelectedItems(List.of());
    }

    private void initializeTable(){
        FilteredTableColumn<Map, String> nameColumn = new FilteredTableColumn<>("荣誉名称");
        FilteredTableColumn<Map, String> awardDateColumn = new FilteredTableColumn<>("颁奖时间");
        FilteredTableColumn<Map, String> departmentColumn = new FilteredTableColumn<>("颁奖部门");
        FilteredTableColumn<Map, String> eventColumn = new FilteredTableColumn<>("获奖事件");
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        awardDateColumn.setCellValueFactory(new MapValueFactory<>("awardDate"));
        departmentColumn.setCellValueFactory(new MapValueFactory<>("department"));
        eventColumn.setCellValueFactory(data->
        {
            if(data.getValue().isEmpty()) return new SimpleStringProperty("");
            Map<String,Object> event=(Map<String,Object>) data.getValue().get("event");
            String name=(String) event.get("name");
            return new SimpleStringProperty(name);
        });
        List<FilteredTableColumn<Map, ?>> columns = new ArrayList<>();
        columns.add(nameColumn);
        columns.add(awardDateColumn);
        columns.add(departmentColumn);
        columns.add(eventColumn);
        honorTable = new SearchableTableView(observableList, List.of("name","department","awardDate"),columns);
        this.getItems().add(honorTable);
    }
    private void initializeStudentTable()
    {
        FilteredTableColumn<Map, String> nameColumn = new FilteredTableColumn<>("学生姓名");
        FilteredTableColumn<Map, String> studentIdColumn = new FilteredTableColumn<>("学号");
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        studentIdColumn.setCellValueFactory(new MapValueFactory<>("studentId"));
        studentTable.getColumns().addAll(nameColumn,studentIdColumn);
    }
    private void displayStudents(Map m) {
        studentObservableList.clear();
        ArrayList<Map> studentList=(ArrayList<Map>) request("/getStudentsByHonor",m).getData();
        studentListView.setSelectedItems(studentList);
        studentObservableList.addAll(FXCollections.observableArrayList(studentList));
        studentTable.setItems(studentObservableList);
    }
}
