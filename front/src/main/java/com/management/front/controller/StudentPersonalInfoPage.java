package com.management.front.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.management.front.request.DataResponse;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;


import java.io.*;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.management.front.util.HttpClientUtil.request;

public class StudentPersonalInfoPage extends TabPane {
    public StudentPersonalInfoPage() {
        Map m=new HashMap();
        m.put("personId",LoginPage.personId);
        Map student =(Map)request("/getStudentByPersonId",m).getData();
       this.getTabs().add(new BasicInfoTab(student));
    }
}
class BasicInfoTab extends Tab {
    private String fileName;
    Map student=new HashMap<>();
    VBox vBox=new VBox();
    GridPane gridPane=new GridPane();
    ImageView photoArea=new ImageView();
    public BasicInfoTab(Map student) {
        this.setText("基本信息");
        this.setContent(vBox);
        this.student=student;
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
                    Map<String,String> m=new HashMap<>();
                    fileName=LoginPage.personId+".jpg";
                    m.put("fileName", fileName);
                    m.put("fileContent", encodedString);
                    DataResponse r = request("/uploadPhoto", m);
                    if(r.getCode() == 0) {
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
        Button printIntroduceButton=new Button("打印个人简历");
        vBox.getChildren().add(printIntroduceButton);
        printIntroduceButton.setOnMouseClicked(event ->print());
        refresh();
    }
    public void print() {
        Document document=new Document();

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
    public void display()
    {
        Map<String,String> m=new HashMap<>();
        m.put("fileName",LoginPage.personId+".jpg");
        DataResponse r=request("/getPhotoImageStr",m);
        if(r.getCode()==0)
        {
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
            Text studentIdText = new Text(student.get("studentId").toString());
           // studentIdText.setStyle("-fx-font: 20 arial; -fx-fill: blue;");

            Text nameText = new Text(student.get("name").toString());
          //  nameText.setStyle("-fx-font: 20 arial; -fx-fill: blue;");

            Text genderText = new Text(student.get("gender").toString());
          //  genderText.setStyle("-fx-font: 20 arial; -fx-fill: blue;");

            Text majorText = new Text(student.get("major").toString());
          //  majorText.setStyle("-fx-font: 20 arial; -fx-fill: blue;");

            gridPane.addColumn(0,
                    new Label("学号:"),
                    new Label("姓名:"),
                    new Label("性别:"),
                    new Label("专业:"),
                    new Label("学院:"),

                    new Label("身份证号:"),
                    new Label("出生日期:")
                    );
            gridPane.addColumn(1,
                    studentIdText,
                    nameText,
                    genderText,
                    majorText);
    }
}
