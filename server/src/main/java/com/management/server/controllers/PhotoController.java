package com.management.server.controllers;

import com.management.server.models.Student;
import com.management.server.models.Teacher;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.StudentRepository;
import com.management.server.repositories.TeacherRepository;
import com.management.server.service.UserDetailsImpl;
import com.management.server.util.CommonMethod;
import com.management.server.util.FileUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
@RestController
public class PhotoController {

    @Value("${attach.folder}")
    private String attachFolder;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    TeacherRepository teacherRepository;
    @PostMapping("/getPhotoImageStr")
    public DataResponse getPhotoImageStr() {
        String fileName = "";
        if(CommonMethod.getUserType().equals("ROLE_STUDENT"))
        {
            Student s=studentRepository.findByStudentId(CommonMethod.getUsername());
            fileName=s.getPhoto();
        }
        else if(CommonMethod.getUserType().equals("ROLE_TEACHER"))
        {
            Teacher t= teacherRepository.findByTeacherId(CommonMethod.getUsername());
            fileName=t.getPhoto();
        }
        try {
            if(fileName==null||fileName.equals(""))
            {
                return new DataResponse(1,null,"请先上传文件！");
            }
            fileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8.toString());
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
    public DataResponse uploadPhoto(@RequestBody byte[] barr,
                                   @RequestParam(name = "fileName") String fileName)  {
        String EuserType= CommonMethod.getUserType();
        if(EuserType.equals("ROLE_STUDENT"))
        {
            Student s=studentRepository.findByStudentId(CommonMethod.getUsername());
            s.setPhoto(fileName);
            studentRepository.save(s);
        }
        else if(EuserType.equals("ROLE_TEACHER"))
        {
           Teacher t= teacherRepository.findByTeacherId(CommonMethod.getUsername());
           t.setPhoto(fileName);
           teacherRepository.save(t);
        }
        DataResponse r=  FileUtil.uploadFile(barr,"Photo",fileName);
        return r;
    }
}
