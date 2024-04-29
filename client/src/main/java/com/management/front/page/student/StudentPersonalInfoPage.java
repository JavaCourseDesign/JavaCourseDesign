package com.management.front.page.student;

import com.management.front.customComponents.SearchableTableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.management.front.util.HttpClientUtil.*;

public class StudentPersonalInfoPage extends TabPane {
    public StudentPersonalInfoPage() {
        Map student = (Map) request("/getStudent", null).getData();
        //System.out.println(student);
        this.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        this.getTabs().add(new StudentBasicInfoTab(student));
        this.getTabs().add(new HonorTab());
    }
}

class HonorTab extends Tab{
    private SearchableTableView honorTable;
    private SplitPane anchorPane=new SplitPane();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    public HonorTab() {
        this.setText("荣誉信息管理");
        this.setContent(anchorPane);
        initializeTable();
        displayHonors();
    }
    private void displayHonors() {
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getHonorsByStudent", null).getData()));
        honorTable.setData(observableList);
    }
    private void initializeTable()
    {
        TableColumn<Map, String> nameColumn = new TableColumn<>("荣誉名称");
        TableColumn<Map, String> timeColumn = new TableColumn<>("获得时间");
        TableColumn<Map, String> departmentColumn = new TableColumn<>("颁奖部门");
        TableColumn<Map, String> eventColumn = new TableColumn<>("获奖事件");
        eventColumn.setCellValueFactory(data->{
            if(data.getValue()!=null)
            {
                Map<String,Object> event=(Map<String,Object>) data.getValue().get("event");
                String name=(String) event.get("name");
                return new SimpleStringProperty(name);
            }
            else {
                return new SimpleStringProperty("");
            }
        });
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        timeColumn.setCellValueFactory(new MapValueFactory<>("awardDate"));
        departmentColumn.setCellValueFactory(new MapValueFactory<>("department"));

        List<TableColumn<Map, ?>> columns = new ArrayList<>();
        columns.add(nameColumn);
        columns.add(timeColumn);
        columns.add(departmentColumn);
        columns.add(eventColumn);
        honorTable = new SearchableTableView(observableList, List.of("name","awardDate"), columns);
        anchorPane.getItems().add(honorTable);
    }
}

