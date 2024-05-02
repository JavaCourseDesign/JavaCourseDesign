package com.management.client.page.admin;

import com.management.client.customComponents.SearchableListView;
import com.management.client.customComponents.SearchableTableView;
import com.management.client.customComponents.WeekTimeTable;
import com.management.client.request.DataResponse;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.*;

import static com.management.client.util.HttpClientUtil.importData;
import static com.management.client.util.HttpClientUtil.request;

public class StudentLogManagementPage extends TabPane {
    public StudentLogManagementPage() {
        Tab studentAbsenceManagementTab = new StudentAbsenceManagementTab();
        studentAbsenceManagementTab.setClosable(false); // 设置为不可关闭
        this.getTabs().add(studentAbsenceManagementTab);

        Tab studentFeeManagementTab = new StudentFeeManagementTab();
        studentFeeManagementTab.setClosable(false); // 设置为不可关闭
        this.getTabs().add(studentFeeManagementTab);
    }
}
class StudentAbsenceManagementTab extends Tab {
    private SplitPane splitPane=new SplitPane();

    private SearchableTableView absenceTable;
    private VBox controlPanel = new VBox();
    private SearchableListView studentListView=new SearchableListView(FXCollections.observableArrayList((ArrayList) request("/getAllStudents",null).getData()), List.of("name","studentId"));
    private ObservableList<Map> observableList= FXCollections.observableArrayList();
    private WeekTimeTable eventView=new WeekTimeTable();
///getAllEventsExceptLessons

    public StudentAbsenceManagementTab() {
        this.setText("学生请假信息管理");
        splitPane.setMinWidth(1000);
        this.setContent(splitPane);
        eventView.setEvents((List<Map<String, Object>>) request("/getAllEventsExceptLessons", null).getData());
        initializeTable();
        initializeControlPanel();
        displayAbsences();
    }
    private Map newMapFromFields(Map m) {
        m.put("students",studentListView.getSelectedItems());
        m.put("events", eventView.getSelectedEvents());
        return m;
    }
    private void displayAbsences() {
        observableList.clear();
        ArrayList<Map> list=(ArrayList<Map>) request("/getAllStudentAbsences", null).getData();
        System.out.println(list);
        for(Map m:list)
        {
            if(m.get("person")!=null)
            {
                Map person=(Map) m.get("person");
                m.put("studentId",person.get("studentId"));
                m.put("studentName",person.get("name"));
            }
            if(m.get("event")!=null)
            {
                Map event=(Map) m.get("event");
                m.put("eventName",event.get("name"));
            }
        }
        observableList.addAll(FXCollections.observableArrayList(list));
        absenceTable.setData(observableList);
        //System.out.println(observableList);
    }

    private void initializeControlPanel() {
        controlPanel.setMinWidth(200);
        controlPanel.setSpacing(10);
        controlPanel.setAlignment(Pos.CENTER);
        Label text = new Label("添加学生重要事件缺勤信息");
        controlPanel.getChildren().add(text);
        controlPanel.getChildren().add(new Label("学生:"));
        controlPanel.getChildren().add(studentListView);
        controlPanel.getChildren().add(new Label("请假事件:"));
        controlPanel.getChildren().add(eventView);
        Button addButton = new Button("添加");
        addButton.setOnMouseClicked(e->{
            addAbsence();
        });
        HBox hBox=new HBox();
        hBox.setAlignment(Pos.CENTER);
        Button approveButton=new Button("通过");
        approveButton.setOnMouseClicked(e->{
            List<Map> selectedItems=absenceTable.getSelectedItems();
            if(selectedItems.isEmpty())
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("请选择要审批的请假");
                alert.showAndWait();
                return;
            }
            for(int i=0;i<selectedItems.size();i++)
            {
                Map map=selectedItems.get(i);
                map.put("isApproved",true);
                request("/updateAbsence",map);
            }
            displayAbsences();
        });
        Button rejectButton=new Button("不通过");
        rejectButton.setOnMouseClicked(e->{
            List<Map> selectedItems=absenceTable.getSelectedItems();
            if(selectedItems.isEmpty())
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("请选择要审批的请假");
                alert.showAndWait();
                return;
            }
            for(int i=0;i<selectedItems.size();i++)
            {
                Map map=selectedItems.get(i);
                map.put("isApproved",false);
                request("/updateAbsence",map);
            }
            displayAbsences();
        });
        hBox.getChildren().add(approveButton);
        hBox.getChildren().add(rejectButton);
        Button deleteButton=new Button("删除");
        deleteButton.setPrefWidth(100);
        deleteButton.setPrefHeight(100);
        deleteButton.setOnMouseClicked(e->
        {
            deleteAbsence();
        });
        controlPanel.getChildren().addAll(addButton,hBox,deleteButton);
        splitPane.getItems().add(controlPanel);
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

    private void initializeTable() {
        TableColumn<Map,String> studentColumn= new TableColumn<>("学生");
        TableColumn<Map,String> studentIdColumn= new TableColumn<>("学生学号");
        TableColumn<Map,String> eventColumn= new TableColumn<>("请假事件");
        TableColumn<Map,String> offReasonColumn= new TableColumn<>("请假原因");
        TableColumn<Map,String> timeColumn= new TableColumn<>("请假时间");
        TableColumn<Map,String> destinationColumn= new TableColumn<>("请假去向");
        TableColumn<Map,String> statusColumn= new TableColumn<>("状态");
        studentColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        studentIdColumn.setCellValueFactory(new MapValueFactory<>("studentId"));
        offReasonColumn.setCellValueFactory(new MapValueFactory<>("offReason"));
        timeColumn.setCellValueFactory(data ->
        {
            if (data.getValue() != null) {
                Map<String, Object> event = (Map<String, Object>) data.getValue().get("event");
                String time = event.get("startDate") + " " + event.get("startTime") + "-" + event.get("endDate") + " " + event.get("endTime");
                return new SimpleStringProperty(time);
            } else return new SimpleStringProperty("");
        });
        destinationColumn.setCellValueFactory(new MapValueFactory<>("destination"));
        eventColumn.setCellValueFactory(new MapValueFactory<>("eventName"));
        statusColumn.setCellValueFactory(data->{
            if(data.getValue()==null)
                return new SimpleStringProperty("");
            Boolean status=(Boolean) data.getValue().get("isApproved");
            if(status==null)
                return new SimpleStringProperty("未审批");
            else if(status)
                return new SimpleStringProperty("已通过");
            else
                return new SimpleStringProperty("未通过");
        });
        List<TableColumn<Map, ?>> columns = new ArrayList<>();
        columns.add(studentColumn);
        columns.add(studentIdColumn);
        columns.add(eventColumn);
        columns.add(offReasonColumn);
        columns.add(timeColumn);
        columns.add(destinationColumn);
        columns.add(statusColumn);
        absenceTable = new SearchableTableView(observableList,List.of("studentName","studentId","time","eventName","isApproved"),columns);
        splitPane.getItems().add(absenceTable);
    }
    private void addAbsence()
    {
        Map m1 = newMapFromFields(new HashMap());
        ArrayList<Map> eventList=(ArrayList<Map>)m1.get("events");
        ArrayList<Map> studentList=(ArrayList<Map>)m1.get("students");
        if(eventList.isEmpty() || studentList.isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("请填写完整信息");
            alert.showAndWait();
            return;
        }
        if(eventList.size()>1)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("只能选择一个事件");
            alert.showAndWait();
            studentListView.setSelectedItems(List.of());

            return;
        }
        //ListMap不能直接传，要传arraylist
        Map m2=new HashMap();
        m2.put("eventId",eventList.get(0).get("eventId"));
        m2.put("studentList",studentList);
        DataResponse r=request("/addStudentAbsence",m2);
        if(r.getCode()!=0)
        {
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setContentText(r.getMsg());
            alert.showAndWait();
        }
        else
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("添加成功");
            alert.showAndWait();
        }
        displayAbsences();
        studentListView.setSelectedItems(List.of());

    }
}
class StudentFeeManagementTab extends Tab{
    private VBox vBox=new VBox();
    private Button uploadFeeButton=new Button("上传消费信息");
    private ObservableList<Map> observableList= FXCollections.observableArrayList();
    private SearchableTableView feeTable;
    public StudentFeeManagementTab() {
        this.setText("学生消费信息管理");
        this.setContent(vBox);
        vBox.getChildren().add(uploadFeeButton);
        initializeTable();
        uploadFeeButton.setPrefHeight(100);
        uploadFeeButton.setPrefWidth(100);
        uploadFeeButton.setOnMouseClicked(e->{
            uploadFee();
        });
        displayFees();
    }

    private void uploadFee() {

        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("前选择消费数据表");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
        fileDialog.getExtensionFilters().add(extFilter);

        File file = fileDialog.showOpenDialog(null);
        if(file==null)
        {
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setContentText("未选择文件");
            return;
        }
       DataResponse r= importData("/importFeeData",file.getPath());
        if (r.getCode() == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("上传成功");
            alert.showAndWait();
            displayFees();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(r.getMsg());
            alert.showAndWait();
        }
    }

    private void displayFees()
    {
        observableList.clear();
        ArrayList<Map> list=(ArrayList<Map>) request("/getAllFees", null).getData();
        System.out.println(list);
        for(Map m:list)
        {
            if(m.get("person")!=null)
            {
                Map person=(Map) m.get("person");
                m.put("name",person.get("name"));
                m.put("studentId",person.get("studentId"));
            }
        }
        observableList.addAll(FXCollections.observableArrayList(list));
        feeTable.setData(observableList);
    }
    private void initializeTable() {
        TableColumn<Map,String> studentNameColumn= new TableColumn<>("学生姓名");
        TableColumn<Map,String> studentIdColumn= new TableColumn<>("学生学号");
        TableColumn<Map,String> moneyColumn= new TableColumn<>("消费金额");
        TableColumn<Map,String> timeColumn= new TableColumn<>("消费时间");
        TableColumn<Map,String> goodsColumn= new TableColumn<>("商品");
        TableColumn<Map,String> placeColumn= new TableColumn<>("消费地点");

        studentNameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        studentIdColumn.setCellValueFactory(new MapValueFactory<>("studentId"));
        moneyColumn.setCellValueFactory(new MapValueFactory<>("money"));
        timeColumn.setCellValueFactory(new MapValueFactory<>("time"));
        goodsColumn.setCellValueFactory(new MapValueFactory<>("goods"));
        placeColumn.setCellValueFactory(new MapValueFactory<>("place"));
        List<TableColumn<Map, ?>> columns = new ArrayList<>();
        columns.add(studentNameColumn);
        columns.add(studentIdColumn);
        columns.add(moneyColumn);
        columns.add(timeColumn);
        columns.add(goodsColumn);
        columns.add(placeColumn);
        feeTable = new SearchableTableView(observableList,List.of("name","studentId","time","goods","place"),columns);
        vBox.getChildren().add(feeTable);
        VBox.setVgrow(feeTable, Priority.ALWAYS);
    }
}
