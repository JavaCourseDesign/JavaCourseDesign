package com.management.server.util;

import com.management.server.payload.response.DataResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Component
public class FileUtil {
    private static String attachFolder;

    @Value("${attach.folder}")    //环境配置变量获取
    public void setAttachFolder(String attachFolder) {
        FileUtil.attachFolder = attachFolder;
    }
    public static DataResponse uploadFile(byte[] barr, String remoteFile,String fileName)  {
        //remoteFile   你想存在的后端的文件夹，不要加/，直接写名字
        try {
            String decodedFileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8.toString());
            System.out.println(attachFolder +remoteFile+"/"+ decodedFileName);
            OutputStream os = new FileOutputStream(new File(attachFolder +remoteFile+"/"+ decodedFileName));
            os.write(barr);
            os.close();
            return new DataResponse(0, null, "上传成功！");
        }catch(Exception e){
            return new DataResponse(1, null, "上传错误！");
        }
    }
    public static DataResponse downloadFile(String remoteFile,String fileName){
        //remoteFile   你想存在的后端的文件夹，不要加/，直接写名字
        try {
            String decodedFileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8.toString());
            File file = new File(attachFolder +remoteFile+"/"+ decodedFileName);
            FileInputStream in = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            in.read(data);
            in.close();
            String fileStr = new String(Base64.getEncoder().encode(data));
            if(file.exists()){
                return new DataResponse(0,fileStr, "下载成功！");
            }else{
                return new DataResponse(1, null, "文件不存在！");
            }
        }catch(Exception e){
            return new DataResponse(1, null, "下载错误！");
        }
    }
    public static void deleteFile(String directory, String fileName) {
        String decoded;
        try {
            decoded=URLDecoder.decode(fileName, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        Path filePath = Paths.get(attachFolder+directory+"/"+decoded);
        try {
            Files.delete(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
