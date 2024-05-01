package com.management.client.page.student;

import com.management.client.ClientApplication;
import com.management.client.page.PdfModel;
import com.management.client.request.DataResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.management.client.util.HttpClientUtil.*;

public class StudentBasicInfoTab extends Tab {
    Map student;
    private final Pagination pagination = new Pagination();
    private PdfModel model;
    //VBox vBox = new VBox();
    @FXML
    private ImageView photoArea;
    @FXML
    private Label NAME;
    @FXML
    private Label STUDENTID;
    @FXML
    private Label MAJOR;
    @FXML
    private Label name;
    @FXML
    private Label studentId;
    @FXML
    private Label major;
    @FXML
    private Label idCardNum;
    @FXML
    private Label gender;
    @FXML
    private Label birthday;
    @FXML
    private Label dept;
    @FXML
    private Label clazz;
    @FXML
    private Label social;
    @FXML
    private TextField phone;
    @FXML
    private TextField email;
    @FXML
    private TextField other;
    @FXML
    private TableView family;
    @FXML
    private TableColumn familyRelationship;
    @FXML
    private TableColumn familyName;
    @FXML
    private TableColumn familyAge;
    @FXML
    private TableColumn familyPhone;


    public StudentBasicInfoTab(Map<String, Object> student) {
        this.student = student;
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("/studentFxml/StudentBasicInfoTab.fxml")); // 确保路径正确
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
    private void showPortrait() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("/studentFxml/StudentPortrait.fxml"));
        Scene scene=new Scene(fxmlLoader.load());
        Stage stage=new Stage();
        stage.setScene(scene);
        stage.show();
        StudentPortraitPage controller = fxmlLoader.getController();
        controller.setStudent(student);
    }

    @FXML
    private void initialize() {
        photoArea.setOnMouseClicked(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files",  "*.jpg"));
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                DataResponse r = uploadFile("/uploadPhoto", file.getPath(), file.getName());
                display();
            }
        });

        familyRelationship.setCellValueFactory(new MapValueFactory<>("relationship"));
        familyName.setCellValueFactory(new MapValueFactory<>("name"));
        familyAge.setCellValueFactory(new MapValueFactory<>("birthday"));
        familyPhone.setCellValueFactory(new MapValueFactory<>("phone"));

        familyRelationship.setCellFactory(TextFieldTableCell.forTableColumn());
        familyName.setCellFactory(TextFieldTableCell.forTableColumn());
        familyAge.setCellFactory(TextFieldTableCell.forTableColumn());
        familyPhone.setCellFactory(TextFieldTableCell.forTableColumn());

        familyRelationship.setOnEditCommit(
                (EventHandler<TableColumn.CellEditEvent<Map, String>>) t -> (t.getTableView().getItems().get(t.getTablePosition().getRow())).put("relationship", t.getNewValue())
        );
        familyName.setOnEditCommit(
                (EventHandler<TableColumn.CellEditEvent<Map, String>>) t -> (t.getTableView().getItems().get(t.getTablePosition().getRow())).put("name", t.getNewValue())
        );
        familyAge.setOnEditCommit(
                (EventHandler<TableColumn.CellEditEvent<Map, String>>) t -> (t.getTableView().getItems().get(t.getTablePosition().getRow())).put("birthday", t.getNewValue())
        );
        familyPhone.setOnEditCommit(
                (EventHandler<TableColumn.CellEditEvent<Map, String>>) t -> (t.getTableView().getItems().get(t.getTablePosition().getRow())).put("phone", t.getNewValue())
        );


    }

    @FXML
    public void showPdf() {
        byte[] data=requestByteData("/getStudentIntroduce",null);
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
        Map m = new HashMap<>();
        m.put("phone", phone.getText());
        m.put("email", email.getText());
        m.put("other", other.getText());
        //存入家庭成员信息
        ObservableList<Map> familyItems = family.getItems();
        List<Map> familyList = familyItems;
        m.put("families", familyList);
        System.out.println(m);
        return m;
    }

    @FXML
    public void save() {
        DataResponse r = request("/saveStudentPersonalInfo", newMapFromFields());
        if (r.getCode() == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("保存成功");
            alert.showAndWait();
        }
        refresh();
    }

    private void refresh() {
        if (student != null) {
            NAME.setText((String) student.get("name"));
            STUDENTID.setText((String) student.get("studentId"));
            MAJOR.setText((String) student.get("major"));
            name.setText((String) student.get("name"));
            studentId.setText((String) student.get("studentId"));
            major.setText((String) student.get("major"));
            idCardNum.setText((String) student.get("idCardNum"));
            gender.setText((String) student.get("gender"));
            birthday.setText((String) student.get("birthday"));
            dept.setText((String) student.get("dept"));
            // 假设student Map中的键为className
            clazz.setText((String) student.get("clazzName"));
            social.setText((String) student.get("social"));
            phone.setText((String) student.get("phone"));
            email.setText((String) student.get("email"));
            // 假设有other键
            other.setText((String) student.get("other"));

            ObservableList<Map> familyItems = FXCollections.observableArrayList();
            List<Map> familyList = (List<Map>) student.get("families");
            if (student.get("families") != null) {
                familyItems = FXCollections.observableArrayList(familyList);
            }
            familyItems.add(new HashMap<>());
            family.setItems(familyItems);
        }
        display();
    }
}
