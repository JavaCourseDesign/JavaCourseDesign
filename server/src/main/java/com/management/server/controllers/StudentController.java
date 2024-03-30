package com.management.server.controllers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.management.server.models.Student;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController

public class StudentController {
    @Autowired
    private StudentRepository studentRepository;

    @PostMapping("/getAllStudents")
    public DataResponse getAllStudents()
    {
        return new DataResponse(200,studentRepository.findAll(),null);
    }

    @PostMapping("/addStudent")
    public DataResponse addStudent(@RequestBody Map<String,String> m)
    {
        String studentId = m.get("studentId");
        if(studentRepository.existsByStudentId(studentId)) {
            return new DataResponse(-1,null,"学号已存在，无法添加");
        }
        Student student = BeanUtil.mapToBean(m, Student.class, true, CopyOptions.create());//要求map键值与对象一致
        studentRepository.save(student);
        return new DataResponse(0,null,"添加成功");
    }
    @PostMapping("/deleteStudent")
    public DataResponse deleteStudent(@RequestBody Map m)
    {
        studentRepository.deleteAllByStudentId(""+m.get("studentId"));
        return new DataResponse(0,null," ");
    }

    @PostMapping("/updateStudent")
    public DataResponse updateStudent(@RequestBody Map m) {
        studentRepository.deleteAllByPersonId(Integer.parseInt((""+ m.get("personId")).split("\\.")[0]));
        addStudent(m);
        return new DataResponse(0, null, " ");
    }
}
