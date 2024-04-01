package com.management.front.controller;
import com.management.front.TestApplication;
import com.management.front.request.DataResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static com.management.front.util.HttpClientUtil.request;

public class CourseController{
    @FXML
    private Button addCourseButton;
    @FXML
    private TableView<Map> courseTable;

    @FXML
    private TableColumn<Map, String> capacityColumn;

    @FXML
    private TableColumn<Map,String> courseIdColumn;

    @FXML
    private TableColumn<Map,String> creditColumn;

    @FXML
    private Button deleteCoureseButton;

    @FXML
    private TableColumn<Map, String> referenceColumn;
    @FXML
    private TableColumn<Map, String> teacherNameColumn;
    private ObservableList<Map> observableList= FXCollections.observableArrayList();
    private ArrayList<Map> courseList;
    private void setTableViewData() {
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList(courseList));
        courseTable.setItems(observableList);
    }
    @FXML
    public void initialize() {
        DataResponse r=request("/getCourse",null);
        courseList =(ArrayList<Map>) r.getData();
        courseIdColumn.setCellValueFactory(new MapValueFactory<>("courseId"));
        creditColumn.setCellValueFactory(new MapValueFactory<>("credit"));
        capacityColumn.setCellValueFactory(new MapValueFactory<>("capacity"));
        referenceColumn.setCellValueFactory(new MapValueFactory<>("reference"));
        teacherNameColumn.setCellValueFactory(new MapValueFactory<>("teacherName"));
        setTableViewData();
    }

    @FXML
    void onAddCourseButton(ActionEvent event)throws IOException {
        Stage stage=new Stage();
        FXMLLoader fxmlLoader=new FXMLLoader(TestApplication.class.getResource("adminFxml/addCourse.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 800, 400);
        stage.setScene(scene);//舞台设置场景
        stage.show();//舞台展现
    }

    @FXML
    void onDeleteCourseButton(ActionEvent event) {

    }

}

