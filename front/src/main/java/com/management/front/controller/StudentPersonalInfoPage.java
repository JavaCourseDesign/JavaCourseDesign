package com.management.front.controller;

import com.management.front.request.DataResponse;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.management.front.util.HttpClientUtil.request;

public class StudentPersonalInfoPage extends TabPane {
    public StudentPersonalInfoPage() {
        Map m=new HashMap();
        m.put("personId",LoginPage.personId);
        Map student =(Map)request("/getStudentByPersonId",m).getData();
       this.getTabs().add(new BasicInfoTab(student));
    }
}
class BasicInfoTab extends Tab {
    private String fileName;
    Map student=new HashMap<>();
    VBox vBox=new VBox();
    GridPane gridPane=new GridPane();
    ImageView photoArea=new ImageView();
    public BasicInfoTab(Map student) {
        this.setText("基本信息");
        this.setContent(vBox);
        this.student=student;
       // vBox.getChildren().add(photoArea);
        AnchorPane anchorPane=new AnchorPane();
        anchorPane.setPrefHeight(200);
        anchorPane.setPrefWidth(200);
        vBox.getChildren().add(anchorPane);
        anchorPane.setStyle("-fx-background-color: #f0f8ff;");
        anchorPane.getChildren().add(photoArea);

// 当AnchorPane的大小改变时，更新ImageView的大小
        anchorPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            photoArea.setFitWidth(newVal.doubleValue());
        });

        anchorPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            photoArea.setFitHeight(newVal.doubleValue());
        });

        photoArea.setPickOnBounds(true);
        photoArea.setPreserveRatio(true);
        photoArea.setStyle("-fx-border-color: #000000; -fx-border-width: 10;");
        photoArea.setOnMouseClicked(event ->{
            System.out.println("click");
        });
        photoArea.setOnDragOver(event -> {
            if (event.getGestureSource() != photoArea && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        photoArea.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                try {
                    File file = db.getFiles().get(0);
                    byte[] fileContent = Files.readAllBytes(file.toPath());
                    String encodedString = Base64.getEncoder().encodeToString(fileContent);
                    Map<String,String> m=new HashMap<>();
                    fileName=file.getName();
                    m.put("fileName", fileName);
                    m.put("fileContent", encodedString);
                    DataResponse r = request("/uploadPhoto", m);
                    if(r.getCode() == 0) {
                        // handle success
                    } else {
                        // handle error
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                success = true;
            }
            System.out.println("drag drop");
            event.setDropCompleted(success);
            event.consume();
        });
        Button button=new Button("显示");
        vBox.getChildren().add(button);
        button.setOnMouseClicked(event -> {
            Map<String,String> m=new HashMap<>();
            m.put("fileName",fileName);
            DataResponse r=request("/getPhotoImageStr",m);
            if(r.getCode()==0)
            {
                String base64Image = r.getData().toString();
                byte[] decodedBytes = Base64.getDecoder().decode(base64Image);
                ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes);
                Image image = new Image(bis);
                photoArea.setImage(image);
            }
        });






        vBox.getChildren().add(gridPane);
        Button saveButton = new Button("保存");
        saveButton.setOnMouseClicked(event -> save());
        vBox.getChildren().add(saveButton);
        refresh();
    }


    public void save() {

        //request("/updateStudent", student);
    }
    public void refresh() {
        gridPane.addColumn(0,
                new Label("学号:"),
                new Label("姓名:"),
                new Label("性别:"),
                new Label("专业:"));
        gridPane.addColumn(1,
                new TextField(student.get("studentId").toString()),
                new TextField(student.get("name").toString()),
                new TextField(student.get("gender").toString()),
                new TextField(student.get("major").toString()));
    }
}
