package com.management.front.controller;

import com.management.front.customComponents.SearchableTableView;
import com.management.front.request.DataResponse;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.VBox;

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
    private TextField nameField = new TextField();

    private Map newMapFromFields(Map m) {
        m.put("administrativeClassId", administrativeClassIdField.getText());
        m.put("name", nameField.getText());
        return m;
    }

    public AdministrativeClassManagementPage() {
        this.setWidth(1000);
        initializeTable();
        initializeControlPanel();
        displayAdministrativeClasses();
    }

    private void initializeTable() {
        TableColumn<Map, String> administrativeClassIdColumn = new TableColumn<>("班级号");
        TableColumn<Map, String> administrativeClassStudentsColumn = new TableColumn<>("学生");

        administrativeClassIdColumn.setCellValueFactory(new MapValueFactory<>("administrativeClassId"));

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
        columns.addAll(List.of(administrativeClassIdColumn, administrativeClassStudentsColumn)) ;

        administrativeClassTable = new SearchableTableView(observableList, List.of("administrativeClassId","name"), columns);

        this.getItems().add(administrativeClassTable);
    }

    private void initializeControlPanel() {
        controlPanel.setMinWidth(200);
        controlPanel.setSpacing(10);

        controlPanel.getChildren().addAll(administrativeClassIdField, nameField, addButton, deleteButton, updateButton);

        addButton.setOnAction(event -> addAdministrativeClass());
        deleteButton.setOnAction(event -> deleteAdministrativeClass());
        updateButton.setOnAction(event -> updateAdministrativeClass());

        administrativeClassTable.setOnItemClick(administrativeClass -> {
            if(administrativeClass!=null)
            {
                administrativeClassIdField.setText((String) administrativeClass.get("administrativeClassId"));
                nameField.setText((String) administrativeClass.get("name"));
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
