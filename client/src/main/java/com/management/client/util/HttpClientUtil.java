package com.management.client.util;

import com.google.gson.Gson;
import com.management.client.request.DataResponse;
import com.management.client.request.JwtResponse;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.controlsfx.dialog.ProgressDialog;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;

public class HttpClientUtil {
    static public String mainUrl = "http://localhost:9090";
    static Gson gson = new Gson();
    static private JwtResponse jwt=new JwtResponse();
    static public boolean login(String username, String password){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(mainUrl+"/login"))
                    .POST(HttpRequest.BodyPublishers.ofString("{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}"))
                    .header("Content-Type", "application/json")
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (response.statusCode() == 200) {
            jwt.setAccessToken(response.body());
            if(response.body().isEmpty()) return false;
            return true;
        } else {
            return false;
        }
    }

    public static DataResponse request(String url,Object request){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(mainUrl + url))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .headers("Content-Type", "application/json")
                .headers("Authorization", "Bearer " + jwt.getAccessToken())
                .build();
        try {
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            //System.out.println("check"+response.body());
            if (response.statusCode() == 200) {
                DataResponse dataResponse = gson.fromJson(response.body(), DataResponse.class);
                return dataResponse;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void executeTask(String requestPath, Map requestData, Runnable onSuccess) {
        // Create a new task
        Task<DataResponse> task = new Task<>() {
            @Override
            protected DataResponse call() {
                // Execute the request in the task
                return request(requestPath, requestData);
            }
        };

        // Set what to do when the task is successfully completed
        task.setOnSucceeded(e -> {
            DataResponse r = task.getValue();
            if (r != null) {
                showAlert(r);
            }
            onSuccess.run();
        });

        // Set what to do when the task fails
        task.setOnFailed(e -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setContentText("请求失败");
            alert.showAndWait();
        });

        // Show a loading dialog
        ProgressDialog dialog = new ProgressDialog(task);
        dialog.setTitle("正在加载");
        dialog.setHeaderText("请稍等...");

        // Start the task in a new thread
        new Thread(task).start();

        // Show the dialog
        dialog.showAndWait();
    }
    private static void showAlert(DataResponse r) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            if (r.getCode() == -1) {
                alert.setTitle("警告");
            } else {
                alert.setTitle("成功");
            }
            alert.setContentText(r.getMsg());
            alert.showAndWait();
        });
    }

    public static void showAlert(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(msg);
            alert.showAndWait();
        });
    }

    static public DataResponse sendAndReceiveDataResponse(String url, Object parameter) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 2. 创建HttpPost实例
        HttpPost httpPost = new HttpPost(mainUrl+url);
        httpPost.setEntity(new StringEntity(gson.toJson(parameter), ContentType.APPLICATION_JSON));

        // 3. 调用HttpClient实例来执行HttpPost实例
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpPost);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 4. 读 response
        int status = response.getStatusLine().getStatusCode();
        if(status<200||status>=300) try {
            throw new ClientProtocolException("Unexpected response status: " + status);
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        }
        HttpEntity entity = response.getEntity();
        String html = null;
        try {
            html = EntityUtils.toString(entity);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 5. 释放连接
        try {
            response.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            httpclient.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //System.out.println(html);
        return gson.fromJson(html, DataResponse.class);
    }

    public static byte[] requestByteData(String url, Object request){
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(mainUrl + url))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .headers("Content-Type", "application/json")
                .headers("Authorization", "Bearer "+jwt.getAccessToken())
                .build();
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<byte[]>  response = client.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
            if(response.statusCode() == 200) {
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static DataResponse uploadFile(String uri,String filePath,String fileName,String paras)  {
        try {
            Path file = Path.of(filePath);
            HttpClient client = HttpClient.newBuilder().build();
            String encodedFileName = URLEncoder.encode(fileName.toString(), StandardCharsets.UTF_8.toString());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(mainUrl+uri+  "?fileName="
                            + encodedFileName+"&paras="+paras))
                    .POST(HttpRequest.BodyPublishers.ofFile(file))
                    .headers("Authorization", "Bearer "+jwt.getAccessToken())
                    .build();
            HttpResponse<String>  response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                DataResponse dataResponse = gson.fromJson(response.body(), DataResponse.class);
                return dataResponse;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static DataResponse uploadFile(String uri,String filePath,String fileName)  {
        try {
            Path file = Path.of(filePath);
            HttpClient client = HttpClient.newBuilder().build();
            String encodedFileName = URLEncoder.encode(fileName.toString(), StandardCharsets.UTF_8.toString());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(mainUrl+uri+  "?fileName="
                            + encodedFileName))
                    .POST(HttpRequest.BodyPublishers.ofFile(file))
                    .headers("Authorization", "Bearer "+jwt.getAccessToken())
                    .build();
            HttpResponse<String>  response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                DataResponse dataResponse = gson.fromJson(response.body(), DataResponse.class);
                return dataResponse;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static DataResponse importData(String url, String filePath)  {

        try {
            Path file = Path.of(filePath);
            String urlStr = mainUrl+url+"?uploader=HttpTestApp";
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlStr))
                    .POST(HttpRequest.BodyPublishers.ofFile(file))
                    .headers("Authorization", "Bearer "+jwt.getAccessToken())
                    .build();
            HttpResponse<String>  response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                DataResponse dataResponse = gson.fromJson(response.body(), DataResponse.class);
                return dataResponse;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}

/*static public Object sendAndReceiveObject(String url, Object parameter) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        // 2. 创建HttpPost实例
        HttpPost httpPost = new HttpPost(mainUrl+url);
        httpPost.setEntity(new StringEntity(gson.toJson(parameter), ContentType.APPLICATION_JSON));

        // 3. 调用HttpClient实例来执行HttpPost实例
        CloseableHttpResponse response = httpclient.execute(httpPost);
        // 4. 读 response
        int status = response.getStatusLine().getStatusCode();
        //if(status<200||status>=300) throw new ClientProtocolException("Unexpected response status: " + status);
        if(status<200||status>=300) return null;
        HttpEntity entity = response.getEntity();
        String html = EntityUtils.toString(entity);

        // 5. 释放连接
        response.close();
        httpclient.close();

        //System.out.println(html);
        return gson.fromJson(html, Object.class);
    }*/

/*public class HttpClientUtil {
    //private DataResponse sendAndReceive(String numName) throws IOException {
    static public DataResponse sendAndReceive(String url, Object parameter, Type type) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        // 2. 创建HttpPost实例
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity("this is Post"+parameter));

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
        return gson.fromJson(html,type);
    }
}*/

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
