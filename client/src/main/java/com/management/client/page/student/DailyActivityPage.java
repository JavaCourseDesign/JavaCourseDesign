package com.management.client.page.student;

import com.management.client.customComponents.SearchableTableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SplitPane;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import javafx.scene.control.cell.MapValueFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.management.client.util.HttpClientUtil.request;

public class DailyActivityPage extends SplitPane {

    private SearchableTableView dailyActivityTable;
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    public DailyActivityPage() {
        initializeTable();
        displayDailyActivities();
    }

    private void displayDailyActivities() {
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getDailyActivitiesByStudent", null).getData()));
        dailyActivityTable.setData(observableList);
    }

    private void initializeTable() {
        FilteredTableColumn<Map, String> nameColumn = new FilteredTableColumn<>("活动名称");
        FilteredTableColumn<Map, String> typeColumn = new FilteredTableColumn<>("活动类型");
        FilteredTableColumn<Map, String> timeColumn = new FilteredTableColumn<>("活动时间");
        FilteredTableColumn<Map, String> locationColumn = new FilteredTableColumn<>("活动地点");
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        typeColumn.setCellValueFactory(new MapValueFactory<>("type"));
        timeColumn.setCellValueFactory(new MapValueFactory<>("startDate"));
        locationColumn.setCellValueFactory(new MapValueFactory<>("location"));

        List<FilteredTableColumn<Map, ?>> columns = new ArrayList<>();
        columns.add(nameColumn);
        columns.add(typeColumn);
        columns.add(timeColumn);
        columns.add(locationColumn);
        dailyActivityTable = new SearchableTableView(observableList, List.of("name", "type"), columns);

        this.getItems().add(dailyActivityTable);
    }
}
