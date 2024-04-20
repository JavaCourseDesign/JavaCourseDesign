package com.management.server.util;

import com.management.server.payload.response.DataResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
}
