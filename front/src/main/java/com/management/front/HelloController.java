package com.management.front;

import com.google.gson.Gson;
import com.management.front.model.Greeting;
import com.management.front.request.DataResponse;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;



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
import org.w3c.dom.Document;
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

}