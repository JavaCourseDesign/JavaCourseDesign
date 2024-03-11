package com.management.server.controllers;

import com.management.server.models.Student;
import com.management.server.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class StudentController {
    @Autowired
    private StudentRepository studentRepository;
    @GetMapping("/demoStudent")
    public String demoStudent(){
        Student s=new Student();
        s.setName("tst");
        studentRepository.save(s);
        return "Hello";
    }
}
