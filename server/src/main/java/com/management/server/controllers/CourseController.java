package com.management.server.controllers;

import com.management.server.models.Course;
import com.management.server.models.Person;
import com.management.server.models.Student;
import com.management.server.models.Teacher;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.CourseRepository;
import com.management.server.repositories.PersonRepository;
import com.management.server.repositories.StudentRepository;
import com.management.server.repositories.TeacherRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
//@RequestMapping("/course")
public class CourseController {
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    TeacherRepository teacherRepository;
    @Autowired
    PersonRepository personRepository;

    @PostMapping("/getCourse")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getCourse(){
        List<Course> list = courseRepository.findAll();
        ArrayList<Map> courseMapList = new ArrayList<>();
        for (Course course : list) {
            Map<String, Object> m = new HashMap<>();
            m.put("courseId", course.getCourseId());
            m.put("reference", course.getReference());
            m.put("capacity", course.getCapacity());
            m.put("credit", course.getCredit());
            // 获取这个课程的所有教师的名字
            List<String> teacherNames = new ArrayList<>();
            List<Person> persons = personRepository.findPersonsByCourseId(course.getCourseId());
            for (Person person : persons) {
                if (person instanceof Teacher) {
                    teacherNames.add(person.getName());
                }
            }
            m.put("teacherName", teacherNames.get(0));
            courseMapList.add(m);
        }
        DataResponse r = new DataResponse(0, courseMapList, null);
        return r;
    }
    @PostMapping("/addCourse")
    public DataResponse addCourse(@RequestBody Map<String,String> m) {
        /*Teacher t=new Teacher();
        t.setName("张三");
        teacherRepository.save(t);
        List<Course> list=new ArrayList<>();
        Course course=new Course();
        course.setCourseId("1");
        course.setReference("线性代数及其应用");
        course.setCapacity(1);
        course.setCredit(1);
        List<Person> teacherList=new ArrayList<>();
        teacherList.add(t);
        course.setPersons(teacherList);
        courseRepository.save(course);
        list.add(course);
        t.setCourses(list);
        teacherRepository.save(t);
        return new DataResponse(1,null,null);*/
        System.out.println(m);
        Teacher t=new Teacher();
        t.setName(m.get("teacherName"));
        t.setTeacherId(m.get("teacherId"));
        List<Course> courseList=new ArrayList<>();
        Course course=new Course();
        course.setCourseId(m.get("courseId"));
        course.setReference(m.get("reference"));
        course.setCapacity(Integer.parseInt(m.get("capacity")));
        course.setCredit(Integer.parseInt(m.get("credit")));
        courseRepository.save(course);
        courseList.add(course);
        t.setCourses(courseList);
        teacherRepository.save(t);
        return new DataResponse(0,null,"添加成功");
    }
}
