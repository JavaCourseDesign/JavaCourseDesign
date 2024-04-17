package com.management.server.controllers;

import com.management.server.payload.response.DataResponse;
import com.management.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Base64;
import java.util.Map;
@RestController
public class PhotoController {

    @Value("${attach.folder}")
    private String attachFolder;
    @PostMapping("/getPhotoImageStr")
    public DataResponse getPhotoImageStr() {
        String fileName = CommonMethod.getUsername()+".jpg";
        String str = "";
        try {
            File file = new File(attachFolder +"Photo/"+ fileName);
            if(!file.exists())
            {
                return new DataResponse(1,null,"请先上传文件！");
            }
            int len = (int) file.length();
            byte data[] = new byte[len];
            FileInputStream in = new FileInputStream(file);
            in.read(data);
            in.close();
            String imgStr = new String(Base64.getEncoder().encode(data));
            return new DataResponse(0, imgStr, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new DataResponse(1,null,"下载错误！");
    }
    @PostMapping("/uploadPhoto")
    public DataResponse uploadPhoto(@RequestBody Map<String, String> map) {
        String fileContent = map.get("fileContent");
        try {
            String fileName= CommonMethod.getUsername()+".jpg";
            // Decode the Base64 string to a byte array
            byte[] data = Base64.getDecoder().decode(fileContent);
            OutputStream os = new FileOutputStream(new File(attachFolder +"Photo/"+fileName ));
            os.write(data);
            os.close();
            return new DataResponse(0, "上传成功", null);
        } catch (IOException e) {
            e.printStackTrace();
            return new DataResponse(1, null, "上传错误！");
        }
    }
}
