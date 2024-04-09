package com.management.server.controllers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.management.server.models.*;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
    LessonRepository lessonRepository;
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
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse addCourse(@RequestBody Map m){
        String courseId = (String) m.get("courseId");
        if(courseRepository.existsByCourseId(courseId)) {
            return new DataResponse(-1,null,"课程已存在，无法添加");
        }
        Course course = BeanUtil.mapToBean(m, Course.class, true, CopyOptions.create());//要求map键值与对象一致

        List<Person> persons = new ArrayList<>();
        for (int i = 0; i < ((ArrayList)m.get("personIds")).size(); i++) {
            persons.add(personRepository.findByPersonId((((Map)((ArrayList)m.get("personIds")).get(i)).get("personId")).toString()));
        }
        course.setPersons(persons);

        List<String> times = (List<String>) m.get("lessonTimes");
        List<Lesson> lessons = new ArrayList<>();
        for (int i = 0; i < times.size(); i++) {
            Lesson lesson = new Lesson();
            lesson.setTime(times.get(i));
            lessons.add(lesson);
            lessonRepository.save(lesson);
        }
        course.setLessons(lessons);

        courseRepository.save(course);

        System.out.println(course);
        System.out.println(course.getLessons().get(0).getTime());

        return new DataResponse(0,null,"添加成功");
    }

    @PostMapping("/deleteCourse")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse deleteCourse(@RequestBody Map m){
        courseRepository.deleteAllByCourseId(""+m.get("courseId"));
        return new DataResponse(0,null,"删除成功");
    }

    @PostMapping("/updateCourse")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse updateCourse(@RequestBody Map m){
        String courseId = (String) m.get("courseId");
        if(!courseRepository.existsByCourseId(courseId)) {
            return new DataResponse(-1,null,"课程不存在，无法更新");
        }
        Course course = courseRepository.findByCourseId(courseId);

        //需要把多对多关系属性忽略掉，student与teacher中亦然，因为这些属性经过传输以及不再具有完整的循环嵌套特征，需要通过主键重新建立联系
        m.remove("persons");
        //System.out.println(m);

        BeanUtil.fillBeanWithMap(m, course, true, CopyOptions.create());//要求map键值与对象一致

        List<Person> persons = new ArrayList<>();
        for (int i = 0; i < ((ArrayList)m.get("personIds")).size(); i++) {
            persons.add(personRepository.findByPersonId((((Map)((ArrayList)m.get("personIds")).get(i)).get("personId")).toString()));
        }
        course.setPersons(persons);

        List<String> times = (List<String>) m.get("lessonTimes");
        List<Lesson> lessons = new ArrayList<>();
        for (int i = 0; i < times.size(); i++) {
            Lesson lesson = new Lesson();
            lesson.setTime(times.get(i));
            lessons.add(lesson);
            lessonRepository.save(lesson);
        }
        course.setLessons(lessons);

        courseRepository.save(course);

        System.out.println(course);
        return new DataResponse(0,null,"更新成功");
    }

    /*@PostMapping("/updateCourse/person")//已被上面的updateCourse方法替代
    @PreAuthorize("hasRole('ADMIN')")//学生申请选课不应该直接使用本方法，应待管理员抽签后调用
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
    }*/
}
