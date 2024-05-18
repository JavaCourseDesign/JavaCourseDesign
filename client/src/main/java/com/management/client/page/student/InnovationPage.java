package com.management.client.page.student;

import com.management.client.customComponents.SearchableTableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SplitPane;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import javafx.scene.control.cell.MapValueFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.management.client.util.HttpClientUtil.request;

public class InnovationPage extends SplitPane {
    private SearchableTableView innovationTable;
    private final ObservableList<Map> observableList = FXCollections.observableArrayList();

    public InnovationPage() {
        initializeTable();
        displayDailyActivities();
    }

    private void displayDailyActivities() {
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getInnovationsByStudent", null).getData()));
        innovationTable.setData(observableList);
      //  System.out.println(observableList);
    }

    private void initializeTable() {
        FilteredTableColumn<Map, String> nameColumn = new FilteredTableColumn<>("项目名称");
        FilteredTableColumn<Map, String> typeColumn = new FilteredTableColumn<>("项目类型");
        FilteredTableColumn<Map, String> timeColumn = new FilteredTableColumn<>("时间");
        FilteredTableColumn<Map, String> locationColumn = new FilteredTableColumn<>("地点");
        FilteredTableColumn<Map, String> performanceColumn = new FilteredTableColumn<>("评价");
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
        performanceColumn.setCellValueFactory(new MapValueFactory<>("performance"));


        List<FilteredTableColumn<Map, ?>> columns = new ArrayList<>();
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
