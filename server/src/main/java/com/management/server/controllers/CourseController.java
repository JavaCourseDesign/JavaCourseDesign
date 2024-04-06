package com.management.server.controllers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.management.server.models.Course;
import com.management.server.models.Person;
import com.management.server.models.Student;
import com.management.server.models.Teacher;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.CourseRepository;
import com.management.server.repositories.PersonRepository;
import com.management.server.repositories.StudentRepository;
import com.management.server.repositories.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    @PostMapping("/getAllCourses")
    public DataResponse getAllCourses(){
        return new DataResponse(0,courseRepository.findAll(),null);
    }

    @PostMapping("/addCourse")
    public DataResponse addCourse(@RequestBody Map m){
        String courseId = (String) m.get("courseId");
        if(courseRepository.existsByCourseId(courseId)) {
            return new DataResponse(-1,null,"课程已存在，无法添加");
        }
        Course course = BeanUtil.mapToBean(m, Course.class, true, CopyOptions.create());//要求map键值与对象一致
        courseRepository.save(course);
        return new DataResponse(0,null,"添加成功");
    }

    @PostMapping("/deleteCourse")
    public DataResponse deleteCourse(@RequestBody Map m){
        courseRepository.deleteAllByCourseId(""+m.get("courseId"));
        return new DataResponse(0,null,"删除成功");
    }

   /* @PostMapping("/updateCourse")
    public DataResponse updateCourse(@RequestBody Map m) {
        courseRepository.deleteAllByCourseId((""+ m.get("courseId")).split("\\.")[0]);
        if(m.containsKey("newPersonId")) m.put("persons", teacherRepository.findByPersonId(Integer.parseInt((""+ m.get("newPersonId")).split("\\.")[0])));
        addCourse(m);
        System.out.println("\n"+m+"\n");
        return new DataResponse(0, null, "更新成功");
    }*/
}
