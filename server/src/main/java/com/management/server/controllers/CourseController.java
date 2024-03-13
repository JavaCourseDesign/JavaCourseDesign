package com.management.server.controllers;

import com.management.server.models.Course;
import com.management.server.models.Person;
import com.management.server.models.Student;
import com.management.server.models.Teacher;
import com.management.server.repositories.CourseRepository;
import com.management.server.repositories.StudentRepository;
import com.management.server.repositories.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("/course")
public class CourseController {
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    TeacherRepository teacherRepository;
    @GetMapping("/demoCourse")
    public String demoCourse(){
        Course c=new Course();
        c.setEventId(1);
        c.setCourseCode("100a");
        c.setName("java");
        c.setCredit(3);
        c.addStudent(studentRepository.findById(1).get());
        c.addTeacher(teacherRepository.findById(2).get());
        //c.addStudent(studentRepository.findByPersonId(1));
        courseRepository.save(c);
        return "HelloCourse"+c.getTeachers().get(0).getName()+" ";
    }

}
