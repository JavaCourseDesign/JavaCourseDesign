package com.management.server.controllers;

import com.management.server.models.Teacher;
import com.management.server.payload.request.DataRequest;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.StudentRepository;
import com.management.server.repositories.TeacherRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TeacherController {
    @Autowired
    TeacherRepository teacherRepository;
    @GetMapping("/demoTeacher")
    public String demoTeacher(){
        Teacher t=new Teacher();
        t.setName("wzk");
        t.setDegree("master");
        teacherRepository.save(t);
        return "HelloTeacher";
    }

    @PostMapping("/getTeacherList")
    //@PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTeacherList(/*@Valid @RequestBody DataRequest dataRequest*/) {
        //String numName= dataRequest.getString("numName");
        //List dataList = getTeacherMapList(numName);
        Teacher t=new Teacher();
        t.setName("wzk");
        return new DataResponse(200,t,"hello");  //按照测试框架规范会送Map的list
    }
}
