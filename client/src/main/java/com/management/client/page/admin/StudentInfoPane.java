package com.management.client.page.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.management.client.util.HttpClientUtil.request;

public class StudentInfoPane {
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
    private Label clazz; //待修改
    @FXML
    private TextField social;
    @FXML
    private Label phone;
    @FXML
    private Label email;
    @FXML
    private Label other;

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



    private final Map<String, Object> student;

    public StudentInfoPane(Map student) {
        this.student = (Map<String, Object>) request("/getStudent", student.get("studentId")).getData();
        refresh();
    }

    @FXML
    //添加与更新合并为保存
    private void save() {
        student.put("name", name.getText());
        student.put("studentId", studentId.getText());
        student.put("idCardNum", idCardNum.getText());
        student.put("dept", dept.getText());
        student.put("major", major.getText());
        //clazz待修改
        student.put("social", social.getText());
        request("/updateStudent", student);
    }

    @FXML
    private void delete() {
        request("/deleteStudent", student);
    }

    private void refresh() {
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
        social.setText(student.get("social")==null?"":student.get("social").toString());
        phone.setText(student.get("phone")==null?"":student.get("phone").toString());
        email.setText(student.get("email")==null?"":student.get("email").toString());
        other.setText(student.get("other")==null?"":student.get("other").toString());

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
    }
}
