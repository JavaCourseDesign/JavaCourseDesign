package com.management.front;

import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DecompressingHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
//import sun.plugin.dom.html.HTMLDocument;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    private TextField name;

    @FXML
    private TextField gender;

    @FXML
    private TextField studentId;

    @FXML
    private TextField major;

    @FXML
    protected void addStudent() throws IOException{
        Map<String, String> student=new HashMap<>();
        student.put("name",name.getText());
        student.put("gender",gender.getText());
        student.put("studentId",studentId.getText());
        student.put("major",major.getText());
        Gson gson = new Gson();
        System.out.println(gson.toJson(student));
    }
}