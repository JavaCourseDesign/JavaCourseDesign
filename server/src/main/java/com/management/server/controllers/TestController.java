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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TestController {//专门用于添加测试数据
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private CourseRepository courseRepository;
    @PostMapping("/test/addTestData")
    public DataResponse addTestData(){
        Student s1=new Student();
        s1.setStudentId("2019210000");
        s1.setName("tst");
        s1.setGender("男");
        s1.setMajor("软件工程");
        s1.setClassName("软工1班");
        studentRepository.save(s1);

        Student s2=new Student();
        s2.setStudentId("2019210001");
        s2.setName("wzk");
        s2.setGender("男");
        s2.setMajor("软件工程");
        s2.setClassName("软工2班");
        studentRepository.save(s2);

        Teacher t1=new Teacher();
        t1.setTeacherId("100000");
        t1.setName("why");
        teacherRepository.save(t1);

        Course c1=new Course();
        c1.setCourseId("100000");
        c1.setName("软件工程");
        List<Person> persons=new ArrayList<>();
        persons.add(s1);
        persons.add(s2);
        persons.add(t1);
        c1.setPersons(persons);
        studentRepository.save(s1);
        studentRepository.save(s2);
        teacherRepository.save(t1);
        courseRepository.save(c1);
        return new DataResponse(0,null,"测试数据添加成功");
    }
}
