package com.management.server.controllers;

import com.management.server.models.Course;
import com.management.server.models.Person;
import com.management.server.models.Student;
import com.management.server.models.Teacher;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.CourseRepository;
import com.management.server.repositories.StudentRepository;
import com.management.server.repositories.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
//@RequestMapping("/course")
public class CourseController {
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    TeacherRepository teacherRepository;
    @PostMapping("/getAllCourses")
    public DataResponse getAllCourses(){
        return new DataResponse(0,courseRepository.findAll(),null);
    }

}
