package com.management.front.controller;

import com.management.front.request.DataResponse;
import com.management.front.util.HttpClientUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.management.front.util.HttpClientUtil.request;
import static com.management.front.util.HttpClientUtil.sendAndReceiveDataResponse;
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
    private RadioButton man;
    @FXML
    private RadioButton woman;
    @FXML
    private HBox genderBox;

    @FXML
    private Button save;
    @FXML
    private Button delete;
    @FXML
    private Button queryButton;
    @FXML
    private TextField queryField;
    private ArrayList<Map> studentList;
    private String gender;
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
      // DataResponse r=sendAndReceiveDataResponse("/getStudent",null);
       DataResponse r=request("/getStudent",null);
       studentList =(ArrayList<Map>) r.getData();
       numColumn.setCellValueFactory(new MapValueFactory<>("studentId"));
       nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
       genderColumn.setCellValueFactory(new MapValueFactory<>("gender"));
       majorColumn.setCellValueFactory(new MapValueFactory<>("major"));
       setTableViewData();
       queryField.setOnKeyPressed(new EventHandler<KeyEvent>() {
           @Override
           public void handle(KeyEvent event) {
               if (event.getCode() == KeyCode.ENTER) {
                   onQueryButton();
               }
           }
       });
       queryButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent event) {
               onQueryButton();
           }
       });
   }
   @FXML
   public void onQueryButton()
   {
       String s=queryField.getText();
       Map<String,String> m=new HashMap();
       m.put("numName",s);
       DataResponse res=request("/queryStudent",m);
       if(res != null && res.getCode()== 0) {
           studentList = (ArrayList<Map>)res.getData();
       }
       numColumn.setCellValueFactory(new MapValueFactory("studentId"));
       nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
       majorColumn.setCellValueFactory(new MapValueFactory<>("major"));
       genderColumn.setCellValueFactory(new MapValueFactory<>("gender"));
      // TableView.TableViewSelectionModel<Map> tsm = studentTableView.getSelectionModel();
      // ObservableList<Integer> list = tsm.getSelectedIndices();
      // list.addListener(this::onTableRowSelect);
       setTableViewData();
   }
    @FXML
    String onSelectGenderButton(ActionEvent event) {
        if(event.getSource()==man)
        {
            gender="男";
        }
        else
        {
            gender="女";
        }
        return gender;
    }

    @FXML
    void onSaveStudentButton(ActionEvent event) throws IOException{
        Map<String,String> m=new HashMap<>();
        m.put("num",numField.getText());
        m.put("name",nameField.getText());
        m.put("gender",gender);
        m.put("major",majorField.getText());
        DataResponse r=request("/addStudent",m);

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
        else
        {
            Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("警告");
            alert.setContentText("确定要删除吗？");
            Optional<ButtonType> result=alert.showAndWait();
            if(result.get()==ButtonType.OK)
            {
                DataResponse r=sendAndReceiveDataResponse("/deleteStudent",form);
                if(r.getCode()==1)
                {
                    Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                    alert1.setContentText("删除成功");
                    alert1.showAndWait();
                }
            }
        }
        initialize();
    }
}


