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
import java.util.Optional;

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

    @PostMapping("/updateCourse")
    public DataResponse updateCourse(@RequestBody Map m) {
        String courseId = (String) m.get("courseId");
        Optional<Course> optionalCourse = Optional.ofNullable(courseRepository.findByCourseId(courseId));
        if(optionalCourse.isPresent()) {
            Course course = optionalCourse.get();

            //需要把多对多关系属性忽略掉，student与teacher中亦然，因为这些属性经过传输以及不再具有完整的循环嵌套特征，需要通过逐渐重新建立联系
            m.remove("persons");
            System.out.println(m);

            BeanUtil.fillBeanWithMap(m, course, true, CopyOptions.create());
            courseRepository.save(course);
            return new DataResponse(0, null, "更新成功");
        } else {
            return new DataResponse(-1, null, "课序号不存在，无法更新");
        }
    }

    @PostMapping("/updateCourse/person")
    public DataResponse updateCoursePerson(@RequestBody Map m){
        String courseId = (String) m.get("courseId");
        String personId = (String) m.get("personId");
        if(!courseRepository.existsByCourseId(courseId)) {
            return new DataResponse(-1,null,"课程不存在，无法更新");
        }
        if(!personRepository.existsByPersonId(personId)) {
            return new DataResponse(-1,null,"人员不存在，无法更新");
        }
        Course course = courseRepository.findById(courseId).get();
        Person person = personRepository.findById(personId).get();
        if(course.getPersons().contains(person))
        {
            course.getPersons().remove(person);
            courseRepository.save(course);
            return new DataResponse(0,null,"已经添加过了,故移除");
        }
        course.getPersons().add(person);
        courseRepository.save(course);
        return new DataResponse(0,null,"更新成功");
        //还需要添加根据班级添加学生的功能
    }
}
