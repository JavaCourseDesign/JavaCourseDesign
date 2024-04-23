package com.management.front.page.teacher;

import com.management.front.customComponents.SearchableListView;
import com.management.front.customComponents.SearchableTableView;
import com.management.front.request.DataResponse;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.management.front.util.HttpClientUtil.request;

public class StudentAbsenceManagementTab extends Tab {

    private SplitPane splitPane=new SplitPane();
    private SearchableTableView absenceTable;
    private VBox controlPanel = new VBox();
    private SearchableListView studentListView=new SearchableListView(FXCollections.observableArrayList((ArrayList) request("/getAllStudentsByTeacherCourses",null).getData()), List.of("name","studentId"));
    private ObservableList<Map> observableList= FXCollections.observableArrayList();
    private SearchableListView lessonListView;
    Map course=new HashMap();
    public StudentAbsenceManagementTab(Map c) {
        course.put("courseId",c.get("courseId"));
        splitPane.setMinWidth(1000);
        this.setText("学生缺勤管理");
        this.setContent(splitPane);
        lessonListView=new SearchableListView(FXCollections.observableArrayList((ArrayList) request("/getLessonsByCourseId",course).getData()), List.of("name","time"));
        initializeTable();
        initializeControlPanel();
        displayAbsences();
    }
    private Map newMapFromFields(Map m) {
        m.put("students",studentListView.getSelectedItems());
        m.put("events",lessonListView.getSelectedItems());
        return m;
    }
    //待改
    private void displayAbsences() {
        observableList.clear();
        ArrayList<Map> list=(ArrayList<Map>) request("/getAllStudentLessonAbsencesByCourse", course).getData();
        System.out.println(list);
        for(Map m:list)
        {
            if(m.get("person")!=null)
            {
                Map person=(Map) m.get("person");
                m.put("studentName",person.get("name"));
                m.put("studentId",person.get("studentId"));
            }
            if(m.get("event")!=null)
            {
                Map event=(Map) m.get("event");
                m.put("time",event.get("time"));
                m.put("lessonName",event.get("name"));
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
        Label text = new Label("添加学生课程缺勤信息");
        controlPanel.getChildren().add(text);
        controlPanel.getChildren().add(new Label("学生:"));
        controlPanel.getChildren().add(studentListView);
        controlPanel.getChildren().add(new Label("请假事件:"));
        controlPanel.getChildren().add(lessonListView);
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
        DataResponse r=request("/deleteAbsences",absenceList);
        if(r.getCode()!=0)
        {
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setContentText(r.getMsg());
            alert.showAndWait();
        }
        else
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("删除成功");
            alert.showAndWait();
        }
        displayAbsences();
    }
    private void initializeTable() {
        TableColumn<Map,String> studentColumn= new TableColumn<>("学生");
        TableColumn<Map,String> studentIdColumn= new TableColumn<>("学生学号");
        TableColumn<Map,String> offReasonColumn= new TableColumn<>("请假原因");
        TableColumn<Map,String> lessonColumn= new TableColumn<>("请假课程");
        TableColumn<Map,String> timeColumn= new TableColumn<>("请假时间");
        TableColumn<Map,String> destinationColumn= new TableColumn<>("请假去向");
        TableColumn<Map,String> statusColumn= new TableColumn<>("状态");
        studentColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        studentIdColumn.setCellValueFactory(new MapValueFactory<>("studentId"));
        lessonColumn.setCellValueFactory(new MapValueFactory<>("lessonName"));
        offReasonColumn.setCellValueFactory(new MapValueFactory<>("offReason"));
        timeColumn.setCellValueFactory(new MapValueFactory<>("time"));
        destinationColumn.setCellValueFactory(new MapValueFactory<>("destination"));
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
        columns.add(lessonColumn);
        columns.add(studentIdColumn);
        columns.add(offReasonColumn);
        columns.add(timeColumn);
        columns.add(destinationColumn);
        columns.add(statusColumn);
        absenceTable = new SearchableTableView(observableList,List.of("lessonName","time","studentName","isApproved"),columns);
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
            lessonListView.setSelectedItems(List.of());
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
        lessonListView.setSelectedItems(List.of());
    }
}
