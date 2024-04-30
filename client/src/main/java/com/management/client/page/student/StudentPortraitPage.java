package com.management.client.page.student;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Map;

public class StudentPortraitPage {
    Map student;
    public void setStudent(Map student) {
        this.student = student;
    }
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private BarChart<String,Number> barChart;
    @FXML
    private PieChart pieChart;
    @FXML
    private AreaChart<String,Number> areaChart;
    @FXML
    private Text nameText;
    @FXML
    private Text numText;
    @FXML
    private Text emailText;
    @FXML
    private Text phoneText;
    @FXML
    private Text addressText;
    @FXML
    private Text majorText;
    @FXML
    private Text gpaText;
    @FXML
    private Text innovationText1;
    @FXML
    private Text innovationText2;
    @FXML
    private Text innovationText3;
    @FXML
    private ImageView photo;



    @FXML
    protected void downLoad() {
        try {
            // 1. 使用snapshot方法获取Node的截图
            WritableImage snapshot = anchorPane.snapshot(new SnapshotParameters(), null);

            // 2. 将WritableImage转换为BufferedImage
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);

            // 3. 创建一个FileChooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("保存个人画像");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File file = fileChooser.showSaveDialog(anchorPane.getScene().getWindow());
            if (file != null) {
                // 4. 创建一个PdfDocument和PdfWriter
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                // 5. 使用ImageIO将截图保存为PDF
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "png", baos);
                Image image = Image.getInstance(baos.toByteArray());
                document.add(image);

                // 6. 关闭文档
                document.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
