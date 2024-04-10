package com.management.front.controller;

import com.management.front.customComponents.SearchableTableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Map;

import static com.management.front.util.HttpClientUtil.request;

public class InnovationManagementPage extends SplitPane {
    private SearchableTableView innovationTable;
    private VBox controlPanel = new VBox();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    private Button addButton = new Button("Add");
    private Button deleteButton = new Button("Delete");
    private Button updateButton = new Button("Update");

    public InnovationManagementPage() {
        this.setWidth(1000);
        initializeTable();
        initializeControlPanel();
        displayInnovations();
    }

    private void displayInnovations() {
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getAllInnovations", null).getData()));
        innovationTable.setData(observableList);
    }

    private void initializeControlPanel() {

    }

    private void initializeTable()
    {
        TableColumn<Map, String> studentNameColumn = new TableColumn<>("学生姓名");
        TableColumn<Map, String> nameColumn = new TableColumn<>("项目名称");
        TableColumn<Map, String> typeColumn = new TableColumn<>("项目类型");
        TableColumn<Map, String> timeColumn = new TableColumn<>("时间");
        TableColumn<Map, String> locationColumn = new TableColumn<>("地点");
        TableColumn<Map, String> performanceColumn = new TableColumn<>("评价");
    }

}
