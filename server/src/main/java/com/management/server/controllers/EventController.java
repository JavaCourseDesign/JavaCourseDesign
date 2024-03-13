package com.management.server.controllers;

import com.management.server.models.Event;
import com.management.server.models.Student;
import com.management.server.repositories.EventRepository;
import com.management.server.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
//@RequestMapping("/course")
public class EventController {
    @Autowired
    EventRepository eventRepository;
    @Autowired
    StudentRepository studentRepository;
    @GetMapping("/demoCourse")
    public String demoCourse(){
        Event c=new Event();
        c.setName("java");
        List<Student> studentList=new ArrayList<>();
        studentList.add(studentRepository.findById(1).get());
        //c.addStudent(studentRepository.findByPersonId(1));
        c.setStudents(studentList);
        eventRepository.save(c);
        return "HelloCourse";
    }

}
