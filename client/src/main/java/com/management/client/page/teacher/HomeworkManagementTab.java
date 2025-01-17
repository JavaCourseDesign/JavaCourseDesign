package com.management.client.page.teacher;

import com.management.client.customComponents.SearchableTableView;
import com.management.client.page.PdfModel;
import com.management.client.request.DataResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.controlsfx.control.tableview2.FilteredTableColumn;

import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.management.client.util.HttpClientUtil.request;

public class HomeworkManagementTab extends Tab {
    private SearchableTableView homeworkTable;
    private VBox controlPanel = new VBox();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();
    private GridPane gridPane;
    private Label homeworkContentLabel = new Label("作业内容");
    private Label deadlineLabel = new Label("截止时间");
    private TextField homeworkContentField = new TextField();
    private DatePicker deadlinePicker = new DatePicker();
    private Button markButton = new Button("批改学生作业");
    private Button addButton = new Button("增加");
    private Button deleteButton = new Button("删除");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private PdfModel model;
    private Pagination pagination=new Pagination();
    private Map course;
    private SplitPane splitPane = new SplitPane();

    public HomeworkManagementTab(Map course) {
        this.course = course;
        this.setText("作业管理");
        this.setContent(splitPane);
        initializeTable();
        initializeControlPanel();
        displayHomeworks();
    }
    private Map newMapFromFields(Map m) {
        m.put("homeworkContent", homeworkContentField.getText());
        m.put("deadline", deadlinePicker.getValue()+"");
        m.put("courseId", course.get("courseId"));
        return m;
    }

    private void displayHomeworks() {
        observableList.clear();
        DataResponse r=request("/getTeacherHomework",course);
        ArrayList<Map> list=(ArrayList<Map>)r.getData();
        for(Map m:list)
        {
            if(m.get("student")!=null)
            {
                Map student=(Map) m.get("student");
                m.put("studentName",student.get("name"));
                m.put("studentId",student.get("studentId"));
            }
            m.put("courseName",course.get("name"));
        }
        observableList.addAll(FXCollections.observableArrayList(list));
        homeworkTable.setData(observableList);
    }

    private void initializeControlPanel() {
        controlPanel.setMinWidth(100);
        controlPanel.setSpacing(10);
        gridPane=new GridPane();
        gridPane.addColumn(0,homeworkContentLabel,deadlineLabel);
        gridPane.addColumn(1,homeworkContentField,deadlinePicker);
        controlPanel.getChildren().add(gridPane);
        controlPanel.getChildren().add(addButton);
        controlPanel.getChildren().add(deleteButton);
        controlPanel.getChildren().add(markButton);
        controlPanel.getChildren().add(new Pane());
        addButton.setOnMouseClicked(event -> {
            addHomework();
        });
        deleteButton.setOnMouseClicked(event -> {
            deleteHomework();
        });
        markButton.setOnMouseClicked(event -> {
            markHomework();
        });
        splitPane.getItems().add(controlPanel);
    }
    private void markHomework()
    {
        if(homeworkTable.getSelectedItems().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("请选择要批改的作业");
            alert.showAndWait();
        }
        else
        {
            Map m=homeworkTable.getSelectedItem();
            DataResponse r=request("/getHomeworkFile",m);
            if(r.getCode()!=0)
            {
                Alert alert=new Alert(Alert.AlertType.ERROR);
                alert.setContentText(r.getMsg());
                alert.showAndWait();
                return;
            }
            byte[] data= Base64.getDecoder().decode(r.getData().toString());
            model=new PdfModel(data);
            pagination.setPageCount(model.numPages());
            pagination.setPageFactory(index -> new ImageView(model.getImage(index)));
            ScrollPane scrollPane=new ScrollPane();
            scrollPane.setContent(pagination);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            HBox hbox = new HBox();
            hbox.getChildren().add(new Label("按下按钮评分："));
            for (int i = 0; i < 5; i++) {
                Button button = new Button((char)('A'+i)+"");
                hbox.getChildren().add(button);
                button.setOnMouseClicked(event -> {
                    m.put("grade",button.getText());
                    request("/markHomework",m);
                    controlPanel.getChildren().set(4,new Pane());
                    displayHomeworks();
                    // 关闭 ScrollPane
                });
            }
// 创建 VBox 并添加 Pagination 和 HBox
            VBox vbox = new VBox();
            vbox.getChildren().addAll(pagination, hbox);

// 创建 ScrollPane 并设置其内容为 VBox
            scrollPane.setContent(vbox);
            controlPanel.getChildren().set(4,scrollPane);
        }
    }


    private void deleteHomework() {
        if(homeworkTable.getSelectedItems().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("请选择要删除的作业");
            alert.showAndWait();
            return;
        }
        Map m=new HashMap();
        List<Map> list=new ArrayList<>(homeworkTable.getSelectedItems());
        DataResponse r=request("/deleteHomework",list);
        if(r.getCode()!=0)
        {
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setContentText(r.getMsg());
            alert.showAndWait();
        }
        else
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("删除成功");
            alert.showAndWait();
        }
        displayHomeworks();
    }

    private void addHomework() {
        Map m=newMapFromFields(new HashMap());
        if(m.get("homeworkContent")==null || m.get("deadline")==null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("请填写完整信息");
            alert.showAndWait();
            return;
        }
        DataResponse r=request("/addHomework",m);
        if(r.getCode()!=0)
        {
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setContentText(r.getMsg());
            return;
        }
        else
        {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("添加成功");
        }
        displayHomeworks();
    }

    private void initializeTable() {
        FilteredTableColumn<Map, String> studentNameColumn = new FilteredTableColumn<>("学生姓名");
        FilteredTableColumn<Map, String> studentIdColumn = new FilteredTableColumn<>("学生学号");
        FilteredTableColumn<Map,String> courseNameColumn= new FilteredTableColumn<>("课程名称");
        FilteredTableColumn<Map,String> homeworkContentColumn= new FilteredTableColumn<>("作业内容");
        FilteredTableColumn<Map,String> deadlineColumn= new FilteredTableColumn<>("截止时间");
        FilteredTableColumn<Map,String> submitTimeColumn= new FilteredTableColumn<>("提交时间");
        FilteredTableColumn<Map,String> gradeColumn= new FilteredTableColumn<>("成绩");

        studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        studentIdColumn.setCellValueFactory(new MapValueFactory<>("studentId"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        homeworkContentColumn.setCellValueFactory(new MapValueFactory<>("homeworkContent"));
        deadlineColumn.setCellValueFactory(new MapValueFactory<>("deadline"));
        submitTimeColumn.setCellValueFactory(new MapValueFactory<>("submitTime"));
        gradeColumn.setCellValueFactory(new MapValueFactory<>("grade"));
        List<FilteredTableColumn<Map, ?>> columns = new ArrayList<>();
        columns.add(studentNameColumn);
        columns.add(studentIdColumn);
        columns.add(courseNameColumn);
        columns.add(homeworkContentColumn);
        columns.add(deadlineColumn);
        columns.add(submitTimeColumn);
        columns.add(gradeColumn);
        homeworkTable=new SearchableTableView(observableList,List.of("studentId","courseName","grade","deadline","studentName"),columns);
        splitPane.getItems().add(homeworkTable);
    }
}
