package com.management.front.page.student;

import com.management.front.customComponents.SearchableListView;
import com.management.front.customComponents.SearchableTableView;
import com.management.front.request.DataResponse;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.management.front.util.HttpClientUtil.*;

public class StudentPersonalInfoPage extends TabPane {
    public StudentPersonalInfoPage() {
        Map student = (Map) request("/getStudent", null).getData();
        //System.out.println(student);
        this.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        this.getTabs().add(new BasicInfoTab(student));
        this.getTabs().add(new InnovationTab());
        this.getTabs().add(new absenceTab());
    }
}

class InnovationTab extends Tab {
    private SearchableTableView innovationTable;
    private SplitPane anchorPane=new SplitPane();
    private VBox controlPanel = new VBox();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    public InnovationTab() {
        this.setText("创新实践信息管理");
        this.setContent(anchorPane);
        initializeTable();
        displayInnovations();
    }
    private void displayInnovations() {
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getInnovationsByStudent", null).getData()));
        innovationTable.setData(observableList);
    }
    private void initializeTable()
    {
        TableColumn<Map, String> nameColumn = new TableColumn<>("项目名称");
        TableColumn<Map, String> typeColumn = new TableColumn<>("项目类型");
        TableColumn<Map, String> timeColumn = new TableColumn<>("时间");
        TableColumn<Map, String> locationColumn = new TableColumn<>("地点");
        TableColumn<Map, String> performanceColumn = new TableColumn<>("评价");
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        typeColumn.setCellValueFactory(new MapValueFactory<>("type"));
        timeColumn.setCellValueFactory(new MapValueFactory<>("time"));
        locationColumn.setCellValueFactory(new MapValueFactory<>("location"));
        performanceColumn.setCellValueFactory(new MapValueFactory<>("performance"));


        List<TableColumn<Map, ?>> columns = new ArrayList<>();
        columns.add(nameColumn);
        columns.add(typeColumn);
        columns.add(timeColumn);
        columns.add(locationColumn);
        columns.add(performanceColumn);
        innovationTable = new SearchableTableView(observableList, List.of("name","type"), columns);
        anchorPane.getItems().add(innovationTable);

        /*innovationTable.setOnItemClick(item -> {
            //System.out.println("Selected item: " + item);
        });*/
    }
}
class absenceTab extends Tab {

    private SearchableTableView absenceTable;
    private SplitPane splitPane=new SplitPane();
    private  VBox controlPanel=new VBox();
    private ObservableList<Map> observableList= FXCollections.observableArrayList();


    private TextField offReasonField=new TextField("玩原神");
    private TextField destinationField=new TextField("宿舍");
    private SearchableListView eventListView;
    public absenceTab() {
        eventListView=new SearchableListView(FXCollections.observableArrayList((ArrayList) request("/getEventsByStudent",null).getData()),List.of("name","time"));
        this.setText("请假信息管理");
        this.setContent(splitPane);
        initializeTable();
        initializeControlPanel();
        displayAbsences();
    }
    private Map newMapFromFields(Map m) {
        m.put("offReason",offReasonField.getText());
        m.put("destination",destinationField.getText());
        m.put("events",eventListView.getSelectedItems());
        return m;
    }
    private void displayAbsences() {
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getAbsencesByStudent", null).getData()));
        absenceTable.setData(observableList);
        System.out.println(observableList);
    }

    private void initializeControlPanel() {
        controlPanel.setMinWidth(200);
        controlPanel.setSpacing(10);
        controlPanel.setAlignment(Pos.CENTER);
        Text text = new Text("请假信息表单");
        controlPanel.getChildren().add(text);
        controlPanel.getChildren().add(new Text("请假原因:"));
        controlPanel.getChildren().add(offReasonField);
        controlPanel.getChildren().add(new Text("请假去向:"));
        controlPanel.getChildren().add(destinationField);
        controlPanel.getChildren().add(new Text("请假事件:"));
        controlPanel.getChildren().add(eventListView);
        Button uploadButton = new Button("提交");
        uploadButton.setOnMouseClicked(e->{
            uploadAbsence();
        });
        Button deleteButton=new Button("删除");
        deleteButton.setPrefWidth(100);
        deleteButton.setPrefHeight(100);
        deleteButton.setOnMouseClicked(e->
        {

        });
        controlPanel.getChildren().addAll(uploadButton,deleteButton);
        splitPane.getItems().add(controlPanel);
    }

    private void initializeTable() {
        TableColumn<Map,String> offReasonColumn= new TableColumn<>("请假原因");
        TableColumn<Map,String> eventColumn= new TableColumn<>("请假事件");
        TableColumn<Map,String> timeColumn= new TableColumn<>("请假时间");
        TableColumn<Map,String> destinationColumn= new TableColumn<>("请假去向");
        TableColumn<Map,String> statusColumn= new TableColumn<>("状态");
        offReasonColumn.setCellValueFactory(new MapValueFactory<>("offReason"));
        eventColumn.setCellValueFactory(data->{
            if(data.getValue()!=null)
            {
                Map<String,Object> event=(Map<String,Object>) data.getValue().get("event");
                String name=(String) event.get("name");
                return new SimpleStringProperty(name);
            }
            else return new SimpleStringProperty("");
        });
        timeColumn.setCellValueFactory(data->
        {
            if(data.getValue()!=null) {
                Map<String, Object> person = (Map<String, Object>) data.getValue().get("event");
                String time = (String) person.get("time");
                return new SimpleStringProperty(time);
            }
            else return new SimpleStringProperty("");
        });
        destinationColumn.setCellValueFactory(new MapValueFactory<>("destination"));
        statusColumn.setCellValueFactory(data->{
            if(data.getValue()==null)
                return new SimpleStringProperty("");
            Boolean status=(Boolean) data.getValue().get("isApproved");
            if(status==null)
                return new SimpleStringProperty("未审批");
            else if(status)
                return new SimpleStringProperty("已通过");
            else
                return new SimpleStringProperty("未通过");
        });
        List<TableColumn<Map, ?>> columns = new ArrayList<>();
        columns.add(offReasonColumn);
        columns.add(eventColumn);
        columns.add(timeColumn);
        columns.add(destinationColumn);
        columns.add(statusColumn);
        absenceTable = new SearchableTableView(observableList,List.of("isApproved"),columns);
        splitPane.getItems().add(absenceTable);
    }
    private void uploadAbsence()
    {
        Map m1 = newMapFromFields(new HashMap());
        List<Map> list=(List<Map>)m1.get("events");
        if(list.isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("请填写完整信息");
            alert.showAndWait();
            return;
        }
        if(list.size()>1)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("只能选择一个事件");
            alert.showAndWait();
            eventListView.setSelectedItems(List.of());
            return;
        }
        ArrayList<Map> eventList=(ArrayList<Map>) list;
        m1.put("eventList",eventList);
        m1.remove("events");
        DataResponse r = request("/uploadAbsence", m1);
        if(r.getCode()!=0)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(r.getMsg());
            alert.showAndWait();
            return;
        }
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("上传成功");
        displayAbsences();
        eventListView.setSelectedItems(List.of());
    }

}
class HonorTab extends Tab {
    private SearchableTableView honorTable;
    private SplitPane anchorPane=new SplitPane();
    private VBox controlPanel = new VBox();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    Map m = new HashMap();
    public HonorTab(Map student) {
        m=student;
        this.setText("创新实践信息管理");
        this.setContent(anchorPane);
        initializeTable();
        displayHonors();
    }
    private void displayHonors() {
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getHonorsByStudent", m).getData()));
        honorTable.setData(observableList);
    }
    private void initializeTable()
    {
        TableColumn<Map, String> nameColumn = new TableColumn<>("项目名称");
        TableColumn<Map, String> typeColumn = new TableColumn<>("项目类型");
        TableColumn<Map, String> timeColumn = new TableColumn<>("时间");
        TableColumn<Map, String> locationColumn = new TableColumn<>("地点");
        TableColumn<Map, String> performanceColumn = new TableColumn<>("评价");
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        typeColumn.setCellValueFactory(new MapValueFactory<>("type"));
        timeColumn.setCellValueFactory(new MapValueFactory<>("time"));
        locationColumn.setCellValueFactory(new MapValueFactory<>("location"));
        performanceColumn.setCellValueFactory(new MapValueFactory<>("performance"));


        List<TableColumn<Map, ?>> columns = new ArrayList<>();
        columns.add(nameColumn);
        columns.add(typeColumn);
        columns.add(timeColumn);
        columns.add(locationColumn);
        columns.add(performanceColumn);
        honorTable = new SearchableTableView(observableList, List.of("name","type"), columns);
        anchorPane.getItems().add(honorTable);

        /*innovationTable.setOnItemClick(item -> {
            //System.out.println("Selected item: " + item);
        });*/
    }
}

