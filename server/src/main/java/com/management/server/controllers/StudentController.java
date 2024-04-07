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
import java.util.Optional;

@RestController

public class StudentController {
    @Autowired
    private StudentRepository studentRepository;

    @PostMapping("/getStudentByPersonId")
    public DataResponse getStudent(@RequestBody Map<String,String> map)
    {
        Student student = studentRepository.findByPersonId(map.get("personId"));
        return new DataResponse(0,student,null);
    }

    @PostMapping("/getAllStudents")
    public DataResponse getAllStudents()
    {
        return new DataResponse(200,studentRepository.findAll(),null);
    }

    @PostMapping("/addStudent")
    public DataResponse addStudent(@RequestBody Map m)
    {
        String studentId = (String) m.get("studentId");
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
        return new DataResponse(0,null,"删除成功");
    }

    @PostMapping("/updateStudent")
    public DataResponse updateStudent(@RequestBody Map m) {
        String personId = (String) m.get("personId");
        Optional<Student> optionalStudent = Optional.ofNullable(studentRepository.findByPersonId(personId));
        if(optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            BeanUtil.fillBeanWithMap(m, student, true, CopyOptions.create());
            studentRepository.save(student);
            return new DataResponse(0, null, "更新成功");
        } else {
            return new DataResponse(-1, null, "学号不存在，无法更新");
        }
    }

}
