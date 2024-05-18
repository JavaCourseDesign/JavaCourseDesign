package com.management.client.page.student;

import com.management.client.customComponents.SearchableTableView;
import com.management.client.customComponents.WeekTimeTable;
import com.management.client.request.DataResponse;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.controlsfx.control.tableview2.FilteredTableColumn;

import java.util.*;

import static com.management.client.util.HttpClientUtil.request;

public class AbsencePage extends SplitPane {

    private SearchableTableView absenceTable;
    private final VBox controlPanel = new VBox();
    private final ObservableList<Map> observableList = FXCollections.observableArrayList();

    private final TextField offReasonField = new TextField();
    private final TextField destinationField = new TextField();
    private final WeekTimeTable eventView=new WeekTimeTable();

    public AbsencePage() {
        eventView.setEvents((List<Map<String, Object>>) request("/getStudentEvents", null).getData());
        initializeTable();
        initializeControlPanel();
        displayAbsences();
    }

    private Map newMapFromFields(Map m) {
        m.put("offReason", offReasonField.getText());
        m.put("destination", destinationField.getText());
        m.put("events", eventView.getSelectedEvents());
        return m;
    }

    private void displayAbsences() {
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getAbsencesByStudent", null).getData()));
        absenceTable.setData(observableList);
        System.out.println(observableList);
    }

    private void initializeControlPanel() {
        controlPanel.setMinWidth(200);
        controlPanel.setSpacing(10);
        controlPanel.setAlignment(Pos.CENTER);
        Text text = new Text("请假信息表单");
        controlPanel.getChildren().add(text);
        controlPanel.getChildren().add(new Text("请假原因:"));
        controlPanel.getChildren().add(offReasonField);
        controlPanel.getChildren().add(new Text("请假去向:"));
        controlPanel.getChildren().add(destinationField);
        controlPanel.getChildren().add(new Text("请假事件:"));
        controlPanel.getChildren().add(eventView);
        Button uploadButton = new Button("提交");
        uploadButton.setOnMouseClicked(e -> {
            uploadAbsence();
        });
        Button deleteButton = new Button("删除");
        deleteButton.setPrefWidth(100);
        deleteButton.setPrefHeight(100);
        deleteButton.setOnMouseClicked(e ->
        {
            deleteAbsence();
        });
        controlPanel.getChildren().addAll(uploadButton, deleteButton);
        this.getItems().add(controlPanel);
    }

    private void initializeTable() {
        FilteredTableColumn<Map, String> offReasonColumn = new FilteredTableColumn<>("请假原因");
        FilteredTableColumn<Map, String> eventColumn = new FilteredTableColumn<>("请假事件");
        FilteredTableColumn<Map, String> timeColumn = new FilteredTableColumn<>("请假时间");
        FilteredTableColumn<Map, String> destinationColumn = new FilteredTableColumn<>("请假去向");
        FilteredTableColumn<Map, String> statusColumn = new FilteredTableColumn<>("状态");
        offReasonColumn.setCellValueFactory(new MapValueFactory<>("offReason"));
        eventColumn.setCellValueFactory(data -> {
            System.out.println(data.getValue());
            if (data.getValue() != null) {
                Map<String, Object> event = (Map<String, Object>) data.getValue().get("event");
                String name = (String) event.get("name");
                return new SimpleStringProperty(name);
            } else return new SimpleStringProperty("");
        });
        timeColumn.setCellValueFactory(data ->
        {
            if (data.getValue() != null) {
                Map<String, Object> event = (Map<String, Object>) data.getValue().get("event");
                String time = event.get("startDate") + " " + event.get("startTime") + "-" + event.get("endDate") + " " + event.get("endTime");
                return new SimpleStringProperty(time);
            } else return new SimpleStringProperty("");
        });
        destinationColumn.setCellValueFactory(new MapValueFactory<>("destination"));
        statusColumn.setCellValueFactory(data -> {
            if (data.getValue() == null)
                return new SimpleStringProperty("");
            Boolean status = (Boolean) data.getValue().get("isApproved");
            if (status == null)
                return new SimpleStringProperty("未审批");
            else if (status)
                return new SimpleStringProperty("已通过");
            else
                return new SimpleStringProperty("未通过");
        });
        List<FilteredTableColumn<Map, ?>> columns = new ArrayList<>();
        columns.add(offReasonColumn);
        columns.add(eventColumn);
        columns.add(timeColumn);
        columns.add(destinationColumn);
        columns.add(statusColumn);
        absenceTable = new SearchableTableView(observableList, List.of("isApproved"), columns);
        this.getItems().add(absenceTable);
    }

    private void uploadAbsence() {
        Map m1 = newMapFromFields(new HashMap());
        List<Map> list = (List<Map>) m1.get("events");
        if (list.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("请填写完整信息");
            alert.showAndWait();
            return;
        }
        if (list.size() > 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("只能选择一个事件");
            alert.showAndWait();
            //eventView.setSelectedItems(List.of());
            return;
        }
        ArrayList<Map> eventList = (ArrayList<Map>) list;
        m1.put("eventList", eventList);
        m1.remove("events");
        DataResponse r = request("/uploadAbsence", m1);
        if (r.getCode() != 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(r.getMsg());
            alert.showAndWait();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("上传成功");
        displayAbsences();
        //eventListView.setSelectedItems(List.of());
    }
    private void deleteAbsence() {
        List<Map> selectedItems=absenceTable.getSelectedItems();
        if(selectedItems.isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("请选择要删除的请假");
            alert.showAndWait();
            return;
        }
        //不能强转 javafx.scene.control.MultipleSelectionModelBase$1 cannot
        // be cast to class java.util.ArrayList不知道为什么
        ArrayList<Map> absenceList=new ArrayList<>();
        for(Map m:selectedItems)
        {
            absenceList.add(m);
        }
        Alert alert=new Alert(Alert.AlertType.CONFIRMATION, "确定要删除吗？");
        alert.setTitle("警告");
        Optional<ButtonType> result=alert.showAndWait();
        if(result.get()== ButtonType.OK)
        {
            DataResponse r=request("/deleteAbsences",absenceList);
            if(r.getCode()!=0)
            {
                Alert alert1=new Alert(Alert.AlertType.ERROR);
                alert1.setContentText(r.getMsg());
                alert1.showAndWait();
            }
            else {
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setContentText("删除成功");
                alert1.showAndWait();
            }
        }
        displayAbsences();
    }
}
