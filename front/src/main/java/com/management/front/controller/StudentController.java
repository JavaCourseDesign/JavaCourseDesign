package com.management.front.controller;

import com.management.front.request.DataResponse;
import com.management.front.util.HttpClientUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.input.MouseEvent;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.management.front.util.HttpClientUtil.sendAndReceive;

public class StudentController {
    @FXML
    private TableView<Map> studentTableView;
    @FXML
    private TableColumn<Map, String> genderColumn;

    @FXML
    private TextField genderField;

    @FXML
    private TableColumn<Map, String> majorColumn;

    @FXML
    private TextField majorField;

    @FXML
    private TableColumn<Map, String> nameColumn;

    @FXML
    private TextField nameField;

    @FXML
    private TableColumn<Map, String> numColumn;

    @FXML
    private TextField numField;

    @FXML
    private Button save;
    @FXML
    private Button delete;
    private ArrayList<Map> studentList;
    private ObservableList<Map> observableList= FXCollections.observableArrayList();
    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < studentList.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(studentList.get(j)));
        }
        studentTableView.setItems(observableList);
    }
    @FXML
    //名字必须为这个，一字不差，且与fxml文件建立联系，系统才会调用这个方法，不然必须自己手动在启动器调用
   public void initialize()throws IOException
   {
       DataResponse r=sendAndReceive("/getStudent",null);
       studentList =(ArrayList<Map>) r.getData();
       numColumn.setCellValueFactory(new MapValueFactory<>("studentId"));
       nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
       genderColumn.setCellValueFactory(new MapValueFactory<>("gender"));
       majorColumn.setCellValueFactory(new MapValueFactory<>("major"));
       setTableViewData();
   }
    @FXML
    void onSaveStudentButton(ActionEvent event) throws IOException{
        Map<String,String> m=new HashMap<>();
        m.put("num",numField.getText());
        m.put("name",nameField.getText());
        m.put("gender",genderField.getText());
        m.put("major",majorField.getText());
        DataResponse r=sendAndReceive("/addStudent",m);

        if(r.getCode()==0)
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("警告");
            alert.setContentText(r.getMsg());
            alert.showAndWait();
        }
        else
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(r.getMsg());
            alert.showAndWait();
        }
        initialize();
    }
    @FXML
    void onDeleteStudentButton(MouseEvent event) throws IOException{
        Map form = studentTableView.getSelectionModel().getSelectedItem();
        if(form==null)
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("未选择，无法删除");
            alert.showAndWait();
        }
        DataResponse r=sendAndReceive("/deleteStudent",form);
        System.out.println(r.getCode());
        if(r.getCode()==1)
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("删除成功");
            alert.showAndWait();
        }
        initialize();
    }

}

