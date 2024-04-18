package com.management.front.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.management.front.customComponents.SearchableListView;
import com.management.front.customComponents.SearchableTableView;
import com.management.front.request.DataResponse;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.management.front.util.HttpClientUtil.request;
import static com.management.front.util.HttpClientUtil.requestByteData;

public class StudentPersonalInfoPage extends TabPane {
    public StudentPersonalInfoPage() {
        Map student = (Map) request("/getStudent", null).getData();
        //System.out.println(student);
        this.getStylesheets().add("dark-theme.css");
        this.getTabs().add(new BasicInfoTab(student));
        this.getTabs().add(new InnovationTab(student));
        this.getTabs().add(new absenceTab(student));
    }
}

class BasicInfoTab extends Tab {
    private Pagination pagination=new Pagination();
    private String fileName;
    private PdfModel model;
    Map student = new HashMap<>();
    VBox vBox = new VBox();
    GridPane gridPane = new GridPane();
    ImageView photoArea = new ImageView();
   private TextField highSchoolField = new TextField();
   private TextField familyMemberField = new TextField();
   private TextField familyMemberPhoneField = new TextField();
   private TextField addressField = new TextField();
   private TextField homeTownField = new TextField();
   private TextField phoneField = new TextField();
   private TextField emailField = new TextField();

    public BasicInfoTab(Map student) {
        this.setText("基本信息");
        this.setContent(vBox);
        this.student = student;
        highSchoolField.setText(student.get("highSchool")+"");
        familyMemberField.setText(student.get("familyMember")+"");
        familyMemberPhoneField.setText(student.get("familyMemberPhone")+"");
        addressField.setText(student.get("address")+"");
        homeTownField.setText(student.get("homeTown")+"");
        phoneField.setText(student.get("phone")+"");
        emailField.setText(student.get("email")+"");
        // vBox.getChildren().add(photoArea);
        vBox.getChildren().add(photoArea);

        photoArea.setFitHeight(200);
        photoArea.setFitWidth(200);
        photoArea.setPickOnBounds(true);
        photoArea.setPreserveRatio(true);
        photoArea.setStyle("-fx-border-color: #000000; -fx-border-width: 10;");
        display();
        photoArea.setOnDragOver(event -> {
            if (event.getGestureSource() != photoArea && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        photoArea.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                try {
                    File file = db.getFiles().get(0);
                    byte[] fileContent = Files.readAllBytes(file.toPath());
                    String encodedString = Base64.getEncoder().encodeToString(fileContent);
                    Map<String, String> m = new HashMap<>();
                    //m.put("fileName", fileName);
                    m.put("fileContent", encodedString);
                    DataResponse r = request("/uploadPhoto", m);
                    if (r.getCode() == 0) {
                        // handle success
                    } else {
                        // handle error
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
            display();
        });

        vBox.getChildren().add(gridPane);
        Button saveButton = new Button("保存");
        saveButton.setOnMouseClicked(event -> save());
        vBox.getChildren().add(saveButton);
        Button printIntroduceButton = new Button("打印个人简历");
        vBox.getChildren().add(printIntroduceButton);
        printIntroduceButton.setOnMouseClicked(event -> showPdf());
        refresh();
    }

    public void showPdf() {
        //System.out.println(requestByteData("/getStudentIntroduce",student));
        byte[] data= requestByteData("/getStudentIntroduce",student);
        model = new PdfModel(data);
        pagination.setPageCount(model.numPages());
        pagination.setPageFactory(index -> new ImageView(model.getImage(index)));
        Stage s=new Stage();
        AnchorPane anchorPane=new AnchorPane();
        Scene scene=new Scene(anchorPane);
        s.setScene(scene);
        anchorPane.getChildren().add(pagination);
        Button button=new Button("导出pdf文件");
        button.setOnMouseClicked(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File file = fileChooser.showSaveDialog(s);
            if (file != null) {
                try  {
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(data);
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        anchorPane.getChildren().add(button);
        s.show();
    }

    public void display() {
        DataResponse r = request("/getPhotoImageStr", null);
        if (r.getCode() == 0) {
            String base64Image = r.getData().toString();
            byte[] decodedBytes = Base64.getDecoder().decode(base64Image);
            ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes);
            Image image = new Image(bis);
            photoArea.setImage(image);
        }
    }
    private Map<String,String> newMapFromFields()
    {
        Map<String,String> m=new HashMap<>();
        m.put("highSchool",highSchoolField.getText());
        m.put("familyMember",familyMemberField.getText());
        m.put("familyMemberPhone",familyMemberPhoneField.getText());
        m.put("address",addressField.getText());
        m.put("homeTown",homeTownField.getText());
        m.put("phone",phoneField.getText());
        m.put("email",emailField.getText());
        return m;
    }

    public void save() {
        DataResponse r=request("/saveStudentPersonalInfo", newMapFromFields());
        if(r.getCode()==0)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("保存成功");
            alert.showAndWait();
        }
    }
    public void refresh() {
        Label label1 = new Label("毕业高中信息");
        label1.setStyle("-fx-font: 20 arial; -fx-text-fill: blue;");
        Label label2=new Label("家庭信息");
        label2.setStyle("-fx-font: 20 arial; -fx-text-fill: blue;");
        Label label3=new Label("校内联系方式");
        label3.setStyle("-fx-font: 20 arial; -fx-text-fill: blue;");
        Text studentIdText = new Text(student.get("studentId")+"");
        Text nameText= new Text(student.get("name")+"");
        Text genderText = new Text(student.get("gender")+"");
        Text majorText = new Text(student.get("major")+"");
        Text deptText= new Text(student.get("dept")+"");
        Text classNameText=new Text(student.get("className")+"");
        Text socilaText=new Text(student.get("social")+"");
        Text cardText=new Text(student.get("idCardNum")+"");
        Text birthdayText=new Text(student.get("birthday")+"");


        gridPane.addColumn(0,
                new Label("学号:"),
                new Label("姓名:"),
                new Label("性别:"),
                new Label("专业:"),
                new Label("学院:"),
                new Label("班级:"),
                new Label("政治面貌:"),
                new Label("身份证号:"),
                new Label("出生日期:"),
                label1,
                new Separator(),
                new Label("毕业高中:"),
                label2,
                new Separator(),
                new Label("家庭联系人:"),
                new Label("家庭联系电话:"),
                new Label("家庭住址:"),
                new Label("家庭籍贯:"),
                label3,
                new Separator(),
                new Label("电话:"),
                new Label("email:")
        );
        gridPane.addColumn(1,
                studentIdText,
                nameText,
                genderText,
                majorText,
                deptText,
                classNameText,
                socilaText,
                cardText,
                birthdayText,
                new Text(),
                new Separator(),
                highSchoolField,
                new Text(),
                new Separator(),
                familyMemberField,
                familyMemberPhoneField,
                addressField,
                homeTownField,
                new Text(),
                new Separator(),
                phoneField,
                emailField);
    }
}
class InnovationTab extends Tab {
    private SearchableTableView innovationTable;
    private SplitPane anchorPane=new SplitPane();
    private VBox controlPanel = new VBox();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    Map m = new HashMap();
    public InnovationTab(Map student) {
        m=student;
        this.setText("创新实践信息管理");
        this.setContent(anchorPane);
        initializeTable();
        displayInnovations();
    }
    private void displayInnovations() {
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getInnovationsByStudent", m).getData()));
        innovationTable.setData(observableList);
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
        timeColumn.setCellValueFactory(new MapValueFactory<>("time"));
        locationColumn.setCellValueFactory(new MapValueFactory<>("location"));
        performanceColumn.setCellValueFactory(new MapValueFactory<>("performance"));


        List<TableColumn<Map, ?>> columns = new ArrayList<>();
        columns.add(nameColumn);
        columns.add(typeColumn);
        columns.add(timeColumn);
        columns.add(locationColumn);
        columns.add(performanceColumn);
        innovationTable = new SearchableTableView(observableList, List.of("name","type"), columns);
        anchorPane.getItems().add(innovationTable);

        /*innovationTable.setOnItemClick(item -> {
            //System.out.println("Selected item: " + item);
        });*/
    }
}
class absenceTab extends Tab {

    private SearchableTableView absenceTable;
    private SplitPane splitPane=new SplitPane();
    private  VBox controlPanel=new VBox();
    private ObservableList<Map> observableList= FXCollections.observableArrayList();


    private TextField idField = new TextField("201921000");
    private TextField offReasonField=new TextField("玩原神");
    private TextField destinationField=new TextField("宿舍");
    private TextField nameField = new TextField("wzk");
    Map s;
    private SearchableListView eventListView;
    public absenceTab(Map student) {
        s=student;
        eventListView=new SearchableListView(FXCollections.observableArrayList((ArrayList) request("/getEventsByStudent",s).getData()),List.of("name","time"));
        this.setText("请假信息管理");
        this.setContent(splitPane);
        initializeTable();
        initializeControlPanel();
        displayAbsences();
    }
    private Map newMapFromFields(Map m) {
        m.put("id", idField.getText());
        m.put("name", nameField.getText());
        m.put("offReason",offReasonField.getText());
        m.put("destination",destinationField.getText());
        m.put("events",eventListView.getSelectedItems());
        return m;
    }
    private void displayAbsences() {
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList((ArrayList) request("/getAbsencesByStudent", s).getData()));
        absenceTable.setData(observableList);
        System.out.println(observableList);
    }

    private void initializeControlPanel() {
        controlPanel.setMinWidth(200);
        controlPanel.setSpacing(10);
        controlPanel.setAlignment(Pos.CENTER);
        Text text = new Text("请假信息表单");
        controlPanel.getChildren().add(text);
        controlPanel.getChildren().add(new Text("学号:"));
        controlPanel.getChildren().add(idField);
        controlPanel.getChildren().add(new Text("姓名:"));
        controlPanel.getChildren().add(nameField);
        controlPanel.getChildren().add(new Text("请假原因:"));
        controlPanel.getChildren().add(offReasonField);
        controlPanel.getChildren().add(new Text("请假去向:"));
        controlPanel.getChildren().add(destinationField);
        controlPanel.getChildren().add(new Text("请假事件:"));
        controlPanel.getChildren().add(eventListView);
        Button uploadButton = new Button("提交");
        uploadButton.setOnMouseClicked(e->{
            uploadAbsence();
        });
        Button deleteButton=new Button("删除");
        deleteButton.setPrefWidth(100);
        deleteButton.setPrefHeight(100);
        deleteButton.setOnMouseClicked(e->
        {

        });
        controlPanel.getChildren().addAll(uploadButton,deleteButton);
        splitPane.getItems().add(controlPanel);
    }

    private void initializeTable() {
        TableColumn<Map,String> personColumn= new TableColumn<>("学生姓名");
        TableColumn<Map,String> offReasonColumn= new TableColumn<>("请假原因");
        TableColumn<Map,String> timeColumn= new TableColumn<>("请假时间");
        TableColumn<Map,String> destinationColumn= new TableColumn<>("请假去向");
        TableColumn<Map,String> statusColumn= new TableColumn<>("状态");
        personColumn.setCellValueFactory(data->{
            if(data.getValue()!=null)
            {
                Map<String,Object> person=(Map<String,Object>) data.getValue().get("person");
                String name=(String) person.get("name");
                return new SimpleStringProperty(name);
            }
            else return new SimpleStringProperty("");
        });
        offReasonColumn.setCellValueFactory(new MapValueFactory<>("offReason"));
        timeColumn.setCellValueFactory(data->
        {
            if(data.getValue()!=null) {
                Map<String, Object> person = (Map<String, Object>) data.getValue().get("event");
                String time = (String) person.get("time");
                return new SimpleStringProperty(time);
            }
            else return new SimpleStringProperty("");
        });
        destinationColumn.setCellValueFactory(new MapValueFactory<>("destination"));
        statusColumn.setCellValueFactory(data->{
            if(data.getValue()==null)
                return new SimpleStringProperty("");
            Boolean status=(Boolean) data.getValue().get("isApproved");
            if(status==null)
                return new SimpleStringProperty("未审批");
            else if(status)
                return new SimpleStringProperty("已通过");
            else
                return new SimpleStringProperty("未通过");
        });
        List<TableColumn<Map, ?>> columns = new ArrayList<>();
        columns.add(personColumn);
        columns.add(offReasonColumn);
        columns.add(timeColumn);
        columns.add(destinationColumn);
        columns.add(statusColumn);
        absenceTable = new SearchableTableView(observableList,List.of("name","isApproved"),columns);
        splitPane.getItems().add(absenceTable);
    }
    private void uploadAbsence()
    {
        Map m1 = newMapFromFields(new HashMap());
        List<Map> list=(List<Map>)m1.get("events");
        if(list.isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("请填写完整信息");
            alert.showAndWait();
            return;
        }
        for(int i=0;i<list.size();i++)
        {
            Map map=newMapFromFields(new HashMap());
            if(map.get("offReason")==null||map.get("destination")==null||map.get("name")==null||map.get("id")==null)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("请填写完整信息");
                alert.showAndWait();
                return;
            }
            map.remove("events");
            map.put("eventId",list.get(i).get("eventId"));
            DataResponse r=request("/uploadAbsence", map);
            if(r.getCode()==1) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("上传失败,"+r.getMsg());
                alert.showAndWait();
                return;
            }
        }
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("上传成功");
        displayAbsences();
        eventListView.setSelectedItems(List.of());
    }

}

