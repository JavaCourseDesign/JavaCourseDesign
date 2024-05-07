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

public class TeacherInfoPane {
    private TabPane root;
    private TeacherManagementPage parent;

    @FXML
    private ImageView photoArea;
    @FXML
    private TextField name;
    @FXML
    private TextField teacherId;
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
    private TextField degree;
    @FXML
    private Label clazz; //待修改
    @FXML
    private TextField social;
    @FXML
    private TextField title;
    @FXML
    private Label phone;
    @FXML
    private Label email;
    @FXML
    private Label other;

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

    private Map<String, Object> teacher;

    public TeacherInfoPane(TeacherManagementPage parent) {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("/adminFxml/TeacherInfoPane.fxml"));
        fxmlLoader.setController(this);

        this.parent = parent;

        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public TabPane getRoot() {
        return root;
    }

    @FXML
    //添加与更新合并为保存
    private void save() {
        Map<String, Object> teacher = new HashMap<>();
        teacher.put("personId", this.teacher.get("personId"));
        teacher.put("name", name.getText());
        teacher.put("teacherId", teacherId.getText());
        teacher.put("idCardNum", idCardNum.getText());
        teacher.put("dept", dept.getText());
        teacher.put("degree", degree.getText());
        //clazz待修改
        teacher.put("social", social.getText());
        teacher.put("title", title.getText());

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "确定要更新吗？");
        alert.setTitle("警告");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            executeTask("/updateTeacher", teacher, this::refresh);
        }
    }

    @FXML
    private void delete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "确定要删除吗？");
        alert.setTitle("警告");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            executeTask("/deleteTeacher", teacher, parent::displayTeachers);
        }
        clear();
    }

    public void setTeacher(Map<String, Object> teacher) {
        this.teacher = teacher;
        refresh();
    }

    public void refresh() {
        DataResponse response = request("/getTeacherByPersonId", teacher);
        if(response!=null) {
            teacher = (Map<String, Object>) response.getData();
        }
        //teacher = (Map<String, Object>) request("/getTeacherByPersonId", teacher).getData();
        String base64Image = teacher.get("photo")==null?"":teacher.get("photo").toString();
        byte[] decodedBytes = Base64.getDecoder().decode(base64Image);
        ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes);
        Image image = new Image(bis);
        photoArea.setImage(image);

        name.setText(teacher.get("name")==null?"":teacher.get("name").toString());
        teacherId.setText(teacher.get("teacherId")==null?"":teacher.get("teacherId").toString());
        idCardNum.setText(teacher.get("idCardNum")==null?"":teacher.get("idCardNum").toString());
        gender.setText(teacher.get("gender")==null?"":teacher.get("gender").toString());
        birthday.setText(teacher.get("birthday")==null?"":teacher.get("birthday").toString());
        homeTown.setText(teacher.get("homeTown")==null?"":teacher.get("homeTown").toString());
        dept.setText(teacher.get("dept")==null?"":teacher.get("dept").toString());
        degree.setText(teacher.get("degree")==null?"":teacher.get("degree").toString());
        social.setText(teacher.get("social")==null?"":teacher.get("social").toString());
        title.setText(teacher.get("title")==null?"":teacher.get("title").toString());
        phone.setText(teacher.get("phone")==null?"":teacher.get("phone").toString());
        email.setText(teacher.get("email")==null?"":teacher.get("email").toString());
        other.setText(teacher.get("other")==null?"":teacher.get("other").toString());

        ObservableList<Map> honorItems = FXCollections.observableArrayList();
        List<Map> honorList = (List<Map>) teacher.get("honors");
        if (teacher.get("honors") != null) {
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


        parent.displayTeachers();
    }

    public void clear() {
        teacher = Map.of();
        refresh();
    }
}
