package com.management.front.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.management.front.request.DataResponse;
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
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class HttpClientUtil {
    static public String mainUrl = "http://localhost:9090";
    static Gson gson = new Gson();
    private static HttpClient client = HttpClient.newHttpClient();
    //private DataResponse sendAndReceive(String numName) throws IOException {
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
    public static String login(Object request){

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(mainUrl + "/user/login"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                    .headers("Content-Type", "application/json")
                    .build();
            try {
                HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                System.out.println(response.body()+"checkpoint");
                if (response.statusCode() == 200) {
                    JwtResponse jwt = new JwtResponse(response.body());
                    AppStore.setJwt(jwt);
                    return "登录成功";
                } else if (response.statusCode() == 401) {
                    return "用户名或密码不存在！";
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "登录失败";
    }

    public static DataResponse request(String url,Object request){
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(mainUrl + url))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .headers("Content-Type", "application/json")
                .headers("Authorization", "Bearer " + AppStore.getJwt().getAccessToken())
                .build();
        System.out.println(AppStore.getJwt().getAccessToken());
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                                System.out.println(response.body());
                DataResponse dataResponse = new DataResponse(1,response.body(),null);
                return dataResponse;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }return null;
    }

}
class  AppStore {
    private static JwtResponse jwt;

    private AppStore(){
    }

    public static JwtResponse getJwt() {
        return jwt;
    }

    public static void setJwt(JwtResponse jwt) {
        AppStore.jwt = jwt;
    }
}

class JwtResponse {

    private String accessToken;


    public JwtResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }



}