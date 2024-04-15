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
import java.util.function.Function;
import java.util.stream.Collectors;

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
        Course course = BeanUtil.mapToBean(m, Course.class, true, CopyOptions.create().ignoreError());//要求map键值与对象一致

        // 获取所有的 persons
        List<String> personIds = ((List<Map>) m.get("persons")).stream()
                .map(personMap -> (String) personMap.get("personId"))
                .collect(Collectors.toList());
        Map<String, Person> personMap = personRepository.findAllById(personIds)
                .stream()
                .collect(Collectors.toMap(Person::getPersonId, Function.identity()));

        // 设置 course 的 persons
        List<Person> persons = new ArrayList<>(personMap.values());
        course.setPersons(persons);

        // 保存 persons 的更改
        personRepository.saveAll(persons);

        // 获取所有的 lessons
        List<Map> mapLessons = (List<Map>) m.get("lessons");
        List<Lesson> lessons = new ArrayList<>();
        for (Map mapLesson : mapLessons) {
            Lesson lesson = new Lesson();
            mapLesson.remove("persons");
            BeanUtil.fillBeanWithMap(mapLesson, lesson, true, CopyOptions.create());
            lesson.setPersons(persons);
            lessons.add(lesson);
        }

        // 批量保存 lessons
        lessonRepository.saveAll(lessons);
        course.setLessons(lessons);

        // 获取所有的 preCourses
        List<String> preCourseIds = ((List<Map>) m.get("preCourses")).stream()
                .map(preCourseMap -> (String) preCourseMap.get("courseId"))
                .collect(Collectors.toList());
        Map<String, Course> preCourseMap = courseRepository.findAllById(preCourseIds)
                .stream()
                .collect(Collectors.toMap(Course::getCourseId, Function.identity()));

        // 设置 course 的 preCourses
        List<Course> preCourses = new ArrayList<>(preCourseMap.values());
        course.setPreCourses(preCourses);

        courseRepository.save(course);

        //System.out.println(course);
        //System.out.println(course.getLessons().get(0).getTime());

        return new DataResponse(0,null,"添加成功");
    }

    @PostMapping("/updateCourse")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse updateCourse(@RequestBody Map m){
        String courseId = (String) m.get("courseId");
        if(!courseRepository.existsByCourseId(courseId)) {
            return new DataResponse(-1,null,"课程不存在，无法更新");
        }
        Course course = courseRepository.findByCourseId(courseId);

        // 获取所有的 persons
        List<String> personIds = ((List<Map>) m.get("persons")).stream()
                .map(personMap -> (String) personMap.get("personId"))
                .collect(Collectors.toList());
        Map<String, Person> personMap = personRepository.findAllById(personIds)
                .stream()
                .collect(Collectors.toMap(Person::getPersonId, Function.identity()));

        // 设置 course 的 persons
        List<Person> persons = new ArrayList<>(personMap.values());
        course.setPersons(persons);

        // 保存 persons 的更改
        personRepository.saveAll(persons);

        // 获取所有的 lessons
        List<Map> mapLessons = (List<Map>) m.get("lessons");
        List<Lesson> lessons = new ArrayList<>();
        for (Map mapLesson : mapLessons) {
            Lesson lesson = new Lesson();
            mapLesson.remove("persons");
            BeanUtil.fillBeanWithMap(mapLesson, lesson, true, CopyOptions.create());
            lesson.setPersons(persons);
            lessons.add(lesson);
        }

        // 批量保存 lessons
        lessonRepository.saveAll(lessons);
        course.setLessons(lessons);

        // 获取所有的 preCourses
        List<String> preCourseIds = ((List<Map>) m.get("preCourses")).stream()
                .map(preCourseMap -> (String) preCourseMap.get("courseId"))
                .collect(Collectors.toList());
        Map<String, Course> preCourseMap = courseRepository.findAllById(preCourseIds)
                .stream()
                .collect(Collectors.toMap(Course::getCourseId, Function.identity()));

        // 设置 course 的 preCourses
        List<Course> preCourses = new ArrayList<>(preCourseMap.values());
        course.setPreCourses(preCourses);

        courseRepository.save(course);

        return new DataResponse(0,null,"更新成功");
    }

    @PostMapping("/deleteCourse")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse deleteCourse(@RequestBody Map m){
        courseRepository.deleteAllByCourseId(""+m.get("courseId"));
        return new DataResponse(0,null,"删除成功");
    }

    //选课抽签方法（理论上也可以在前端实现，但鉴于后端没啥东西而且传一堆学生传来传去好像很浪费，故写在后端）
    @PostMapping("/drawLots")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse drawLots(@RequestBody Map m){
        String courseId = (String) m.get("courseId");
        Double capacity = (Double) m.get("capacity");
        if(!courseRepository.existsByCourseId(courseId)) {
            return new DataResponse(-1,null,"课程不存在，无法抽签");
        }
        Course course = courseRepository.findByCourseId(courseId);
        if(course.getPersons().stream().anyMatch(person -> person instanceof Student))  {
            return new DataResponse(-1,null,"课程已有学生，无法抽签");
        }

        List<Person> willingStudents = new ArrayList<>(course.getWillingStudents());
        if(willingStudents.size()<=capacity){
            course.setPersons(willingStudents);
        }else{
            List<Person> persons = new ArrayList<>();
            ArrayList<Map> personsMap = (ArrayList<Map>) m.get("persons");
            for (int i = 0; i < personsMap.size(); i++) {
                persons.add(personRepository.findByPersonId((personsMap.get(i).get("personId")).toString()));
            }//保证老师不会被修改

            for (int i = 0; i < capacity; i++) {
                int index = (int) (Math.random() * willingStudents.size());
                persons.add(studentRepository.findByPersonId(willingStudents.get(index).getPersonId()));
                willingStudents.remove(index);
            }
            course.setPersons(persons);
            course.setWillingStudents(willingStudents);//如果选课未开放且有人选课，那么这些人就是抽签失败的人
        }
        courseRepository.save(course);
        return new DataResponse(0,null,"抽签成功");
    }

    @PostMapping("/applyCourse")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse applyCourse(@RequestBody Map m){//前序课分数要求待添加
        String courseId = (String) m.get("courseId");
        if(!courseRepository.existsByCourseId(courseId)) {
            return new DataResponse(-1,null,"课程不存在，无法选课");
        }
        Course course = courseRepository.findByCourseId(courseId);
        if(!course.isAvailable()){
            return new DataResponse(-1,null,"选课未开放/已结束");
        }
        Person person = personRepository.findByPersonId((String) m.get("personId"));
        if(course.getPersons().contains(person)){
            return new DataResponse(-1,null,"已选中，无需重复选课");
        }
        course.getWillingStudents().add(person);
        courseRepository.save(course);
        return new DataResponse(0,null,"选课成功");
    }
}
