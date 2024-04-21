package com.management.front.controller;

import com.management.front.request.DataResponse;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.management.front.util.HttpClientUtil.*;

public class BasicInfoTab extends Tab {
    private Pagination pagination = new Pagination();
    private String fileName;
    private PdfModel model;
    Map student = new HashMap<>();
    VBox vBox = new VBox();
    GridPane gridPane = new GridPane();
    ImageView photoArea = new ImageView();
    private Button uploadButton = new Button("上传照片");
    private TextField highSchoolField = new TextField();
    private TextField familyMemberField = new TextField();
    private TextField familyMemberPhoneField = new TextField();
    private TextField addressField = new TextField();
    private TextField homeTownField = new TextField();
    private TextField phoneField = new TextField();
    private TextField emailField = new TextField();

    public BasicInfoTab(Map student) {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("BasicInfoTab.fxml"));
        fxmlLoader.setController(this);
        try {
            VBox content = fxmlLoader.load();
            this.setContent(content);
            this.setText("基本信息");
        } catch (Exception e) {
            e.printStackTrace(); // 处理加载异常
        }


        this.setText("基本信息");
        this.setContent(vBox);
        this.student = student;
        highSchoolField.setText(student.get("highSchool") + "");
        familyMemberField.setText(student.get("familyMember") + "");
        familyMemberPhoneField.setText(student.get("familyMemberPhone") + "");
        addressField.setText(student.get("address") + "");
        homeTownField.setText(student.get("homeTown") + "");
        phoneField.setText(student.get("phone") + "");
        emailField.setText(student.get("email") + "");
        // vBox.getChildren().add(photoArea);
        vBox.getChildren().add(photoArea);
        vBox.getChildren().add(uploadButton);
        uploadButton.setOnMouseClicked(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                DataResponse r = uploadFile("/uploadPhoto", file.getPath(), file.getName());
                display();
            }
        });

        photoArea.setFitHeight(200);
        photoArea.setFitWidth(200);
        photoArea.setPickOnBounds(true);
        photoArea.setPreserveRatio(true);
        photoArea.setStyle("-fx-border-color: #000000; -fx-border-width: 10;");
        display();

        //给gridpane设置浅灰色横隔
        gridPane.setGridLinesVisible(true);
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
        byte[] data = requestByteData("/getStudentIntroduce", student);
        model = new PdfModel(data);
        pagination.setPageCount(model.numPages());
        pagination.setPageFactory(index -> new ImageView(model.getImage(index)));
        Stage s = new Stage();
        AnchorPane anchorPane = new AnchorPane();
        Scene scene = new Scene(anchorPane);
        s.setScene(scene);
        anchorPane.getChildren().add(pagination);
        Button button = new Button("导出pdf文件");
        button.setOnMouseClicked(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File file = fileChooser.showSaveDialog(s);
            if (file != null) {
                try {
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

    private Map<String, String> newMapFromFields() {
        Map<String, String> m = new HashMap<>();
        m.put("highSchool", highSchoolField.getText());
        m.put("familyMember", familyMemberField.getText());
        m.put("familyMemberPhone", familyMemberPhoneField.getText());
        m.put("address", addressField.getText());
        m.put("homeTown", homeTownField.getText());
        m.put("phone", phoneField.getText());
        m.put("email", emailField.getText());
        return m;
    }

    public void save() {
        DataResponse r = request("/saveStudentPersonalInfo", newMapFromFields());
        if (r.getCode() == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("保存成功");
            alert.showAndWait();
        }
    }

    public void refresh() {
        Label label1 = new Label("毕业高中信息");
        label1.setStyle("-fx-font: 20 arial; -fx-text-fill: blue;");
        Label label2 = new Label("家庭信息");
        label2.setStyle("-fx-font: 20 arial; -fx-text-fill: blue;");
        Label label3 = new Label("校内联系方式");
        label3.setStyle("-fx-font: 20 arial; -fx-text-fill: blue;");
        Text studentIdText = new Text(student.get("studentId") + "");
        Text nameText = new Text(student.get("name") + "");
        Text genderText = new Text(student.get("gender") + "");
        Text majorText = new Text(student.get("major") + "");
        Text deptText = new Text(student.get("dept") + "");
        Text classNameText = new Text(student.get("className") + "");
        Text socilaText = new Text(student.get("social") + "");
        Text cardText = new Text(student.get("idCardNum") + "");
        Text birthdayText = new Text(student.get("birthday") + "");


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
