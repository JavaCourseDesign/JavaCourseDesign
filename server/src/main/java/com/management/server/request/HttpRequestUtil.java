package com.management.server.request;

import com.google.gson.Gson;
import com.management.client.javafxclient.AppStore;
import com.teach.javafxclient.util.CommonMethod;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

public class HttpRequestUtil {
    public static boolean isLocal = false;
    private static Gson gson = new Gson();
    private static HttpClient client = HttpClient.newHttpClient();
    public static String serverUrl = "http://localhost:9090";

    public static void close(){
        if(isLocal)
            SQLiteJDBC.getInstance().close();
    }

    public static String login(LoginRequest request){
        if(isLocal) {
            return SQLiteJDBC.getInstance().login(request.getUsername(),request.getPassword());
        }else {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl + "/api/auth/login"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                    .headers("Content-Type", "application/json")
                    .build();
            try {
                HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                            System.out.println(response.body());
                if (response.statusCode() == 200) {
                    JwtResponse jwt = gson.fromJson(response.body(), JwtResponse.class);
                    AppStore.setJwt(jwt);
                    return null;
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
    }

    public static DataResponse request(String url,DataRequest request){
        if(isLocal) {
            int index = url.lastIndexOf('/');
            String methodName = url.substring(index+1,url.length());
            try {
                Method method = SQLiteJDBC.class.getMethod(methodName, DataRequest.class);
                return (DataResponse)method.invoke(SQLiteJDBC.getInstance(), request);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }else {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl + url))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                    .headers("Content-Type", "application/json")
                    .headers("Authorization", "Bearer " + AppStore.getJwt().getAccessToken())
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            try {
                HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    //                System.out.println(response.body());
                    DataResponse dataResponse = gson.fromJson(response.body(), DataResponse.class);
                    return dataResponse;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static MyTreeNode requestTreeNode(String url, DataRequest request){
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + url))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .headers("Content-Type", "application/json")
                .headers("Authorization", "Bearer "+AppStore.getJwt().getAccessToken())
                .build();
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String>  response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                return gson.fromJson(response.body(), MyTreeNode.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<OptionItem> requestOptionItemList(String url, DataRequest request){
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + url))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .headers("Content-Type", "application/json")
                .headers("Authorization", "Bearer "+AppStore.getJwt().getAccessToken())
                .build();
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String>  response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                OptionItemList o = gson.fromJson(response.body(), OptionItemList.class);
                if(o != null)
                return o.getItemList();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static  List<OptionItem> getDictionaryOptionItemList(String code) {
        DataRequest req = new DataRequest();
        req.put("code", code);
        return requestOptionItemList("/api/base/getDictionaryOptionItemList",req);
    }

    public static byte[] requestByteData(String url, DataRequest request){
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + url))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .headers("Content-Type", "application/json")
                .headers("Authorization", "Bearer "+AppStore.getJwt().getAccessToken())
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

    public static DataResponse uploadFile(String fileName,String remoteFile)  {
        try {
            Path file = Path.of(fileName);
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl+"/api/base/uploadFile?uploader=HttpTestApp&remoteFile="+remoteFile + "&fileName="
                            + file.getFileName()))
                    .POST(HttpRequest.BodyPublishers.ofFile(file))
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

    public static DataResponse importData(String url, String fileName, String paras)  {
        try {
            Path file = Path.of(fileName);
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl+url+"?uploader=HttpTestApp&fileName="
                            + file.getFileName() + "&"+paras))
                    .POST(HttpRequest.BodyPublishers.ofFile(file))
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

    public static int uploadHtmlString(String html)  {
            DataRequest req = new DataRequest();
            String str = new String(Base64.getEncoder().encode(html.getBytes(StandardCharsets.UTF_8)));
            req.put("html", str);
            DataResponse res =request("/api/base/uploadHtmlString",req);
            return CommonMethod.getIntegerFromObject(res.getData());
    }

}
