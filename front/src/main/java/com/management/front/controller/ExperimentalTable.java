package com.management.front.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.ArrayList;
import java.util.Map;

import static com.management.front.util.HttpClientUtil.request;

public class ExperimentalTable extends SplitPane {
    private TableView<Map> experimentalTable = new TableView<>();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();
    public ExperimentalTable() {
        this.setWidth(1000);
        //建立列
        TableColumn<Map, String> studentId = new TableColumn<>("学号");
        TableColumn<Map, String> studentName = new TableColumn<>("姓名");
        TableColumn<Map, String> studentGender = new TableColumn<>("性别");
        TableColumn<Map, String> studentMajor = new TableColumn<>("专业");
        studentId.setEditable(true);
        studentName.setEditable(true);
        studentGender.setEditable(true);
        studentMajor.setEditable(true);

        //把map填入单元格
        studentId.setCellValueFactory(new MapValueFactory<>("studentId"));//与后端属性一致
        studentName.setCellValueFactory(new MapValueFactory<>("name"));
        studentGender.setCellValueFactory(new MapValueFactory<>("gender"));
        studentMajor.setCellValueFactory(new MapValueFactory<>("major"));
        studentId.setCellFactory(TextFieldTableCell.forTableColumn());
        studentName.setCellFactory(TextFieldTableCell.forTableColumn());
        studentGender.setCellFactory(TextFieldTableCell.forTableColumn());
        studentMajor.setCellFactory(TextFieldTableCell.forTableColumn());

        experimentalTable.getColumns().addAll(studentId, studentName, studentGender, studentMajor);
        this.getItems().add(experimentalTable);
        displayStudents();
        experimentalTable.setEditable(true);
        experimentalTable.editableProperty().set(true);

        experimentalTable.selectionModelProperty().get().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null)
            {
                System.out.println(newValue);
            }
        });

    }

    private void displayStudents(){
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getAllStudents", null).getData()));
        experimentalTable.setItems(observableList);
    }

}
