package com.management.front.controller;


import com.management.front.request.DataResponse;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

import static com.management.front.util.HttpClientUtil.request;

public class AddCourseController {
    @FXML
    private GridPane lessonTableGridPane;

    @FXML
    private TextField capacityField;

    @FXML
    private TextField courseIdField;

    @FXML
    private TextField creditField;

    @FXML
    private TextField referenceField;

    @FXML
    private Button saveCourseButton;

    @FXML
    private TextField teacherIdField;

    @FXML
    private TextField teacherNameField;
    int[][] array = new int[8][6];

    // 这是你的方法
    public void setArrayValue(int columnIndex, int rowIndex,int value) {
        array[columnIndex][rowIndex] = value;
    }
    @FXML
    void onSaveCourseButton(MouseEvent event) {
        //加基本信息
        Map<String, Object> map = new HashMap<>();
        map.put("capacity", capacityField.getText());
        map.put("courseId", courseIdField.getText());
        map.put("credit", creditField.getText());
        map.put("reference", referenceField.getText());
        map.put("teacherId", teacherIdField.getText());
        map.put("teacherName", teacherNameField.getText());
        //加课的信息
        for (Node node : lessonTableGridPane.getChildren()) {
            if (node instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) node;
                int columnIndex = GridPane.getColumnIndex(checkBox);
                int rowIndex = GridPane.getRowIndex(checkBox);
                if (checkBox.isSelected()) {
                    setArrayValue(columnIndex, rowIndex, 1);
                } else {
                    setArrayValue(columnIndex, rowIndex, 0);
                }
            }
        }
        map.put("array", array);
        //
        DataResponse r = request("/addCourse", map);
        if (r.getCode() == -1) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("警告");
            alert.setContentText(r.getMsg());
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(r.getMsg());
            alert.showAndWait();
            Stage stage = (Stage) saveCourseButton.getScene().getWindow();
            // 关闭窗口
            stage.close();
        }
    }
}
