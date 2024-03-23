package com.management.front.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebView;

import java.net.URL;

public class MainStage{


    @FXML
    private WebView timeTable; // 确保FXML文件中WebView的fx:id为"timeTable"

    @FXML
    private void initialize() {
        URL url = getClass().getResource("/com/management/front/timeTable.html");
        if (url != null) {
            // 使用WebEngine加载HTML文件
            timeTable.getEngine().load(url.toExternalForm());
        } else {
            System.err.println("找不到HTML文件");
        }
    }
}
