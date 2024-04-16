package com.management.front.controller;

import com.management.front.util.HttpClientUtil;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;

import static com.management.front.util.HttpClientUtil.uploadFile;

public class FileUploadPage extends VBox {

    public FileUploadPage() {
        this.setAlignment(Pos.CENTER);
        this.getChildren().add(new AnchorPane());
        this.getChildren().add(new Text("请点击按钮文件上传文件"));
        Button button = new Button("上传");
        button.setOnMouseClicked(event -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                String filePath = file.getPath();
                String remoteFile = "file";
                String fileName="作业.pdf";
                //remoteFile   你想存在的后端的文件夹，不要加/，直接写名字
                uploadFile("/uploadFile",filePath, remoteFile,fileName);
            }
        });
        this.getChildren().add(button);
    }
}