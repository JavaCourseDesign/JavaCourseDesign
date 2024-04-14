package com.management.front.controller;


import com.management.front.customComponents.SearchableTableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.GridPane;
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
    private ObservableList<Map> observableList = FXCollections.observableArrayList();
    private ComboBox typeField = new ComboBox(FXCollections.observableArrayList(
            "学生", "教师"
    ));
    private TextField idField = new TextField();
    private TextField nameField = new TextField();
    private TextField timeField = new TextField();

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
        m.put("time", timeField.getText());
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
        controlPanel.getChildren().add(new Text("请假时间:"));
        controlPanel.getChildren().add(timeField);
        Button addButton = new Button("添加");
        addButton.setOnMouseClicked(e->{
            addAbsence();
        });
        Button approveButton = new Button("审批");
        approveButton.setPrefWidth(100);
        approveButton.setPrefHeight(100);
        approveButton.setOnMouseClicked(e->
        {
            Stage s=new Stage();
            GridPane gridPane=new GridPane();

            Scene scene=new Scene(gridPane);
            s.setScene(scene);
            s.show();
        });
        Button deleteButton=new Button("删除");
        deleteButton.setPrefWidth(100);
        deleteButton.setPrefHeight(100);
        deleteButton.setOnMouseClicked(e->
        {


        });
        controlPanel.getChildren().addAll(addButton,approveButton,deleteButton);
        this.getItems().add(controlPanel);
    }

    private void initializeTable() {
        TableColumn<Map,String> personColumn= new TableColumn<>("学生/教师");
        TableColumn<Map,String> offReasonColumn= new TableColumn<>("请假原因");
        TableColumn<Map,String> timeColumn= new TableColumn<>("请假时间");
        TableColumn<Map,String> destinationColumn= new TableColumn<>("地点");
        TableColumn<Map,String> statusColumn= new TableColumn<>("状态");
        personColumn.setCellValueFactory(data->{
            Map<String,Object> person=(Map<String,Object>) data.getValue().get("person");
            String name=(String) person.get("name");
            return new SimpleStringProperty(name);
        });
        offReasonColumn.setCellValueFactory(new MapValueFactory<>("offReason"));
        timeColumn.setCellValueFactory(new MapValueFactory<>("time"));
        destinationColumn.setCellValueFactory(new MapValueFactory<>("destination"));
        statusColumn.setCellValueFactory(data->{
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
        Map m = newMapFromFields(new HashMap());
        request("/addAbsence", m);
        displayAbsences();
    }

}
