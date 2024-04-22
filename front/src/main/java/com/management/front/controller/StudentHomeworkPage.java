package com.management.front.controller;

import com.management.front.customComponents.SearchableTableView;
import com.management.front.request.DataResponse;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
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

import static com.management.front.util.HttpClientUtil.request;
import static com.management.front.util.HttpClientUtil.uploadFile;

public class StudentHomeworkPage extends VBox{
    private Button uploadHomeworkButton=new Button("上传作业");
    private ObservableList<Map> observableList= FXCollections.observableArrayList();
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
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF and Image files (*.pdf, *.jpg, *.png)", "*.pdf", "*.jpg", "*.png");
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
        TableColumn<Map,String> courseNameColumn= new TableColumn<>("课程名称");
        TableColumn<Map,String> homeworkContentColumn= new TableColumn<>("作业内容");
        TableColumn<Map,String> deadlineColumn= new TableColumn<>("截止时间");
        TableColumn<Map,String> submitTimeColumn= new TableColumn<>("提交时间");
        TableColumn<Map,String> gradeColumn= new TableColumn<>("成绩");

        courseNameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        homeworkContentColumn.setCellValueFactory(new MapValueFactory<>("homeworkContent"));
        deadlineColumn.setCellValueFactory(new MapValueFactory<>("deadline"));
        submitTimeColumn.setCellValueFactory(new MapValueFactory<>("submitTime"));
        gradeColumn.setCellValueFactory(new MapValueFactory<>("grade"));
        List<TableColumn<Map, ?>> columns = new ArrayList<>();
        columns.add(courseNameColumn);
        columns.add(homeworkContentColumn);
        columns.add(deadlineColumn);
        columns.add(submitTimeColumn);
        columns.add(gradeColumn);
        homeworkTable=new SearchableTableView(observableList,List.of("courseName","grade"),columns);
        this.getChildren().add(homeworkTable);
        this.setVgrow(homeworkTable, Priority.ALWAYS);
    }
}
