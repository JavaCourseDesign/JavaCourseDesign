package com.management.client.page.admin;

import com.management.client.ClientApplication;
import com.management.client.request.DataResponse;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

import static com.management.client.util.HttpClientUtil.executeTask;
import static com.management.client.util.HttpClientUtil.request;

public class StudentInfoPane{

    private TabPane root;
    private StudentManagementPage parent;

    @FXML
    private ImageView photoArea;
    @FXML
    private TextField name;
    @FXML
    private TextField studentId;
    @FXML
    private TextField idCardNum;
    @FXML
    private Label gender;
    @FXML
    private Label birthday;
    @FXML
    private Label homeTown;
    @FXML
    private TextField dept;
    @FXML
    private TextField major;
    @FXML
    private ComboBox<String> clazz;
    @FXML
    private ComboBox<String> social;
    @FXML
    private TextField highSchool;
    @FXML
    private Label phone;
    @FXML
    private Label email;
    @FXML
    private Label address;
    /*@FXML
    private Label other;*/

    @FXML
    private TableView<Map> family;
    @FXML
    private TableColumn familyRelationship;
    @FXML
    private TableColumn familyName;
    @FXML
    private TableColumn familyAge;
    @FXML
    private TableColumn familyPhone;

    @FXML
    private TableView honorTable;
    @FXML
    private TableColumn<Map, String> awardDateColumn;
    @FXML
    private TableColumn<Map, String> nameColumn;
    @FXML
    private TableColumn<Map, String> departmentColumn;
    @FXML
    private TableColumn<Map, String> eventColumn;

    @FXML
    private Label avgMark;
    @FXML
    private Label gpa;
    @FXML
    private TableView scoreTable;
    @FXML
    private TableColumn<Map, String> courseIdColumn;
    @FXML
    private TableColumn<Map, String> courseNameColumn;
    @FXML
    private TableColumn<Map, String> gradeColumn;
    @FXML
    private TableColumn<Map, String> gradePointColumn;


    private Map<String, Object> student;

    public StudentInfoPane(StudentManagementPage parent) {

        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("/adminFxml/StudentInfoPane.fxml"));
        fxmlLoader.setController(this);

        this.parent = parent;

        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Map> clazzList = (List<Map>) request("/getAllClazz", null).getData();
        List<String> clazzNameList = new ArrayList<>();
        for (Map clazz : clazzList) {
            clazzNameList.add(clazz.get("name")+"");
        }
        clazz.getItems().addAll(clazzNameList);
        social.getItems().addAll("群众", "共青团员", "共产党员");
    }

    public TabPane getRoot() {
        return root;
    }

    @FXML
    //添加与更新合并为保存
    private void save() {
        Map<String, Object> student = new HashMap<>();
        if(this.student!=null)student.put("personId", this.student.get("personId"));
        student.put("name", name.getText());
        student.put("studentId", studentId.getText());
        student.put("idCardNum", idCardNum.getText());
        student.put("dept", dept.getText());
        student.put("major", major.getText());
        student.put("clazzName", clazz.getValue());
        student.put("social", social.getValue());
        student.put("highSchool", highSchool.getText());

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "确定要更新吗？");
        alert.setTitle("警告");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            executeTask("/updateStudent", student, this::refresh);
        }
    }

    @FXML
    private void clear()
    {
        student = Map.of();
        refresh();
    }

    @FXML
    private void delete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "确定要删除吗？");
        alert.setTitle("警告");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            executeTask("/deleteStudent", student, parent::displayStudents);
        }
        clear();
    }

    public void setStudent(Map<String, Object> student) {
        this.student = student;
        refresh();
    }

    public void refresh() {
        DataResponse response = request("/getStudentByPersonId", student);
        if(response!=null) {
            student = (Map<String, Object>) response.getData();
        }
        //student = (Map<String, Object>) request("/getStudentByPersonId", student).getData();
        String base64Image = student.get("photo")==null?"":student.get("photo").toString();
        byte[] decodedBytes = Base64.getDecoder().decode(base64Image);
        ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes);
        Image image = new Image(bis);
        photoArea.setImage(image);

        name.setText(student.get("name")==null?"":student.get("name").toString());
        studentId.setText(student.get("studentId")==null?"":student.get("studentId").toString());
        idCardNum.setText(student.get("idCardNum")==null?"":student.get("idCardNum").toString());
        gender.setText(student.get("gender")==null?"":student.get("gender").toString());
        birthday.setText(student.get("birthday")==null?"":student.get("birthday").toString());
        homeTown.setText(student.get("homeTown")==null?"":student.get("homeTown").toString());
        dept.setText(student.get("dept")==null?"":student.get("dept").toString());
        major.setText(student.get("major")==null?"":student.get("major").toString());

        clazz.setValue(student.get("clazzName")==null?"":student.get("clazzName").toString());

        social.setValue(student.get("social")==null?"":student.get("social").toString());
        highSchool.setText(student.get("highSchool")==null?"":student.get("highSchool").toString());
        phone.setText(student.get("phone")==null?"":student.get("phone").toString());
        email.setText(student.get("email")==null?"":student.get("email").toString());
        address.setText(student.get("address")==null?"":student.get("address").toString());
        //other.setText(student.get("other")==null?"":student.get("other").toString());
        ObservableList<Map> familyItems = FXCollections.observableArrayList();
        List<Map> familyList = (List<Map>) student.get("families");
        if (student.get("families") != null) {
            familyItems = FXCollections.observableArrayList(familyList);
        }
        family.setItems(familyItems);
        familyRelationship.setCellValueFactory(new MapValueFactory<>("relationship"));
        familyName.setCellValueFactory(new MapValueFactory<>("name"));
        familyAge.setCellValueFactory(new MapValueFactory<>("birthday"));
        familyPhone.setCellValueFactory(new MapValueFactory<>("phone"));
        ObservableList<Map> honorItems = FXCollections.observableArrayList();
        List<Map> honorList = (List<Map>) student.get("honors");
        if (student.get("honors") != null) {
            honorItems = FXCollections.observableArrayList(honorList);
        }
        honorTable.setItems(honorItems);
        awardDateColumn.setCellValueFactory(new MapValueFactory<>("awardDate"));
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        departmentColumn.setCellValueFactory(new MapValueFactory<>("department"));
        eventColumn.setCellValueFactory(data->
        {
            if(data.getValue().isEmpty()) return new SimpleStringProperty("");
            Map<String,Object> event=(Map<String,Object>) data.getValue().get("event");
            String name=(String) event.get("name");
            return new SimpleStringProperty(name);
        });
        ObservableList<Map> scoreItems = FXCollections.observableArrayList();
        List<Map> scores = (List<Map>) student.get("scores");
        if(scores!=null)
        {
            scoreItems = FXCollections.observableArrayList(scores);
        }

        avgMark.setText(student.get("avgMark")==null?"":student.get("avgMark").toString());
        gpa.setText(student.get("gpa")==null?"":student.get("gpa").toString());

        scoreTable.setItems(scoreItems);
        //System.out.println(scoreItems);
        courseIdColumn.setCellValueFactory(data -> {
            if(data.getValue().isEmpty()) return new SimpleStringProperty("");
            String courseId = (String) data.getValue().get("courseId");
            return new SimpleStringProperty("sdu"+String.format("%06d",Integer.parseInt(courseId)));
        });
        courseNameColumn.setCellValueFactory(
            new MapValueFactory<>("courseName")
        );
        gradeColumn.setCellValueFactory(new MapValueFactory<>("mark"));
        gradePointColumn.setCellValueFactory(data -> {
            Map<String, Object> row = data.getValue();
            if(row.get("mark")==null) return new SimpleStringProperty("");
            double mark = Double.parseDouble(row.get("mark").toString());
            double gradePoint = mark<60?0:1+(mark-60)/10;
            return new SimpleStringProperty(gradePoint+"");
        });


        parent.displayStudents();
    }
}
