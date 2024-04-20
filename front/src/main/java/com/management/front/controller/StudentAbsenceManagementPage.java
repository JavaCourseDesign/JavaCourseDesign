package com.management.front.controller;


import com.management.front.customComponents.SearchableListView;
import com.management.front.customComponents.SearchableTableView;
import com.management.front.request.DataResponse;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.management.front.util.HttpClientUtil.request;

public class StudentAbsenceManagementPage extends SplitPane {
    private SearchableTableView absenceTable;
    private VBox controlPanel = new VBox();
    private ObservableList<Map> observableList= FXCollections.observableArrayList();
    private SearchableListView eventListView=new SearchableListView(FXCollections.observableArrayList((ArrayList) request("/getAllEvents",null).getData()),List.of("name","time"));
    private ComboBox typeField = new ComboBox(FXCollections.observableArrayList(
            "学生", "教师"
    ));
    private TextField idField = new TextField("201921000");
    private TextField nameField = new TextField("wzk");

    public StudentAbsenceManagementPage() {
            this.setWidth(1000);
            initializeTable();
            initializeControlPanel();
            displayAbsences();
    }
    private Map newMapFromFields(Map m) {
        m.put("id", idField.getText());
        m.put("type", typeField.getValue());
        m.put("name", nameField.getText());
        m.put("events",eventListView.getSelectedItems());
        return m;
    }
    private void displayAbsences() {
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getAllAbsences", null).getData()));
        absenceTable.setData(observableList);
        System.out.println(observableList);
    }

    private void initializeControlPanel() {
        controlPanel.setMinWidth(200);
        controlPanel.setSpacing(10);
        controlPanel.setAlignment(Pos.CENTER);
        Text text = new Text("临时添加缺勤信息");
        controlPanel.getChildren().add(text);
        controlPanel.getChildren().add(new Text("学号工号:"));
        controlPanel.getChildren().add(idField);
        controlPanel.getChildren().add(new Text("身份:"));
        controlPanel.getChildren().add(typeField);
        controlPanel.getChildren().add(new Text("姓名:"));
        controlPanel.getChildren().add(nameField);
        controlPanel.getChildren().add(new Text("请假事件:"));
        controlPanel.getChildren().add(eventListView);
        Button addButton = new Button("添加");
        addButton.setOnMouseClicked(e->{
            addAbsence();
        });
        HBox hBox=new HBox();
        hBox.setAlignment(Pos.CENTER);
        Button approveButton=new Button("通过");
        approveButton.setOnMouseClicked(e->{
            List<Map> selectedItems=absenceTable.getSelectedItems();
            if(selectedItems.isEmpty())
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("请选择要审批的请假");
                alert.showAndWait();
                return;
            }
            for(int i=0;i<selectedItems.size();i++)
            {
                Map map=selectedItems.get(i);
                map.put("isApproved",true);
                request("/updateAbsence",map);
            }
            displayAbsences();
        });

        Button rejectButton=new Button("不通过");
        rejectButton.setOnMouseClicked(e->{
            List<Map> selectedItems=absenceTable.getSelectedItems();
            if(selectedItems.isEmpty())
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("请选择要审批的请假");
                alert.showAndWait();
                return;
            }
            for(int i=0;i<selectedItems.size();i++)
            {
                Map map=selectedItems.get(i);
                map.put("isApproved",false);
                request("/updateAbsence",map);
            }
            displayAbsences();
        });
        hBox.getChildren().add(approveButton);
        hBox.getChildren().add(rejectButton);
        Button deleteButton=new Button("删除");
        deleteButton.setPrefWidth(100);
        deleteButton.setPrefHeight(100);
        deleteButton.setOnMouseClicked(e->
        {

        });
        controlPanel.getChildren().addAll(addButton,hBox,deleteButton);
        this.getItems().add(controlPanel);
    }

    private void initializeTable() {
        TableColumn<Map,String> personColumn= new TableColumn<>("学生/教师");
        TableColumn<Map,String> offReasonColumn= new TableColumn<>("请假原因");
        TableColumn<Map,String> timeColumn= new TableColumn<>("请假时间");
        TableColumn<Map,String> destinationColumn= new TableColumn<>("请假去向");
        TableColumn<Map,String> statusColumn= new TableColumn<>("状态");
        personColumn.setCellValueFactory(data->{
            if(data.getValue()!=null)
            {
                Map<String,Object> person=(Map<String,Object>) data.getValue().get("person");
                String name=(String) person.get("name");
                return new SimpleStringProperty(name);
            }
            else return new SimpleStringProperty("");
        });
        offReasonColumn.setCellValueFactory(new MapValueFactory<>("offReason"));
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
        columns.add(personColumn);
        columns.add(offReasonColumn);
        columns.add(timeColumn);
        columns.add(destinationColumn);
        columns.add(statusColumn);
        absenceTable = new SearchableTableView(observableList,List.of("name","isApproved"),columns);
        this.getItems().add(absenceTable);
    }
    private void addAbsence()
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
        for(int i=0;i<list.size();i++)
        {
            Map map=newMapFromFields(new HashMap());
            if(map.get("type")==null||map.get("name")==null||map.get("id")==null)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("请填写完整信息");
                alert.showAndWait();
                return;
            }
            map.remove("events");
            map.put("eventId",list.get(i).get("eventId"));
            DataResponse r=request("/addAbsence", map);
            if(r.getCode()==1) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(r.getMsg());
                alert.showAndWait();
                return;
            }
        }
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("添加成功");
        displayAbsences();
        eventListView.setSelectedItems(List.of());
    }

}
