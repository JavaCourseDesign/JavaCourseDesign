package com.management.client.page.admin;

import com.management.client.customComponents.SearchableTableView;
import com.management.client.request.DataResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.VBox;
import org.controlsfx.control.tableview2.FilteredTableColumn;

import java.util.*;

import static com.management.client.util.HttpClientUtil.request;

public class ClazzManagementPage extends SplitPane {
    private SearchableTableView clazzTable;
    private VBox controlPanel = new VBox();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    private Button addButton = new Button("Add");
    private Button deleteButton = new Button("Delete");
    private Button updateButton = new Button("Update");

    private TextField clazzMajorField = new TextField();
    private TextField clazzGradeField = new TextField();
    private TextField clazzNameField = new TextField();
    private TextField clazzNumberField = new TextField();

    private ListView<String> studentList = new ListView<>();

    private Map newMapFromFields(Map m) {
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
        FilteredTableColumn<Map, String> clazzNameColumn = new FilteredTableColumn<>("班级名");
        FilteredTableColumn<Map, String> clazzMajorColumn = new FilteredTableColumn<>("专业");
        FilteredTableColumn<Map, String> clazzGradeColumn = new FilteredTableColumn<>("年级");
        FilteredTableColumn<Map, String> clazzNumberColumn = new FilteredTableColumn<>("班级号");
        //FilteredTableColumn<Map, String> clazzStudentsColumn = new FilteredTableColumn<>("学生");
        FilteredTableColumn<Map, String> clazzStudentCountColumn = new FilteredTableColumn<>("学生数");
        FilteredTableColumn<Map, String> clazzMaleCountColumn = new FilteredTableColumn<>("男");
        FilteredTableColumn<Map, String> clazzFemaleCountColumn = new FilteredTableColumn<>("女");
        FilteredTableColumn<Map, String> clazzCPCCountColumn = new FilteredTableColumn<>("共产党员");
        FilteredTableColumn<Map, String> clazzCYLCountColumn = new FilteredTableColumn<>("共青团员");
        FilteredTableColumn<Map, String> clazzU18CountColumn = new FilteredTableColumn<>("未满18岁");
        FilteredTableColumn<Map, String> clazzO18CountColumn = new FilteredTableColumn<>("满18岁");
        FilteredTableColumn<Map, String> clazzInnoCountColumn = new FilteredTableColumn<>("创新实践项目");
        FilteredTableColumn<Map, String> clazzHonorCountColumn = new FilteredTableColumn<>("荣誉");
        FilteredTableColumn<Map, String> clazzAvgScoreColumn = new FilteredTableColumn<>("平均分");

        clazzMajorColumn.setCellValueFactory(new MapValueFactory<>("major"));
        clazzGradeColumn.setCellValueFactory(new MapValueFactory<>("grade"));
        clazzNameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        clazzNumberColumn.setCellValueFactory(new MapValueFactory<>("clazzNumber"));
        clazzStudentCountColumn.setCellValueFactory(new MapValueFactory<>("studentCount"));
        clazzMaleCountColumn.setCellValueFactory(new MapValueFactory<>("maleCount"));
        clazzFemaleCountColumn.setCellValueFactory(new MapValueFactory<>("femaleCount"));
        clazzCPCCountColumn.setCellValueFactory(new MapValueFactory<>("CPCCount"));
        clazzCYLCountColumn.setCellValueFactory(new MapValueFactory<>("CYLCount"));
        clazzU18CountColumn.setCellValueFactory(new MapValueFactory<>("under18Count"));
        clazzO18CountColumn.setCellValueFactory(new MapValueFactory<>("over18Count"));
        clazzInnoCountColumn.setCellValueFactory(new MapValueFactory<>("innoCount"));
        clazzHonorCountColumn.setCellValueFactory(new MapValueFactory<>("honorCount"));
        clazzAvgScoreColumn.setCellValueFactory(new MapValueFactory<>("avgScore"));


        /*clazzStudentsColumn.setCellValueFactory(data -> {
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
        });*/

        List<FilteredTableColumn<Map, ?>> columns = new ArrayList<>();
        columns.addAll(List.of(
                  clazzMajorColumn
                , clazzGradeColumn
                , clazzNumberColumn
                , clazzNameColumn
                , clazzStudentCountColumn
                , clazzMaleCountColumn
                , clazzFemaleCountColumn
                , clazzCPCCountColumn
                , clazzCYLCountColumn
                , clazzU18CountColumn
                , clazzO18CountColumn
                , clazzInnoCountColumn
                , clazzHonorCountColumn
                , clazzAvgScoreColumn)) ;

        clazzTable = new SearchableTableView(observableList, List.of("clazzNumber","name"), columns);

        this.getItems().add(clazzTable);
    }

    private void initializeControlPanel() {
        controlPanel.setMinWidth(200);
        controlPanel.setSpacing(10);

        controlPanel.getChildren().addAll(clazzNameField, clazzMajorField, clazzGradeField, clazzNumberField , studentList,addButton, deleteButton, updateButton);

        addButton.setOnAction(event -> addClazz());
        deleteButton.setOnAction(event -> deleteClazz());
        updateButton.setOnAction(event -> updateClazz());

        clazzTable.setOnItemClick(clazz -> {
            if(clazz!=null)
            {
                clazzNameField.setText((String) clazz.get("name"));
                clazzMajorField.setText((String) clazz.get("major"));
                clazzGradeField.setText((String) clazz.get("grade"));
                clazzNumberField.setText((String) clazz.get("clazzNumber"));

                studentList.setItems(FXCollections.observableArrayList((List<String>) clazz.get("students")));
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
