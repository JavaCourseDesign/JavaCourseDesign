package com.management.server.controllers;

import com.management.server.models.Person;
import com.management.server.models.Student;
/*import com.management.server.repository.PersonRepository;*/
import com.management.server.payload.request.DataRequest;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.StudentRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController

public class StudentController {
    /*@Autowired
    private PersonRepository personRepository;*/
    @Autowired
    private StudentRepository studentRepository;
    @GetMapping ("/demoStudent")
    public String demoStudent(){
        Student s=new Student();
        s.setMajor("software");
        studentRepository.save(s);
        return "HelloStudent";
    }

    @GetMapping("/checkEvents")
    public String checkEvents(){
        Student s=studentRepository.findById(1).get();
        return s.getEvents().toString();
    }

    @PostMapping("/addStudent")
    public DataResponse addStudent(@Valid @RequestBody Map<String,String> data){
        Student student=new Student();
        student.setName(data.get("name"));
        student.setGender(data.get("gender"));
        student.setMajor(data.get("studentId"));
        student.setClassName(data.get("major"));
        studentRepository.save(student);
        DataResponse r=new DataResponse();
        r.setMsg("添加成功"+student.getName()+data.get("name"));
        return r;
    }



}
