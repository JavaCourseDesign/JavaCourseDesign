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

import static com.management.front.util.HttpClientUtil.sendAndReceive;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() throws IOException{
        /*DataRequest req=new DataRequest();
        DataResponse res= HttpRequestUtil.request("/demoStudent",req);
        welcomeText.setText(res.getData().toString());*/
        //welcomeText.setText(demoStudent());
        //sendPostRequest("123");
        DataResponse r=new DataResponse();
        r.setMsg("123");
        System.out.println(r.getMsg());
        Gson gson = new Gson();
        System.out.println(gson.toJson(r));
        welcomeText.setText("success3"+sendAndReceive("http://localhost:9090/getTeacherList",r).getData());
    }

    /*private DataResponse sendAndReceive(String numName) throws IOException{
        CloseableHttpClient httpclient = HttpClients.createDefault();

        // 2. 创建HttpPost实例
        HttpPost httpPost = new HttpPost("http://localhost:9090/getTeacherList");
        httpPost.setEntity(new StringEntity("this is Post"+numName));

        // 3. 调用HttpClient实例来执行HttpPost实例
        CloseableHttpResponse response = httpclient.execute(httpPost);
        // 4. 读 response
        int status = response.getStatusLine().getStatusCode();
        if(status<200||status>=300) throw new ClientProtocolException("Unexpected response status: " + status);
        HttpEntity entity = response.getEntity();
        System.out.println(response);
        System.out.println("===================");
        String html = EntityUtils.toString(entity);
        System.out.println("html:"+html);

        // 5. 释放连接
        response.close();
        httpclient.close();

        Gson gson = new Gson();
        return gson.fromJson(html,DataResponse.class);
    }*/

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


    private void sendPostRequest(String numName) {
        try {
            URL url = new URL("http://localhost:9090/getTeacherList");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            String jsonInputString = "{\"numName\": \"" + numName + "\"}";

            try(java.io.OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try(java.io.BufferedReader br = new java.io.BufferedReader(
                    new java.io.InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("success1 "+response.toString());
            }

            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}