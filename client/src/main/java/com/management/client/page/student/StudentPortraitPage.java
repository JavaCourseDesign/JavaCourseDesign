package com.management.client.page.student;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfWriter;
import com.management.client.request.DataResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DecimalFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.management.client.util.HttpClientUtil.request;

public class StudentPortraitPage {
    Map student;
    public void setStudent(Map student) {
        this.student = student;
    }
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private BarChart<String,Number> feeBarChart;
    @FXML
    private PieChart pieChart;
    @FXML
    private BarChart<String,Number> scoreBarChart;
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
    private Text competitionText;
    @FXML
    private Text paperText;
    @FXML
    private Text projectText;
    @FXML
    private ImageView photoArea;
    @FXML
    public void initialize(){
        setPhoto();
        Map m = (Map) request("/getStudentPortraitData", null).getData();
        nameText.setText(m.get("name")+"");
        numText.setText(m.get("studentId")+"");
        emailText.setText(m.get("email")+"");
        phoneText.setText(m.get("phone")+"");
        addressText.setText(m.get("address")+"");
        majorText.setText(m.get("major")+"");
        gpaText.setText(m.get("gpa")+"");
        competitionText.setText("曾参与"+m.get("competitionName1")+",荣获"+m.get("competitionPerformance1") );
        paperText.setText("曾在刊物"+m.get("paperLocation1")+"上发表过《"+m.get("paperName1")+"》");
        projectText.setText("曾参加"+m.get("projectName1")+"项目,项目评价为"+m.get("projectPerformance1"));
        DecimalFormat df = new DecimalFormat("#.##");
        String gpa=df.format(Double.parseDouble(m.get("gpa").toString()));
        gpaText.setText("平均学分绩点为"+gpa);
        Map<String, Double> timeMoneyMap = new HashMap<>();
        List<Map> feeList= (List<Map>) m.get("feeList");
        for (Map map : feeList) {
            if (map.get("time") != null && map.get("money") != null) {
                String time = map.get("time").toString();
                Double money = Double.parseDouble(map.get("money").toString());
                // 时间重复把money相加
                if (timeMoneyMap.containsKey(time)) {
                    money += timeMoneyMap.get(time);
                }
                // 将"time"值和对应的"money"值的总和存入Map
                timeMoneyMap.put(time, money);
            }
        }
// 从Map中获取数据来设置BarChart的数据
        XYChart.Series<String, Number> seriesFee = new XYChart.Series<>();
        seriesFee.setName("日常消费");

        for (Map.Entry<String, Double> entry : timeMoneyMap.entrySet()) {
            seriesFee.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        ObservableList<XYChart.Series<String, Number>> barData =
                FXCollections.observableArrayList();
        barData.add(seriesFee);
        feeBarChart.setData(barData);

        List<Map> scoreList = (List<Map>) m.get("scoreList");
        //System.out.println(scoreList);
        XYChart.Series<String, Number> seriesScore = new XYChart.Series<>();
        seriesScore.setName("成绩");
        for (Map map : scoreList) {
            if(map.get("mark")!=null)
            seriesScore.getData().add(new XYChart.Data<>(map.get("courseName").toString(),Double.parseDouble( map.get("mark").toString())));
        }
        ObservableList<XYChart.Series<String,Number>> scoreData =
                FXCollections.observableArrayList();
        scoreData.add(seriesScore);
        scoreBarChart.setData(scoreData);


        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();
        List<Map>markList = (List<Map>)m.get("markList");
        for(Map map:markList) {
            chartData.add(new PieChart.Data(map.get("title").toString(),Double.parseDouble(map.get("value").toString())));
        }
        pieChart.setData(chartData);
    }
    @FXML
    protected void setPhoto(){
        DataResponse r = request("/getPhotoImageStr", null);
        if (r.getCode() == 0) {
            String base64Image = r.getData().toString();
            byte[] decodedBytes = Base64.getDecoder().decode(base64Image);
            ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes);
            javafx.scene.image.Image image = new javafx.scene.image.Image(bis);
            photoArea.setImage(image);
        }
    }
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
