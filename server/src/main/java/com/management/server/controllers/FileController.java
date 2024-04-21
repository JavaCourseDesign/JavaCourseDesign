package com.management.server.controllers;

import com.management.server.payload.response.DataResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
public class FileController {
    @Value("${attach.folder}")    //环境配置变量获取
    private String attachFolder;
    @PostMapping(path = "/uploadFile")
    public DataResponse uploadFile(@RequestBody byte[] barr,
    @RequestParam(name = "fileName") String fileName)  {
        try {
            String decodedFileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8.toString());
            OutputStream os = new FileOutputStream(new File(attachFolder + "homework" +"/"+ decodedFileName));
            os.write(barr);
            os.close();
            return new DataResponse(0, null, "上传成功！");
        }catch(Exception e){
            return new DataResponse(1, null, "上传错误！");
        }
    }

}
