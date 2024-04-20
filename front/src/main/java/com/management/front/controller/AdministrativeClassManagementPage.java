package com.management.front.controller;

import com.management.front.customComponents.SearchableTableView;
import com.management.front.request.DataResponse;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.management.front.util.HttpClientUtil.request;

public class AdministrativeClassManagementPage extends SplitPane {
    private SearchableTableView administrativeClassTable;
    private VBox controlPanel = new VBox();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    private Button addButton = new Button("Add");
    private Button deleteButton = new Button("Delete");
    private Button updateButton = new Button("Update");

    private TextField administrativeClassIdField = new TextField();
    private TextField administrativeMajorField = new TextField();
    private TextField administrativeGradeField = new TextField();
    private TextField administrativeNameField = new TextField();
    private TextField administrativeClassNumberField = new TextField();

    private Map newMapFromFields(Map m) {
        m.put("administrativeClassId", administrativeClassIdField.getText());
        m.put("name", administrativeNameField.getText());
        m.put("major", administrativeMajorField.getText());
        m.put("grade", administrativeGradeField.getText());
        m.put("classNumber", administrativeClassNumberField.getText());
        return m;
    }

    public AdministrativeClassManagementPage() {
        this.setWidth(1000);
        initializeTable();
        initializeControlPanel();
        displayAdministrativeClasses();
        // 添加背景图片
        Image backgroundImage = new Image("E:\\JavaCourseDesign\\front\\src\\main\\resources\\com\\management\\front\\images\\menupage03.jpg");
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        this.setBackground(new Background(background));
    }

    private void initializeTable() {
        TableColumn<Map, String> administrativeClassIdColumn = new TableColumn<>("班级ID");
        TableColumn<Map, String> administrativeNameColumn = new TableColumn<>("班级名");
        TableColumn<Map, String> administrativeMajorColumn = new TableColumn<>("专业");
        TableColumn<Map, String> administrativeGradeColumn = new TableColumn<>("年级");
        TableColumn<Map, String> administrativeClassStudentsColumn = new TableColumn<>("学生");
        TableColumn<Map, String> administrativeClassNumberColumn = new TableColumn<>("班级号");

        administrativeClassIdColumn.setCellValueFactory(new MapValueFactory<>("administrativeClassId"));
        administrativeMajorColumn.setCellValueFactory(new MapValueFactory<>("major"));
        administrativeGradeColumn.setCellValueFactory(new MapValueFactory<>("grade"));
        administrativeNameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        administrativeClassNumberColumn.setCellValueFactory(new MapValueFactory<>("classNumber"));

        administrativeClassStudentsColumn.setCellValueFactory(new MapValueFactory<>("persons"));
        administrativeClassStudentsColumn.setCellValueFactory(data -> {
            List<Map<String, Object>> persons = (List<Map<String, Object>>) data.getValue().get("students");
            if(persons==null)
            {
                return new SimpleStringProperty("");
            }
            String personNames = persons.stream()
                    .filter(person -> person.containsKey("studentId"))
                    .map(person -> (String) person.get("name"))
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(personNames);
        });

        List<TableColumn<Map, ?>> columns = new ArrayList<>();
        columns.addAll(List.of(administrativeClassIdColumn, administrativeMajorColumn, administrativeGradeColumn, administrativeClassNumberColumn, administrativeNameColumn, administrativeClassStudentsColumn)) ;

        administrativeClassTable = new SearchableTableView(observableList, List.of("administrativeClassNumber","name"), columns);

        this.getItems().add(administrativeClassTable);
    }

    private void initializeControlPanel() {
        controlPanel.setMinWidth(200);
        controlPanel.setSpacing(10);

        controlPanel.getChildren().addAll(administrativeClassIdField, administrativeNameField, administrativeMajorField, administrativeGradeField, administrativeClassNumberField , addButton, deleteButton, updateButton);

        addButton.setOnAction(event -> addAdministrativeClass());
        deleteButton.setOnAction(event -> deleteAdministrativeClass());
        updateButton.setOnAction(event -> updateAdministrativeClass());

        administrativeClassTable.setOnItemClick(administrativeClass -> {
            if(administrativeClass!=null)
            {
                administrativeClassIdField.setText((String) administrativeClass.get("administrativeClassId"));
                administrativeNameField.setText((String) administrativeClass.get("name"));
                administrativeMajorField.setText((String) administrativeClass.get("major"));
                administrativeGradeField.setText((String) administrativeClass.get("grade"));
                administrativeClassNumberField.setText((String) administrativeClass.get("classNumber"));
            }
        });
        this.getItems().add(controlPanel);
    }

    private void displayAdministrativeClasses(){
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getAllAdministrativeClasses", null).getData()));
        administrativeClassTable.setData(observableList);
        System.out.println(observableList);
    }

    private void addAdministrativeClass() {
        Map m=newMapFromFields(new HashMap<>());

        System.out.println(m);

        DataResponse r=request("/addAdministrativeClass",m);

        displayAdministrativeClasses();

        if(r.getCode()==-1)
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
    }

    private void deleteAdministrativeClass() {
        Map m = administrativeClassTable.getSelectedItem();
        if(m==null)
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("未选择，无法删除");
            alert.showAndWait();
        }
        else
        {
            Alert alert=new Alert(Alert.AlertType.CONFIRMATION, "确定要删除吗？");
            alert.setTitle("警告");
            Optional<ButtonType> result=alert.showAndWait();
            if(result.get()==ButtonType.OK)
            {
                DataResponse r=request("/deleteAdministrativeClass",m);
                System.out.println(m);
                System.out.println(r);

                displayAdministrativeClasses();

                if(r.getCode()==0)
                {
                    Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                    alert1.setContentText("删除成功");
                    alert1.showAndWait();
                }
            }
        }
    }

    private void updateAdministrativeClass() {
        Map selected = administrativeClassTable.getSelectedItem();
        if(selected==null)
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("未选择，无法更新");
            alert.showAndWait();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "确定要更新吗？");
        alert.setTitle("警告");
        Optional<ButtonType> result=alert.showAndWait();
        if(result.get()==ButtonType.OK)
        {
            DataResponse r=request("/updateAdministrativeClass",newMapFromFields(selected));

            displayAdministrativeClasses();

            if(r.getCode()==0)
            {
                Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                alert1.setContentText("更新成功");
                alert1.showAndWait();
            }
        }
    }
}
