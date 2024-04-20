package com.management.front.controller;

import com.management.front.customComponents.SearchableTableView;
import com.management.front.request.DataResponse;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.GridPane;

import java.util.*;
import java.util.stream.Collectors;

import static com.management.front.util.HttpClientUtil.request;

public class HonorManagementPage extends SplitPane {
    private SearchableTableView honorTable;
    private GridPane controlPanel = new GridPane();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    private Button addButton = new Button("Add");
    private Button deleteButton = new Button("Delete");
    private Button updateButton = new Button("Update");
    private TextField studentIdField = new TextField("201921000");
    private TextField nameField = new TextField("创新比赛");
    //private TextField typeField = new TextField("社会实践");
    private ComboBox<String> typeField = new ComboBox<>(FXCollections.observableArrayList(
            "社会实践","学科竞赛","科研成果","培训讲座","创新项目","校外实习"
    ));
    private TextField timeField = new TextField("2024");
    private TextField locationField = new TextField("软件学院");
    private TextField performanceField = new TextField("good");

    public HonorManagementPage() {
        this.setWidth(1000);
        initializeTable();
        initializeControlPanel();
        displayHonors();
    }






    private Map newMapFromFields(Map m)
    {
        m.put("studentId",studentIdField.getText());
        m.put("name",nameField.getText());
        m.put("type",typeField.getValue());
        m.put("time",timeField.getText());
        m.put("location",locationField.getText());
        m.put("performance",performanceField.getText());
        return m;
    }

    private void displayHonors() {
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getAllHonors", null).getData()));
        honorTable.setData(observableList);
    }

    private void initializeControlPanel() {
        controlPanel.addColumn(0,
                new Label("学号"),
                new Label("项目名称"),
                new Label("项目类型"),
                new Label("时间"),
                new Label("地点"),
                new Label("评价")
        );
        controlPanel.addColumn(1,
                studentIdField,
                nameField,
                typeField,
                timeField,
                locationField,
                performanceField
        );
        controlPanel.addRow(6, addButton, deleteButton, updateButton);
        addButton.setOnAction(event -> addHonor());
        deleteButton.setOnAction(event ->deleteHonor());
        updateButton.setOnAction(event -> updateHonor());

        honorTable.setOnItemClick(honor->{
            List<Map<String, Object>> persons = (List<Map<String, Object>>) honor.get("persons");//逻辑不完整，应该使用slv
            String studentIds = persons.stream()
                    .filter(person -> person.containsKey("studentId"))
                    .map(person -> (String) person.get("studentId"))
                    .collect(Collectors.joining(", "));
            studentIdField.setText(studentIds);
            nameField.setText((String) honor.get("name"));
            typeField.setValue((String) honor.get("type"));
            timeField.setText((String) honor.get("time"));
            locationField.setText((String) honor.get("location"));
            performanceField.setText((String) honor.get("performance"));
        });
        this.getItems().add(controlPanel);
    }




    private void updateHonor() {
        Map m=honorTable.getSelectedItem();
        if(m==null)
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
            DataResponse r=request("/updateHonor",newMapFromFields(m));
            displayHonors();

            if(r.getCode()==0)
            {
                Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                alert1.setContentText("更新成功");
                alert1.showAndWait();
            }
        }
    }


    private void deleteHonor() {
        Map m=honorTable.getSelectedItem();
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
                DataResponse r=request("/deleteHonor",m);
                displayHonors();
                if(r.getCode()==0)
                {
                    Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                    alert1.setContentText("删除成功");
                    alert1.showAndWait();
                }
            }
        }

    }

    private void addHonor() {
        Map m=newMapFromFields(new HashMap());
        DataResponse r=request("/addHonor",m);
        displayHonors();
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

    private void initializeTable()
    {
        TableColumn<Map, String> studentNameColumn = new TableColumn<>("学生姓名");
        TableColumn<Map,String> studentIdColumn = new TableColumn<>("学号");
        TableColumn<Map, String> nameColumn = new TableColumn<>("项目名称");
        TableColumn<Map, String> typeColumn = new TableColumn<>("项目类型");
        TableColumn<Map, String> timeColumn = new TableColumn<>("时间");
        TableColumn<Map, String> locationColumn = new TableColumn<>("地点");
        TableColumn<Map, String> performanceColumn = new TableColumn<>("评价");
        studentNameColumn.setCellValueFactory(data->{List<Map<String, Object>> persons = (List<Map<String, Object>>) data.getValue().get("persons");
            String personNames = persons.stream()
                    .filter(person -> person.containsKey("studentId"))
                    .map(person -> (String) person.get("name"))
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(personNames);});
        studentIdColumn.setCellValueFactory(data->{List<Map<String, Object>> persons = (List<Map<String, Object>>) data.getValue().get("persons");
            String studentIds = persons.stream()
                    .filter(person -> person.containsKey("studentId"))
                    .map(person -> (String) person.get("studentId"))
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(studentIds);});
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        typeColumn.setCellValueFactory(new MapValueFactory<>("type"));
        timeColumn.setCellValueFactory(new MapValueFactory<>("time"));
        locationColumn.setCellValueFactory(new MapValueFactory<>("location"));
        performanceColumn.setCellValueFactory(new MapValueFactory<>("performance"));


        //搜索使用
        List<TableColumn<Map, ?>> columns = new ArrayList<>();
        columns.add(studentNameColumn);
        columns.add(studentIdColumn);
        columns.add(nameColumn);
        columns.add(typeColumn);
        columns.add(timeColumn);
        columns.add(locationColumn);
        columns.add(performanceColumn);
        honorTable = new SearchableTableView(observableList, List.of("studentName","name","type"), columns);
        this.getItems().add(honorTable);
    }


}