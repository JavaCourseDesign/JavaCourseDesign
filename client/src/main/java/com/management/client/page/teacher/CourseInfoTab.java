package com.management.client.page.teacher;

import com.calendarfx.view.popover.EntryPopOverContentPane;
import com.management.client.ClientApplication;
import com.management.client.customComponents.WeekTimeTable;
import com.management.client.request.DataResponse;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.management.client.util.HttpClientUtil.request;
import static com.management.client.util.HttpClientUtil.uploadFile;

public class CourseInfoTab extends Tab {
    Map course;
    private Button pptUploadButton = new Button("上传PPT");
    private Button pdfUploadButton = new Button("上传参考资料PDF");
    private Button pptDownloadButton = new Button("下载PPT");
    private Button pdfDownloadButton = new Button("下载参考资料PDF");
    private TableView studentTable = new TableView();
    private WeekTimeTable weekTimeTable = new WeekTimeTable();
    public CourseInfoTab(Map course) {
        this.course = course;
        pptUploadButton.setStyle("-fx-background-color: #00a1d6; -fx-text-fill: white ;-fx-font-size: 20");
        pdfUploadButton.setStyle("-fx-background-color: #00a1d6; -fx-text-fill: white;-fx-font-size: 20");
        pptDownloadButton.setStyle("-fx-background-color: #00a1d6; -fx-text-fill: white;-fx-font-size: 20");
        pdfDownloadButton.setStyle("-fx-background-color: #00a1d6; -fx-text-fill: white;-fx-font-size: 20");
        pptUploadButton.setOnMouseClicked(event -> uploadPPT());
        pdfUploadButton.setOnMouseClicked(event -> uploadPDF());
        pptDownloadButton.setOnMouseClicked(event -> downloadPPT());
        pdfDownloadButton.setOnMouseClicked(event -> downloadPDF());
        weekTimeTable.getCalendars().get(0).setReadOnly(true);
       /* weekTimeTable.setEntryDetailsPopOverContentCallback(
                param->{
                    eventId=param.getEntry().getId();
                    return  new EntryPopOverContentPane(param.getPopOver(), param.getDateControl(), param.getEntry());
                }
        );*/
        initializeStudentTable();
        displayCourseInfo();
        this.setText("课程信息");
    }

    private void uploadPDF() {
        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("选择pdf文件");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF  (*.pdf)", "*.pdf");
        fileDialog.getExtensionFilters().add(extFilter);
        File file = fileDialog.showOpenDialog(null);
        if(file==null)
        {
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setContentText("未选择文件");
            alert.show();
            return;
        }
        DataResponse r=uploadFile("/uploadReference",file.getPath(),file.getName(),(String) course.get("courseId"));
        if(r.getCode()==0)
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("上传成功");
            alert.show();
        }
        else
        {
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setContentText(r.getMsg());
            alert.show();
        }
    }

    private void downloadPPT() {
        if(weekTimeTable.getEvents().isEmpty())
        {
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setContentText("未选课，功能暂不开放");
            alert.show();
            return;
        }
        if(weekTimeTable.getSelectedEvents().size()>1)
        {
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setContentText("只能选择一节课");
            alert.show();
            return;
        }
        if(weekTimeTable.getSelectedEvents().isEmpty())
        {
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setContentText("未选择对应课");
            alert.show();
            return;
        }
        Map lesson=weekTimeTable.getSelectedEvents().get(0);
        DataResponse r=request("/downloadPPT",lesson);
        if(r.getCode()!=0)
        {
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setContentText(r.getMsg());
            alert.show();
            return;
        }
        byte[] data= Base64.getDecoder().decode(r.getData().toString());
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PPT Files", "*.pptx","*.ppt"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(data);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void downloadPDF() {
        DataResponse r=request("/downloadReference",course);
        if(r.getCode()!=0)
        {
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setContentText(r.getMsg());
            alert.show();
            return;
        }
        byte[] data= Base64.getDecoder().decode(r.getData().toString());
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(data);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadPPT() {
        if(weekTimeTable.getEvents().isEmpty())
        {
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setContentText("未选课，功能暂不开放");
            alert.show();
            return;
        }

        if(weekTimeTable.getSelectedEvents().size()>1)
        {
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setContentText("只能选择一节课");
            alert.show();
            return;
        }
        if(weekTimeTable.getSelectedEvents().isEmpty())
        {
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setContentText("未选择对应课");
            alert.show();
            return;
        }
        Map lesson=weekTimeTable.getSelectedEvents().get(0);
        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("选择ppt文件");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PPT files (*.ppt, *.pptx)", "*.ppt", "*.pptx");
        fileDialog.getExtensionFilters().add(extFilter);
        File file = fileDialog.showOpenDialog(null);
        if(file==null)
        {
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setContentText("未选择文件");
            alert.show();
            return;
        }
       DataResponse r= uploadFile("/uploadPPT",file.getPath(),file.getName(),(String)lesson.get("eventId") );
        if(r.getCode()==0)
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("上传成功");
            alert.show();
        }
        else
        {
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setContentText(r.getMsg());
            alert.show();
        }
        displayCourseInfo();
    }

    private void displayCourseInfo() {
        VBox courseInfoBox = new VBox();
        HBox hBox = new HBox();
        weekTimeTable.setEvents((List<Map<String,Object>>) request("/getLessonsByCourse", course).getData());
        weekTimeTable.setPrefHeight(500);
        weekTimeTable.setPrefWidth(800);
        hBox.getChildren().add(courseInfoBox);
        VBox vBox = new VBox();
        hBox.getChildren().add(vBox);
        ImageView ppt=new ImageView(new Image(ClientApplication.class.getResourceAsStream("/images/ppt.png")));
        ImageView pdf=new ImageView(new Image(ClientApplication.class.getResourceAsStream("/images/pdf.png")));
        ppt.setFitHeight(150);
        pdf.setFitHeight(150);
        ppt.setPreserveRatio(true);
        pdf.setPreserveRatio(true);
        vBox.getChildren().addAll(ppt, pptUploadButton, pptDownloadButton, pdf, pdfUploadButton, pdfDownloadButton,studentTable);
        String courseId="sdu"+String.format("%06d",Integer.parseInt((String) course.get("courseId")));
        Label courseIdLabel = new Label("课程号: " + courseId);
        Label courseNameLabel = new Label("课程名: " + course.get("name"));
        String reference=course.get("reference")+"";
        reference=reference.replace(".pdf","");
        Label referenceLabel = new Label("参考资料: " +reference );
        Label capacityLabel = new Label("课容量: " + course.get("capacity"));
        Label creditLabel = new Label("学分: " + course.get("credit"));
        Label typeLabel = new Label("课程类型: " +course.get("type"));
        Label preCoursesLabel = new Label("先修课程: " + course.get("preCourses"));
        Label teacherLabel = new Label("教师: " + ((List<Map>)course.get("persons")).stream()
                .filter(person -> person.containsKey("teacherId"))
                .map(person -> ""+ person.get("name"))
                .collect(Collectors.joining(", ")));
        courseIdLabel.setStyle("-fx-font-size: 20 ; -fx-text-fill: #00a1d6");
        courseNameLabel.setStyle("-fx-font-size: 20 ; -fx-text-fill: #00a1d6");
        referenceLabel.setStyle("-fx-font-size: 20 ; -fx-text-fill: #00a1d6");
        capacityLabel.setStyle("-fx-font-size: 20 ; -fx-text-fill: #00a1d6");
        creditLabel.setStyle("-fx-font-size: 20 ; -fx-text-fill: #00a1d6");
        typeLabel.setStyle("-fx-font-size: 20 ; -fx-text-fill: #00a1d6");
        preCoursesLabel.setStyle("-fx-font-size: 20 ; -fx-text-fill: #00a1d6");
        teacherLabel.setStyle("-fx-font-size: 20 ; -fx-text-fill: #00a1d6");
        courseInfoBox.getChildren().addAll(weekTimeTable, courseIdLabel, courseNameLabel, referenceLabel, capacityLabel, creditLabel, typeLabel, preCoursesLabel, teacherLabel);
        this.setContent(hBox);
    }
    private void initializeStudentTable()
    {
        ObservableList list=FXCollections.observableArrayList();
        List<Map> persons=(List<Map>)(course.get("persons"));
        for(Map person:persons)
        {
            if(person.containsKey("studentId"))
            {
                Map m=Map.of("studentId",person.get("studentId"),"name",person.get("name"));
                list.add(m);
            }
        }
        TableColumn<Map, String> studentIdColumn = new TableColumn<>("学号");
        TableColumn<Map, String> studentNameColumn = new TableColumn<>("姓名");
        studentIdColumn.setCellValueFactory(new MapValueFactory<>("studentId"));
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        studentTable.setItems(list);
        studentTable.getColumns().addAll(studentIdColumn, studentNameColumn);
    }
}
