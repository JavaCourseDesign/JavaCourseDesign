package com.management.server.controllers;

import com.management.server.models.Teacher;
import com.management.server.repositories.StudentRepository;
import com.management.server.repositories.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
