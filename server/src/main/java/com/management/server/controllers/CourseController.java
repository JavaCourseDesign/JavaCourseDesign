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
    @GetMapping("/demoCourse")
    public String demoCourse(){
        Course c=new Course();
        c.setEventId(1);
        c.setCourseCode("100a");
        c.setName("java");
        c.setCredit(3);
        List<Student> studentList=new ArrayList<>();
        studentList.add(studentRepository.findByStudentId(1));
        studentList.add(studentRepository.findByStudentId(2));
        List<Teacher> teacherList=new ArrayList<>();
        teacherList.add(teacherRepository.findByPersonId(3));
        teacherList.add(teacherRepository.findByPersonId(4));
        c.setStudents(studentList);
        c.setTeachers(teacherList);
        //c.addStudent(studentRepository.findByPersonId(1));
        courseRepository.save(c);
        return "HelloCourse";
    }

}
