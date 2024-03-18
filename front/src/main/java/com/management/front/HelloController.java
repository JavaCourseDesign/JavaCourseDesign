package com.management.front;

import com.google.gson.Gson;
import com.management.front.model.Greeting;
import com.management.front.request.DataResponse;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        /*DataRequest req=new DataRequest();
        DataResponse res= HttpRequestUtil.request("/demoStudent",req);
        welcomeText.setText(res.getData().toString());*/
        //welcomeText.setText(demoStudent());
        welcomeText.setText(demoTeacher().getData().toString());
    }

    // a demo to get a string from server and create a new student
    private String demoStudent() {
        try {
            URL url = new URL("http://localhost:9090/demoStudent");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            return content.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error fetching data";
        }
    }

    private Greeting getGreetingFromServer() {
        try {
            URL url = new URL("http://localhost:9090/greeting");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            // 将JSON字符串转换为Greeting对象
            Gson gson = new Gson();
            return gson.fromJson(content.toString(), Greeting.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private DataResponse demoTeacher() {
        try {
            URL url = new URL("http://localhost:9090/getTeacherList");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            // 将JSON字符串转换为Greeting对象
            Gson gson = new Gson();
            return gson.fromJson(content.toString(),DataResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}