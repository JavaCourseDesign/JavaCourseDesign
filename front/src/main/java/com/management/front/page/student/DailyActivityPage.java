package com.management.front.page.student;

import com.management.front.customComponents.SearchableTableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.management.front.util.HttpClientUtil.request;

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
        TableColumn<Map, String> nameColumn = new TableColumn<>("活动名称");
        TableColumn<Map, String> typeColumn = new TableColumn<>("活动类型");
        TableColumn<Map, String> timeColumn = new TableColumn<>("活动时间");
        TableColumn<Map, String> locationColumn = new TableColumn<>("活动地点");
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        typeColumn.setCellValueFactory(new MapValueFactory<>("type"));
        timeColumn.setCellValueFactory(new MapValueFactory<>("startDate"));
        locationColumn.setCellValueFactory(new MapValueFactory<>("location"));

        List<TableColumn<Map, ?>> columns = new ArrayList<>();
        columns.add(nameColumn);
        columns.add(typeColumn);
        columns.add(timeColumn);
        columns.add(locationColumn);
        dailyActivityTable = new SearchableTableView(observableList, List.of("name", "type"), columns);

        this.getItems().add(dailyActivityTable);
    }
}
