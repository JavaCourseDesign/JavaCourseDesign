package com.management.client.page.admin;

import com.management.client.customComponents.SearchableTableView;
import com.management.client.request.DataResponse;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.VBox;

import java.util.*;
import java.util.stream.Collectors;

import static com.management.client.util.HttpClientUtil.request;

public class ClazzManagementPage extends SplitPane {
    private SearchableTableView clazzTable;
    private VBox controlPanel = new VBox();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    private Button addButton = new Button("Add");
    private Button deleteButton = new Button("Delete");
    private Button updateButton = new Button("Update");

    private TextField clazzIdField = new TextField();
    private TextField clazzMajorField = new TextField();
    private TextField clazzGradeField = new TextField();
    private TextField clazzNameField = new TextField();
    private TextField clazzNumberField = new TextField();

    private Map newMapFromFields(Map m) {
        m.put("clazzId", clazzIdField.getText());
        m.put("name", clazzNameField.getText());
        m.put("major", clazzMajorField.getText());
        m.put("grade", clazzGradeField.getText());
        m.put("clazzNumber", clazzNumberField.getText());
        return m;
    }

    public ClazzManagementPage() {
        this.setWidth(1000);
        initializeTable();
        initializeControlPanel();
        displayClazz();
    }

    private void initializeTable() {
        TableColumn<Map, String> clazzIdColumn = new TableColumn<>("班级ID");
        TableColumn<Map, String> clazzNameColumn = new TableColumn<>("班级名");
        TableColumn<Map, String> clazzMajorColumn = new TableColumn<>("专业");
        TableColumn<Map, String> clazzGradeColumn = new TableColumn<>("年级");
        TableColumn<Map, String> clazzStudentsColumn = new TableColumn<>("学生");
        TableColumn<Map, String> clazzNumberColumn = new TableColumn<>("班级号");

        clazzIdColumn.setCellValueFactory(new MapValueFactory<>("clazzId"));
        clazzMajorColumn.setCellValueFactory(new MapValueFactory<>("major"));
        clazzGradeColumn.setCellValueFactory(new MapValueFactory<>("grade"));
        clazzNameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        clazzNumberColumn.setCellValueFactory(new MapValueFactory<>("clazzNumber"));

        clazzStudentsColumn.setCellValueFactory(new MapValueFactory<>("persons"));
        clazzStudentsColumn.setCellValueFactory(data -> {
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
        columns.addAll(List.of(clazzIdColumn, clazzMajorColumn, clazzGradeColumn, clazzNumberColumn, clazzNameColumn, clazzStudentsColumn)) ;

        clazzTable = new SearchableTableView(observableList, List.of("clazzNumber","name"), columns);

        this.getItems().add(clazzTable);
    }

    private void initializeControlPanel() {
        controlPanel.setMinWidth(200);
        controlPanel.setSpacing(10);

        controlPanel.getChildren().addAll(clazzIdField, clazzNameField, clazzMajorField, clazzGradeField, clazzNumberField , addButton, deleteButton, updateButton);

        addButton.setOnAction(event -> addClazz());
        deleteButton.setOnAction(event -> deleteClazz());
        updateButton.setOnAction(event -> updateClazz());

        clazzTable.setOnItemClick(clazz -> {
            if(clazz!=null)
            {
                clazzIdField.setText((String) clazz.get("clazzId"));
                clazzNameField.setText((String) clazz.get("name"));
                clazzMajorField.setText((String) clazz.get("major"));
                clazzGradeField.setText((String) clazz.get("grade"));
                clazzNumberField.setText((String) clazz.get("clazzNumber"));
            }
        });
        this.getItems().add(controlPanel);
    }

    private void displayClazz(){
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getAllClazz", null).getData()));
        clazzTable.setData(observableList);
        System.out.println(observableList);
    }

    private void addClazz() {
        Map m=newMapFromFields(new HashMap<>());

        System.out.println(m);

        DataResponse r=request("/addClazz",m);

        displayClazz();

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

    private void deleteClazz() {
        Map m = clazzTable.getSelectedItem();
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
                DataResponse r=request("/deleteClazz",m);
                System.out.println(m);
                System.out.println(r);

                displayClazz();

                if(r.getCode()==0)
                {
                    Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                    alert1.setContentText("删除成功");
                    alert1.showAndWait();
                }
            }
        }
    }

    private void updateClazz() {
        Map selected = clazzTable.getSelectedItem();
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
            DataResponse r=request("/updateClazz",newMapFromFields(selected));

            displayClazz();

            if(r.getCode()==0)
            {
                Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                alert1.setContentText("更新成功");
                alert1.showAndWait();
            }
        }
    }
}
