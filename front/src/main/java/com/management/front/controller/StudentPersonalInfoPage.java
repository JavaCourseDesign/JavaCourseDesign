package com.management.front.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.management.front.customComponents.SearchableTableView;
import com.management.front.request.DataResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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


import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.management.front.util.HttpClientUtil.request;

public class StudentPersonalInfoPage extends TabPane {
    public StudentPersonalInfoPage() {
        Map m = new HashMap();
        m.put("personId", LoginPage.personId);
        Map student = (Map) request("/getStudentByPersonId", m).getData();
        System.out.println(student);
        this.getTabs().add(new BasicInfoTab(student));
        this.getTabs().add(new InnovationTab(student));
    }
}

class BasicInfoTab extends Tab {
    private String fileName;
    Map student;
    VBox vBox = new VBox();
    GridPane gridPane = new GridPane();
    ImageView photoArea = new ImageView();

    public BasicInfoTab(Map student) {
        this.setText("基本信息");
        this.setContent(vBox);
        this.student = student;
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
                    fileName = LoginPage.personId + ".jpg";
                    m.put("fileName", fileName);
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
        printIntroduceButton.setOnMouseClicked(event -> print());
        refresh();
    }

    public void print() {
        Document document = new Document();

        // 创建文件选择器
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择保存位置");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setInitialFileName("resume.pdf");
        FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("PDF 文件 (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(pdfFilter);

        // 显示文件选择对话框
        File outputFile = fileChooser.showSaveDialog(null);

        if (outputFile != null) {
            try {
                // 创建 PDF Writer，并指定输出文件
                PdfWriter.getInstance(document, new FileOutputStream(outputFile));

                // 打开文档
                document.open();

                // 添加个人信息
                Font nameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
                Paragraph nameParagraph = new Paragraph("John Doe", nameFont);
                document.add(nameParagraph);

                Font occupationFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
                Paragraph occupationParagraph = new Paragraph("Software Engineer", occupationFont);
                document.add(occupationParagraph);

                // 添加其他内容

                // 关闭文档
                document.close();

                System.out.println("个人简历 PDF 保存成功。");
            } catch (DocumentException | FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("未选择保存位置。");
        }
    }

    public void display() {
        Map<String, String> m = new HashMap<>();
        m.put("fileName", LoginPage.personId + ".jpg");
        DataResponse r = request("/getPhotoImageStr", m);
        if (r.getCode() == 0) {
            String base64Image = r.getData().toString();
            byte[] decodedBytes = Base64.getDecoder().decode(base64Image);
            ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes);
            Image image = new Image(bis);
            photoArea.setImage(image);
        }
    }

    public void save() {

        //request("/updateStudent", student);
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
        TextField highSchoolField = new TextField(student.get("highSchool")+"");
        TextField familyMemberField = new TextField(student.get("familyMember")+"");
        TextField familyMemberPhoneField = new TextField(student.get("familyMemberPhone")+"");
        TextField addressField = new TextField(student.get("address")+"");
        TextField homeTownField = new TextField(student.get("homeTown")+"");
        TextField phoneField = new TextField(student.get("phone")+"");
        TextField emailField = new TextField(student.get("email")+"");

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

