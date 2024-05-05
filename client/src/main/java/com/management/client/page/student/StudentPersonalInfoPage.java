package com.management.client.page.student;

import com.management.client.customComponents.SearchableTableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import javafx.scene.control.cell.MapValueFactory;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.management.client.util.HttpClientUtil.*;

public class StudentPersonalInfoPage extends TabPane {
    public StudentPersonalInfoPage() {
        Map student = (Map) request("/getStudent", null).getData();
        //System.out.println(student);
        this.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        this.getTabs().add(new StudentBasicInfoTab(student));
        this.getTabs().add(new HonorTab());
        this.getTabs().add(new FeeTab());
    }
}

class HonorTab extends Tab {
    private SearchableTableView honorTable;
    private SplitPane splitPane=new SplitPane();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    public HonorTab() {
        this.setText("荣誉信息");
        this.setContent(splitPane);
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
        FilteredTableColumn<Map, String> nameColumn = new FilteredTableColumn<>("荣誉名称");
        FilteredTableColumn<Map, String> timeColumn = new FilteredTableColumn<>("获得时间");
        FilteredTableColumn<Map, String> departmentColumn = new FilteredTableColumn<>("颁奖部门");
        FilteredTableColumn<Map, String> eventColumn = new FilteredTableColumn<>("获奖事件");
        eventColumn.setCellValueFactory(data->{
            if(data.getValue()!=null)
            {
                Map<String,Object> event=(Map<String,Object>) data.getValue().get("event");
                String name=(String) event.get("name");
                return new SimpleStringProperty(name);
            }
            else return new SimpleStringProperty("");
        });
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        timeColumn.setCellValueFactory(new MapValueFactory<>("awardDate"));
        departmentColumn.setCellValueFactory(new MapValueFactory<>("department"));

        List<FilteredTableColumn<Map, ?>> columns = new ArrayList<>();
        columns.add(nameColumn);
        columns.add(timeColumn);
        columns.add(departmentColumn);
        columns.add(eventColumn);
        honorTable = new SearchableTableView(observableList, List.of("name","awardDate"), columns);
        splitPane.getItems().add(honorTable);
    }
}
class FeeTab extends Tab{
    private SearchableTableView feeTable;
    private SplitPane splitPane=new SplitPane();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    public FeeTab() {
        this.setText("消费信息");
        this.setContent(splitPane);
        initializeTable();
        displayFees();
    }

    private void displayFees() {
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getFeesByStudent", null).getData()));
        feeTable.setData(observableList);
    }

    private void initializeTable() {
        FilteredTableColumn<Map,String> moneyColumn= new FilteredTableColumn<>("消费金额");
        FilteredTableColumn<Map,String> timeColumn= new FilteredTableColumn<>("消费时间");
        FilteredTableColumn<Map,String> goodsColumn= new FilteredTableColumn<>("商品");
        FilteredTableColumn<Map,String> placeColumn= new FilteredTableColumn<>("消费地点");
        moneyColumn.setCellValueFactory(new MapValueFactory<>("money"));
        timeColumn.setCellValueFactory(new MapValueFactory<>("time"));
        goodsColumn.setCellValueFactory(new MapValueFactory<>("goods"));
        placeColumn.setCellValueFactory(new MapValueFactory<>("place"));
        List<FilteredTableColumn<Map, ?>> columns = new ArrayList<>();
        columns.add(moneyColumn);
        columns.add(timeColumn);
        columns.add(goodsColumn);
        columns.add(placeColumn);
        feeTable = new SearchableTableView(observableList, List.of("time","goods","place"), columns);
        splitPane.getItems().add(feeTable);
    }
}

