package com.management.client.page.teacher;

import com.management.client.ClientApplication;
import com.management.client.request.DataResponse;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.management.client.util.HttpClientUtil.*;

public class TeacherBasicInfoTab extends Tab {
    Map teacher;
    @FXML
    private ImageView photoArea;
    @FXML
    private Label NAME;
    @FXML
    private Label TEACHERID;
    @FXML
    private Label DEPT;
    @FXML
    private Label name;
    @FXML
    private Label teacherId;
    @FXML
    private Label title;
    @FXML
    private TextField phone;
    @FXML
    private TextField email;

    public TeacherBasicInfoTab(Map<String, Object> teacher) {
        this.teacher = teacher;
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("teacherFxml/TeacherBasicInfoTab.fxml"));
        fxmlLoader.setController(this);
        try {
            VBox content = fxmlLoader.load();
            this.setContent(content);
            this.setText("基本信息");
            refresh();
        } catch (IOException e) {
            e.printStackTrace(); // 处理加载异常
        }
    }

    @FXML
    private void initialize() {
        // 初始化操作
        photoArea.setOnMouseClicked(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files",  "*.jpg"));
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                DataResponse r = uploadFile("/uploadPhoto", file.getPath(), file.getName());
                display();
            }
        });
    }

    private Map<String, String> newMapFromFields() {
        Map m = new HashMap<>();
        m.put("phone", phone.getText());
        m.put("email", email.getText());
        return m;
    }

    @FXML
    public void save() {
        DataResponse r = request("/saveTeacherPersonalInfo", newMapFromFields());
        if (r.getCode() == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("保存成功");
            alert.showAndWait();
        }
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

    private void refresh() {
        if (teacher != null) {
            NAME.setText((String) teacher.get("name"));
            TEACHERID.setText((String) teacher.get("teacherId"));
            DEPT.setText((String) teacher.get("dept"));
            name.setText((String) teacher.get("name"));
            teacherId.setText((String) teacher.get("teacherId"));
            title.setText((String) teacher.get("title"));
            phone.setText((String) teacher.get("phone"));
            email.setText((String) teacher.get("email"));
        }
        display();
    }
}