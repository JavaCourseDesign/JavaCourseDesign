package com.management.client.page.student;

import com.management.client.customComponents.SearchableTableView;
import com.management.client.request.DataResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.management.client.util.HttpClientUtil.request;
import static com.management.client.util.HttpClientUtil.uploadFile;

public class StudentHomeworkPage extends VBox{
    private final Button uploadHomeworkButton=new Button("上传作业");
    private final ObservableList<Map> observableList= FXCollections.observableArrayList();
    private SearchableTableView homeworkTable;

    public StudentHomeworkPage() {
        this.getChildren().add(uploadHomeworkButton);
        uploadHomeworkButton.setPrefWidth(100);
        uploadHomeworkButton.setPrefHeight(100);
        uploadHomeworkButton.setOnMouseClicked(event -> {
            if(homeworkTable.getSelectedItem()==null)
            {
                Alert alert=new Alert(Alert.AlertType.ERROR);
                alert.setContentText("请先选择一条课程记录");
                alert.show();
            }
            else
            {
                uploadHomeWork(homeworkTable.getSelectedItem());
            }
        });
        initializeTable();
        displayAllHomework();
    }

    private void uploadHomeWork(Map m) {
        String submitTime = LocalDate.now().toString();  // set submit time to today
        String deadline =(String)m.get("deadline");  // get deadline from selected item
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate submitDate = LocalDate.parse(submitTime, formatter);
        LocalDate deadlineDate = LocalDate.parse(deadline, formatter);
        if (submitDate.isAfter(deadlineDate)) {
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setContentText("提交时间已经超过截止日期");
            alert.show();
            return;
        }
        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("选择作业文件");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf");
        fileDialog.getExtensionFilters().add(extFilter);

        File file = fileDialog.showOpenDialog(null);
        if(file==null)
        {
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setContentText("未选择文件");
            alert.show();
            return;
        }
        DataResponse r=uploadFile("/uploadHomework",file.getPath(), file.getName(),(String) homeworkTable.getSelectedItem().get("homeworkId"));
        if(r.getCode()==0)
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("上传成功");
            alert.show();
            displayAllHomework();
        }
        else
        {
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setContentText("上传失败");
            alert.show();
        }
    }

    private void displayAllHomework() {
        observableList.clear();
        ArrayList<Map> list=(ArrayList<Map>) request("/getStudentHomework", null).getData();
        for(Map m:list)
        {
            if(m.get("course")!=null)
            {
                Map course=(Map) m.get("course");
                m.put("courseName",course.get("name"));
            }
        }
        observableList.addAll(FXCollections.observableArrayList(list));
        homeworkTable.setData(observableList);
    }

    private void initializeTable() {
        FilteredTableColumn<Map,String> courseNameColumn= new FilteredTableColumn<>("课程名称");
        FilteredTableColumn<Map,String> homeworkContentColumn= new FilteredTableColumn<>("作业内容");
        FilteredTableColumn<Map,String> deadlineColumn= new FilteredTableColumn<>("截止时间");
        FilteredTableColumn<Map,String> submitTimeColumn= new FilteredTableColumn<>("提交时间");
        FilteredTableColumn<Map,String> gradeColumn= new FilteredTableColumn<>("成绩");

        courseNameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        homeworkContentColumn.setCellValueFactory(new MapValueFactory<>("homeworkContent"));
        deadlineColumn.setCellValueFactory(new MapValueFactory<>("deadline"));
        submitTimeColumn.setCellValueFactory(new MapValueFactory<>("submitTime"));
        gradeColumn.setCellValueFactory(new MapValueFactory<>("grade"));
        List<FilteredTableColumn<Map, ?>> columns = new ArrayList<>();
        columns.add(courseNameColumn);
        columns.add(homeworkContentColumn);
        columns.add(deadlineColumn);
        columns.add(submitTimeColumn);
        columns.add(gradeColumn);
        homeworkTable=new SearchableTableView(observableList,List.of("courseName","grade"),columns);
        this.getChildren().add(homeworkTable);
        setVgrow(homeworkTable, Priority.ALWAYS);
    }
}
