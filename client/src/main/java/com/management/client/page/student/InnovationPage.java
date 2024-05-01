package com.management.client.page.student;

import com.management.client.customComponents.SearchableTableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.MapValueFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.management.client.util.HttpClientUtil.request;

public class InnovationPage extends SplitPane {
    private SearchableTableView innovationTable;
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    public InnovationPage() {
        initializeTable();
        displayDailyActivities();
    }

    private void displayDailyActivities() {
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getInnovationsByStudent", null).getData()));
        innovationTable.setData(observableList);
    }

    private void initializeTable() {
        TableColumn<Map, String> nameColumn = new TableColumn<>("项目名称");
        TableColumn<Map, String> typeColumn = new TableColumn<>("项目类型");
        TableColumn<Map, String> timeColumn = new TableColumn<>("时间");
        TableColumn<Map, String> locationColumn = new TableColumn<>("地点");
        TableColumn<Map, String> performanceColumn = new TableColumn<>("评价");
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        typeColumn.setCellValueFactory(new MapValueFactory<>("type"));
        timeColumn.setCellValueFactory(new MapValueFactory<>("startDate"));
        locationColumn.setCellValueFactory(new MapValueFactory<>("location"));
        performanceColumn.setCellValueFactory(new MapValueFactory<>("performance"));


        List<TableColumn<Map, ?>> columns = new ArrayList<>();
        columns.add(nameColumn);
        columns.add(typeColumn);
        columns.add(timeColumn);
        columns.add(locationColumn);
        columns.add(performanceColumn);
        innovationTable = new SearchableTableView(observableList, List.of("name", "type"), columns);
        this.getItems().add(innovationTable);

        /*innovationTable.setOnItemClick(item -> {
            //System.out.println("Selected item: " + item);
        });*/
    }
}
