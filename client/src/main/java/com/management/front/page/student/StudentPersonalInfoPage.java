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
import javafx.scene.control.SplitPane;
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
        this.getTabs().add(new HonorTab());
        this.getTabs().add(new FeeTab());
    }
}

class HonorTab extends Tab{
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
            else return new SimpleStringProperty("");
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
        TableColumn<Map,String> moneyColumn= new TableColumn<>("消费金额");
        TableColumn<Map,String> timeColumn= new TableColumn<>("消费时间");
        TableColumn<Map,String> goodsColumn= new TableColumn<>("商品");
        TableColumn<Map,String> placeColumn= new TableColumn<>("消费地点");
        moneyColumn.setCellValueFactory(new MapValueFactory<>("money"));
        timeColumn.setCellValueFactory(new MapValueFactory<>("time"));
        goodsColumn.setCellValueFactory(new MapValueFactory<>("goods"));
        placeColumn.setCellValueFactory(new MapValueFactory<>("place"));
        List<TableColumn<Map, ?>> columns = new ArrayList<>();
        columns.add(moneyColumn);
        columns.add(timeColumn);
        columns.add(goodsColumn);
        columns.add(placeColumn);
        feeTable = new SearchableTableView(observableList, List.of("time","goods","place"), columns);
        splitPane.getItems().add(feeTable);
    }
}

