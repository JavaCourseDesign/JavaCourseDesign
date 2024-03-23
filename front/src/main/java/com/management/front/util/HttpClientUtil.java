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
import java.lang.reflect.Type;

public class HttpClientUtil  {
    static public String mainUrl = "http://localhost:9090";
    //private DataResponse sendAndReceive(String numName) throws IOException {
    static public DataResponse sendAndReceive(String url, Object parameter) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        Gson gson = new Gson();
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


        return gson.fromJson(html,DataResponse.class);
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
