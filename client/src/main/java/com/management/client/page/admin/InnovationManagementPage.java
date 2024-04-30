package com.management.client.page.admin;

import com.management.client.customComponents.SearchableListView;
import com.management.client.customComponents.SearchableTableView;
import com.management.client.request.DataResponse;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.*;

import static com.management.client.util.HttpClientUtil.request;

public class InnovationManagementPage extends SplitPane {
    private SearchableTableView innovationTable;
    private TableView studentTable=new TableView();
    private VBox   controlPanel = new VBox();
    private GridPane gridPane = new GridPane();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();
    private ObservableList<Map> studentObservableList = FXCollections.observableArrayList();
    private SearchableListView studentListView= new SearchableListView(FXCollections.observableArrayList((ArrayList) request("/getAllStudents", null).getData()), List.of("name", "studentId"));

    private Button addButton = new Button("Add");
    private Button deleteButton = new Button("Delete");
    private Button updateButton = new Button("Update");

    private TextField nameField = new TextField("创新比赛");
    //private TextField typeField = new TextField("社会实践");
    private ComboBox<String> typeField = new ComboBox<>(FXCollections.observableArrayList(
            "社会实践","学科竞赛","科研成果","培训讲座","创新项目","校外实习"
    ));
    private DatePicker timePicker=new DatePicker();
    private TextField locationField = new TextField("软件学院");
    private TextField performanceField = new TextField("good");

    public InnovationManagementPage() {
        this.setWidth(1000);
        initializeTable();
        initializeStudentTable();
        initializeControlPanel();
        displayInnovations();
    }
    private Map newMapFromFields(Map m)
    {
        m.put("studentList",studentListView.getSelectedItems());
        m.put("name",nameField.getText());
        m.put("type",typeField.getValue());
        m.put("startDate",timePicker.getValue()+"");
        m.put("location",locationField.getText());
        m.put("performance",performanceField.getText());
        return m;
    }

    private void displayInnovations() {
        observableList.clear();
        observableList.add(Map.of());
        ArrayList<Map> innovationList=(ArrayList<Map>) request("/getAllInnovations", null).getData();
        //System.out.println(innovationList);
        observableList.addAll(FXCollections.observableArrayList(innovationList));
        innovationTable.setData(observableList);
    }

    private void initializeControlPanel() {
        gridPane.addColumn(0,
                new Label("学生"),
                new Label("项目名称"),
                new Label("项目类型"),
                new Label("时间"),
                new Label("地点"),
                new Label("评价")
                );
        gridPane.addColumn(1,
                studentListView,
                nameField,
                typeField,
                timePicker,
                locationField,
                performanceField
        );
        gridPane.addRow(6, addButton, deleteButton, updateButton);
        addButton.setOnAction(event -> addInnovation());
        deleteButton.setOnAction(event ->deleteInnovation());
        updateButton.setOnAction(event -> updateInnovation());

       innovationTable.setOnItemClick(innovation->{
           if(innovationTable.getSelectedIndex()==0)
           {
               addButton.setDisable(false);
               studentTable.setVisible(false);
                nameField.setText("");
                typeField.setValue("");
                timePicker.setValue(null);
                locationField.setText("");
                performanceField.setText("");
                studentListView.setSelectedItems(List.of());
               return;
           }
           addButton.setDisable(true);
           nameField.setText((String) innovation.get("name"));
           typeField.setValue((String) innovation.get("type"));
           timePicker.setValue(LocalDate.parse((String) innovation.get("startDate")));
           locationField.setText((String) innovation.get("location"));
           performanceField.setText((String) innovation.get("performance"));
           displayStudents(innovation);
           studentTable.setVisible(true);
        });

        controlPanel.getChildren().add(gridPane);
        studentTable.setVisible(false);
        controlPanel.getChildren().add(studentTable);
        this.getItems().add(controlPanel);
    }

    private void displayStudents(Map m) {
        studentObservableList.clear();
        ArrayList<Map> studentlist=(ArrayList<Map>) request("/getStudentsByInnovation",m).getData();
        studentListView.setSelectedItems(studentlist);
        studentObservableList.addAll(FXCollections.observableArrayList(studentlist));
        studentTable.setItems(studentObservableList);
    }

    private void updateInnovation() {
        Map m=innovationTable.getSelectedItem();
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
            DataResponse r=request("/updateInnovation",newMapFromFields(m));
            displayInnovations();
            displayStudents(m);
            if(r.getCode()==0)
            {
                Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                alert1.setContentText("更新成功");
                alert1.showAndWait();
            }
        }
    }


    private void deleteInnovation() {
        if(innovationTable.getSelectedItems().isEmpty())
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("未选择，无法删除");
            alert.showAndWait();
        }
        List<Map> innovationList=new ArrayList<>(innovationTable.getSelectedItems());
        Alert alert=new Alert(Alert.AlertType.CONFIRMATION, "确定要删除吗？");
        alert.setTitle("警告");
        Optional<ButtonType> result=alert.showAndWait();
            if(result.get()==ButtonType.OK)
            {
                DataResponse r=request("/deleteInnovations",innovationList);
                if(r.getCode()==0)
                {
                    Alert alert1=new Alert(Alert.AlertType.INFORMATION);
                    alert1.setContentText("删除成功");
                    alert1.showAndWait();
                }
                displayInnovations();
            }
    }

    private void addInnovation() {
        Map m=newMapFromFields(new HashMap());
        System.out.println(m);
        System.out.println(m.get("studentList"));
        if(m.get("name")==null||m.get("type")==null||m.get("startDate").equals("null")||m.get("performance")==null||m.get("location")==null||
        studentListView.getSelectedItems().isEmpty()||m.get("name").equals("")||m.get("type").equals("")||m.get("startDate").equals("")||m.get("performance").equals("")||m.get("location").equals(""))
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("警告");
            alert.setContentText("请填写完整信息");
            alert.showAndWait();
            studentListView.setSelectedItems(List.of());
            return;
        }
        DataResponse r=request("/addInnovation",m);
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
        displayInnovations();
        studentListView.setSelectedItems(List.of());
    }
    private void initializeStudentTable()
    {
        TableColumn<Map, String> nameColumn = new TableColumn<>("学生姓名");
        TableColumn<Map, String> studentIdColumn = new TableColumn<>("学号");
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        studentIdColumn.setCellValueFactory(new MapValueFactory<>("studentId"));
        studentTable.getColumns().addAll(nameColumn,studentIdColumn);
    }

    private void initializeTable()
    {
        TableColumn<Map, String> nameColumn = new TableColumn<>("项目名称");
        TableColumn<Map, String> typeColumn = new TableColumn<>("项目类型");
        TableColumn<Map, String> timeColumn = new TableColumn<>("时间");
        TableColumn<Map, String> locationColumn = new TableColumn<>("地点");
        TableColumn<Map, String> performanceColumn = new TableColumn<>("评价");
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        typeColumn.setCellValueFactory(new MapValueFactory<>("type"));
        timeColumn.setCellValueFactory(new MapValueFactory<>("startDate"));
        locationColumn.setCellValueFactory(new MapValueFactory<>("location"));
        performanceColumn.setCellValueFactory(new MapValueFactory<>("performance"));

        //搜索使用
        List<TableColumn<Map, ?>> columns = new ArrayList<>();
        columns.add(nameColumn);
        columns.add(typeColumn);
        columns.add(timeColumn);
        columns.add(locationColumn);
        columns.add(performanceColumn);
        innovationTable = new SearchableTableView(observableList, List.of("name","type"), columns);
        this.getItems().add(innovationTable);
    }

}
