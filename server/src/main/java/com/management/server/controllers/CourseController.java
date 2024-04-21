package com.management.server.controllers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.management.server.models.*;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.*;
import com.management.server.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
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
    public DataResponse getAllCourses(@RequestBody Map m){
        String username = CommonMethod.getUsername();
        List<Course> courses = courseRepository.findAll();
        //给每个course添加一个抽签状态属性，当这个course不可选且willingStudents中包含了username代表的学生时，抽签状态为false，否则为true
        if(m.get("getChosenState")!=null&&m.get("getChosenState").toString().equals("true")){
            courses.forEach(course -> {
                if(course.getPersons().stream().anyMatch(person -> studentRepository.findByStudentId(username).getPersonId().equals(person.getPersonId()))){
                    course.setChosen(true);
                }
                else if(!course.isAvailable()&&course.getWillingStudents().stream().anyMatch(person -> studentRepository.findByStudentId(username).getPersonId().equals(person.getPersonId()))){
                    course.setChosen(false);
                }
                else
                {
                    course.setChosen(null);
                }
            });
        }

        if(m.get("filterConflict")!=null&&m.get("filterConflict").toString().equals("true")){
            courses = courses.stream().filter(course ->
                    !conflict(new ArrayList<>(
                            courseRepository.findWantedCoursesByPersonId(studentRepository.findByStudentId(username).getPersonId())),course)).collect(Collectors.toList());
        }
        if(m.get("filterAvailable")!=null&&m.get("filterAvailable").toString().equals("true")){
            courses = courses.stream().filter(Course::isAvailable).collect(Collectors.toList());
        }
        if(m.get("filterChosen")!=null&&m.get("filterChosen").toString().equals("true")){
            //过滤掉username代表的学生已经在willingStudents的课程
            courses = courses.stream().filter(course ->
                    course.getWillingStudents().stream().noneMatch(person ->
                            studentRepository.findByPersonId(person.getPersonId()).getStudentId().equals(username))).collect(Collectors.toList());
        }
        return new DataResponse(0,courses,null);
    }

    @PostMapping("/getLessonsByCourseId")
    public DataResponse getLessonsByCourse(@RequestBody Map m){
        String courseId = (String) m.get("courseId");
        if(!courseRepository.existsByCourseId(courseId)) {
            return new DataResponse(-1, List.of(),"课程不存在，无法获取课程信息");
        }
        System.out.println(courseRepository.findByCourseId(courseId).getLessons().size());
        if(courseRepository.findByCourseId(courseId).getLessons().isEmpty()){
            //System.out.println("无课程信息");
            return new DataResponse(0, List.of(),"无课程信息");
        }
        Course course = courseRepository.findByCourseId(courseId);
        return new DataResponse(0,course.getLessons(),null);
    }

    /*@PostMapping("/getPreCoursesByCourseId")
    public DataResponse getPreCourseByCourse(@RequestBody Map m){
        String courseId = (String) m.get("courseId");
        if(!courseRepository.existsByCourseId(courseId)) {
            return new DataResponse(-1, List.of(),"课程不存在，无法获取课程信息");
        }
        Course course = courseRepository.findByCourseId(courseId);
        if(course.getPreCourses()==null){
            return new DataResponse(0, List.of(),"无先修课程");
        }
        return new DataResponse(0,course.getPreCourses(),null);
    }*/

    @PostMapping("/addCourse")
    //@PreAuthorize("hasRole('ADMIN')")
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
        Set<Person> persons = new HashSet<>(personMap.values());
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

        /*// 获取所有的 preCourses
        List<String> preCourseIds = ((List<Map>) m.get("preCourses")).stream()
                .map(preCourseMap -> (String) preCourseMap.get("courseId"))
                .collect(Collectors.toList());
        Map<String, Course> preCourseMap = courseRepository.findAllById(preCourseIds)
                .stream()
                .collect(Collectors.toMap(Course::getCourseId, Function.identity()));

        // 设置 course 的 preCourses
        Set<Course> preCourses = new HashSet<>(preCourseMap.values());
        course.setPreCourses(preCourses);*/

        courseRepository.save(course);

        //System.out.println(course);
        //System.out.println(course.getLessons().get(0).getTime());

        return new DataResponse(0,null,"添加成功");
    }
    @PostMapping("/getTeacherCourses")
    public DataResponse getTeacherCourses(){
        String teacherId=teacherRepository.findByTeacherId(CommonMethod.getUsername()).getPersonId();
        return new DataResponse(0,courseRepository.findCoursesByPersonId(teacherId),null);
    }

    @PostMapping("/updateCourse")
    //@PreAuthorize("hasRole('ADMIN')")
    public DataResponse updateCourse(@RequestBody Map m){
        String courseId = (String) m.get("courseId");
        if(!courseRepository.existsByCourseId(courseId)) {
            return new DataResponse(-1,null,"课程不存在，无法更新");
        }
        Course course = courseRepository.findByCourseId(courseId);
        BeanUtil.fillBeanWithMap(m, course, true, CopyOptions.create().ignoreError());

        // 获取所有的 persons
        List<String> personIds = ((List<Map>) m.get("persons")).stream()
                .map(personMap -> (String) personMap.get("personId"))
                .collect(Collectors.toList());
        Map<String, Person> personMap = personRepository.findAllById(personIds)
                .stream()
                .collect(Collectors.toMap(Person::getPersonId, Function.identity()));

        // 设置 course 的 persons
        Set<Person> persons = new HashSet<>(personMap.values());
        course.setPersons(persons);

        // 保存 persons 的更改
        personRepository.saveAll(persons);

        if(m.get("lessons")!=null){
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
        }

        courseRepository.save(course);

        return new DataResponse(0,null,"更新成功");
    }

    @PostMapping("/deleteCourse")
    //@PreAuthorize("hasRole('ADMIN')")
    public DataResponse deleteCourse(@RequestBody Map m){
        courseRepository.deleteAllByCourseId(""+m.get("courseId"));
        return new DataResponse(0,null,"删除成功");
    }

    //选课抽签方法（理论上也可以在前端实现，但鉴于后端没啥东西而且传一堆学生传来传去好像很浪费，故写在后端）
    @PostMapping("/drawLots")
    //@PreAuthorize("hasRole('ADMIN')")
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

        Set<Person> willingStudents = new HashSet<>(course.getWillingStudents());
        if(willingStudents.size()<=capacity){
            course.setPersons(willingStudents);
        }else{
            Set<Person> persons = new HashSet<>();
            ArrayList<Map> personsMap = (ArrayList<Map>) m.get("persons");
            for (int i = 0; i < personsMap.size(); i++) {
                persons.add(personRepository.findByPersonId((personsMap.get(i).get("personId")).toString()));
            }//保证老师不会被修改

            for (int i = 0; i < capacity; i++) {
                int index = (int) (Math.random() * willingStudents.size());
                persons.add(studentRepository.findByPersonId(willingStudents.toArray(new Person[0])[index].getPersonId()));
                willingStudents.remove(willingStudents.toArray(new Person[0])[index]);//此处逻辑需要再探讨
            }
            course.setPersons(persons);
            course.setWillingStudents(willingStudents);//如果选课未开放且有人选课，那么这些人就是抽签失败的人
        }
        course.setAvailable(false);
        courseRepository.save(course);
        return new DataResponse(0,null,"抽签成功");
    }

    @PostMapping("/applyCourse")
    //@PreAuthorize("hasRole('STUDENT')")
    public DataResponse applyCourse(@RequestBody Map m){//前序课分数要求待添加
        String courseId = (String) m.get("courseId");
        String username = CommonMethod.getUsername();
        if(!courseRepository.existsByCourseId(courseId)) {
            return new DataResponse(-1,null,"课程不存在，无法选课");
        }
        Course course = courseRepository.findByCourseId(courseId);
        if(!course.isAvailable()){
            return new DataResponse(-1,null,"选课未开放/已结束");
        }

        Person person = studentRepository.findByStudentId(username);
        if(course.getPersons().contains(person)){
            return new DataResponse(-1,null,"已选中，无需重复选课");
        }
        if(course.getWillingStudents().contains(person)){
            return new DataResponse(-1,null,"已申请，无需重复选课");
        }
        if(conflict(new ArrayList<>(courseRepository.findWantedCoursesByPersonId(person.getPersonId())),course)){
            return new DataResponse(-1,null,"课程时间冲突");
        }
        course.getWillingStudents().add(person);
        courseRepository.save(course);
        return new DataResponse(0,null,"选课成功");
    }

    @PostMapping("/getWantedCourses")
    public DataResponse getWantedCourses(){//所有与本人相关的都应该通过token提取
        String username = CommonMethod.getUsername();
        //System.out.println(username);
        //System.out.println(studentRepository.findByStudentId(username).getPersonId());
        //System.out.println(courseRepository.findWantedCoursesByPersonId(studentRepository.findByStudentId(username).getPersonId()));
        List<Course> courses = courseRepository.findWantedCoursesByPersonId(studentRepository.findByStudentId(username).getPersonId());
        return new DataResponse(0,courses,null);
    }

    private boolean conflict(List<Course> courses, Course course){
        for (Course c : courses) {
            if(c.getLessons().stream().anyMatch(
                    lesson -> course.getLessons().stream().anyMatch(
                            lesson1 ->
                                    (lesson1.getWeek()+lesson1.getDay()+lesson1.getTime())
                                            .equals(lesson.getWeek()+lesson.getDay()+lesson.getTime())))){//实现不优雅不精确，但是不想写太多了
                return true;
            }
        }
        return false;
    }
}
