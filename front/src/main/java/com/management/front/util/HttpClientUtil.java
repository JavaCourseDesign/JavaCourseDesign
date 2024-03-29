package com.management.front.util;

import com.google.gson.Gson;
import com.management.front.request.DataResponse;
import com.management.front.request.JwtResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpClientUtil {
    static public String mainUrl = "http://localhost:9090";
    static Gson gson = new Gson();
    static private JwtResponse jwt=new JwtResponse();//在老师的示例项目中被存储在appstore

    private static HttpClient client = HttpClient.newHttpClient();
    static public boolean login(String username, String password) throws IOException, URISyntaxException, InterruptedException {
        /*HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(mainUrl+"/login"))
                .POST(HttpRequest.BodyPublishers.ofString("{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}"))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            System.out.println("Login successful");
            System.out.println("Token:"+response.body());
            jwt.setAccessToken(response.body());
            return true;
        } else {
            System.out.println("Login failed, statusCode="+response.statusCode());
            return false;
        }*/
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(mainUrl + "/login"))
                .POST(HttpRequest.BodyPublishers.ofString("{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}"))
                .headers("Content-Type", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            if (response.statusCode() == 200) {
                jwt = gson.fromJson(response.body(), JwtResponse.class);
                return true;
            } else if (response.statusCode() == 401) {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static DataResponse request(String url,Object request){
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(mainUrl + url))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .headers("Content-Type", "application/json")
                .headers("Authorization", "Bearer " + jwt.getAccessToken())
                .build();
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
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


    static public Object sendAndReceiveObject(String url, Object parameter) throws IOException {
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
    }

    static public DataResponse sendAndReceiveDataResponse(String url, Object parameter) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 2. 创建HttpPost实例
        HttpPost httpPost = new HttpPost(mainUrl+url);
        httpPost.setEntity(new StringEntity(gson.toJson(parameter), ContentType.APPLICATION_JSON));

        // 3. 调用HttpClient实例来执行HttpPost实例
        CloseableHttpResponse response = httpclient.execute(httpPost);
        // 4. 读 response
        int status = response.getStatusLine().getStatusCode();
        if(status<200||status>=300) throw new ClientProtocolException("Unexpected response status: " + status);
        HttpEntity entity = response.getEntity();
        String html = EntityUtils.toString(entity);

        // 5. 释放连接
        response.close();
        httpclient.close();

        //System.out.println(html);
        return gson.fromJson(html, DataResponse.class);
    }
}



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
