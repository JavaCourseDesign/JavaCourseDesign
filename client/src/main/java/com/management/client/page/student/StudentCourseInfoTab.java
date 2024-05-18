package com.management.client.page.student;

import com.management.client.ClientApplication;
import com.management.client.customComponents.WeekTimeTable;
import com.management.client.request.DataResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.management.client.util.HttpClientUtil.request;
import static com.management.client.util.HttpClientUtil.uploadFile;

public class StudentCourseInfoTab extends Tab {
    Map course;

    private final Button pptDownloadButton = new Button("下载PPT");
    private final Button pdfDownloadButton = new Button("下载参考资料PDF");
    private final WeekTimeTable weekTimeTable = new WeekTimeTable();
    public StudentCourseInfoTab(Map course) {
        this.course = course;

        pptDownloadButton.setStyle("-fx-background-color: #00a1d6; -fx-text-fill: white;-fx-font-size: 20");
        pdfDownloadButton.setStyle("-fx-background-color: #00a1d6; -fx-text-fill: white;-fx-font-size: 20");

        pptDownloadButton.setOnMouseClicked(event -> downloadPPT());
        pdfDownloadButton.setOnMouseClicked(event -> downloadPDF());
        weekTimeTable.getCalendars().get(0).setReadOnly(true);
       /* weekTimeTable.setEntryDetailsPopOverContentCallback(
                param->{
                    eventId=param.getEntry().getId();
                    return  new EntryPopOverContentPane(param.getPopOver(), param.getDateControl(), param.getEntry());
                }
        );*/
        displayCourseInfo();
        this.setText("课程信息");
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
        vBox.getChildren().addAll(ppt, pptDownloadButton, pdf,  pdfDownloadButton);
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
}

