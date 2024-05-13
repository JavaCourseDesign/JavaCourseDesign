package com.management.server.controllers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.management.server.models.*;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.*;
import com.management.server.util.CommonMethod;
import com.management.server.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @Autowired
    AbsenceRepository absenceRepository;
    @Autowired
    EventRepository eventRepository;

    @PostMapping("/getAllCourses")
    public DataResponse getAllCourses(){
        //把persons属性和willingStudents属性包含在返回内容中
        return new DataResponse(0,courseRepository.findAll(),null);
    }

    @PostMapping("/getAllCoursesForStudent")
    public DataResponse getAllCoursesForStudent(@RequestBody Map m){
        String username = CommonMethod.getUsername();
        String personId = studentRepository.findByStudentId(username).getPersonId();
        Student student = studentRepository.findByPersonId(personId);
        List<Course> courses = courseRepository.findAll();

        if(m.get("getChosenState")!=null&&m.get("getChosenState").toString().equals("true")){
            courses.forEach(course -> {
                Set<String> personIds = course.getPersons().stream().map(Person::getPersonId).collect(Collectors.toSet());
                Set<String> willingStudentIds = course.getWillingStudents().stream().map(Person::getPersonId).collect(Collectors.toSet());
                if(personIds.contains(personId)){
                    course.setChosen(true);
                }
                else if(!course.isAvailable()&&willingStudentIds.contains(personId)){
                    course.setChosen(false);
                }
                else
                {
                    course.setChosen(null);
                }
            });
        }

        if(m.get("filterConflict")!=null&&m.get("filterConflict").toString().equals("true")){
            List<Course> wantedCourses = courseRepository.findWantedCoursesByPersonId(personId);
            courses = courses.stream().filter(course -> !conflict(getPersonAllEvents(student), course)).collect(Collectors.toList());
        }
        if(m.get("filterAvailable")!=null&&m.get("filterAvailable").toString().equals("true")){
            courses = courses.stream().filter(Course::isAvailable).collect(Collectors.toList());
        }
        if(m.get("filterChosen")!=null&&m.get("filterChosen").toString().equals("true")){
            courses = courses.stream().filter(course ->
                    course.getWillingStudents().stream().noneMatch(person -> person.getPersonId().equals(personId))).collect(Collectors.toList());
        }
        return new DataResponse(0,courses,null);
    }

    @PostMapping("/getStudentCourses")
    public DataResponse getStudentCourses(){
        String studentId=studentRepository.findByStudentId(CommonMethod.getUsername()).getPersonId();
        List<Course> courseList=courseRepository.findCoursesByPersonId(studentId);
        return new DataResponse(0,courseList,null);
    }

    @PostMapping("/getTeacherCourses")
    public DataResponse getTeacherCourses(){
        String teacherId=teacherRepository.findByTeacherId(CommonMethod.getUsername()).getPersonId();
        return new DataResponse(0,courseRepository.findCoursesByPersonId(teacherId),null);
    }

    @PostMapping("/getLessonsByCourse")
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

        return new DataResponse(0,null,"添加成功");
    }


    @PostMapping("/updateCourse")
    //@PreAuthorize("hasRole('ADMIN')")
    public DataResponse updateCourse(@RequestBody Map m){
        System.out.println("\nmap:"+m);
        String courseId = (String) m.get("courseId");
        if(!courseRepository.existsByCourseId(courseId)) {
            return new DataResponse(-1,null,"课程不存在，无法更新");
        }
        Course course = courseRepository.findByCourseId(courseId);

        System.out.println("\nmap:"+m);
        List<Map> lessonsMap = (List<Map>) m.get("lessons");
        System.out.println("\nlessonMap:"+lessonsMap);
        m.remove("lessons");

        BeanUtil.fillBeanWithMap(m, course, CopyOptions.create().ignoreError());

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

        if(lessonsMap==null){
            courseRepository.save(course);
            return new DataResponse(0,null,"更新成功");
        }

        List<String> lessonIds = lessonsMap.stream().map(map -> (String) map.get("eventId")).toList();
        List<Lesson> lessons = lessonRepository.findAllById(lessonIds);
        //删掉不在lessons中的lesson
        course.getLessons().removeIf(lesson -> !lessonIds.contains(lesson.getEventId()));
        List<Lesson> newLessons = new ArrayList<>();

        for (Map mapLesson : lessonsMap) {
            Lesson lesson = lessons.stream()
                    .filter(l -> l.getEventId().equals(mapLesson.get("eventId")))
                    .findFirst()
                    .orElseGet(() -> {
                        Lesson newLesson = new Lesson();
                        newLesson.setCourse(course);
                        newLessons.add(newLesson);
                        return newLesson;
                    });
            BeanUtil.fillBeanWithMap(mapLesson, lesson, CopyOptions.create());
            //lesson.setPersons(persons);
        }

        //lessonRepository.saveAll(newLessons); 不用先保存？？？
        lessonRepository.saveAll(lessons);

        course.getLessons().addAll(newLessons);
        System.out.println("\nlesson:"+course.getLessons());


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
            course.getPersons().addAll(willingStudents);
        }else{
            for (int i = 0; i < capacity; i++) {
                int index = (int) (Math.random() * willingStudents.size());
                course.getPersons().add(studentRepository.findByPersonId(willingStudents.toArray(new Person[0])[index].getPersonId()));
                willingStudents.remove(willingStudents.toArray(new Person[0])[index]);//此处逻辑需要再探讨
            }
            course.setWillingStudents(willingStudents);//如果选课未开放且有人选课，那么这些人就是抽签失败的人
        }
        //更新course的lessons的人员
        List<Lesson> lessons = course.getLessons();
        for (Lesson lesson : lessons) {
            lesson.setPersons(new HashSet<>(course.getPersons()));
        }
        lessonRepository.saveAll(lessons);
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
        if(conflict(new ArrayList<>(getPersonAllEvents(person)),course)){
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


    @PostMapping("/uploadReference")
    public DataResponse uploadReference(@RequestBody byte[] barr,
                                        @RequestParam(name = "fileName") String fileName,
                                        @RequestParam(name="paras") String courseId){
        Course course=courseRepository.findByCourseId(courseId);
        if(course.getReference()!=null){
            FileUtil.deleteFile("pdf",course.getReference());
        }
        course.setReference(fileName);
        courseRepository.save(course);
        DataResponse r=FileUtil.uploadFile(barr,"pdf",fileName);
        return r;
    }
    @PostMapping("/downloadReference")
    public DataResponse downloadReference(@RequestBody Map m){
        String courseId = (String) m.get("courseId");
        Course course = courseRepository.findByCourseId(courseId);
        if(course.getReference()==null){
            return new DataResponse(-1,null,"请先上传文件！");
        }
        return FileUtil.downloadFile("pdf",course.getReference());
    }

    /*private boolean conflict(List<Course> courses, Course course){
        for (Course c : courses) {
            for (Lesson l1 : c.getLessons()) {
                for (Lesson l2 : course.getLessons()) {
                    if (isConflict(l1, l2)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }*/

    private List<Event> getPersonAllEvents(Person p){
        List<Course> courses = courseRepository.findCoursesByPersonId(p.getPersonId());
        courses.addAll(courseRepository.findWantedCoursesByPersonId(p.getPersonId()));
        List<Event> events = new ArrayList<>();
        for (Course course : courses) {
            events.addAll(course.getLessons());
        }
        events.addAll(p.getEvents());
        return events;
    }

    private boolean conflict(List<Event> events, Course course){
        for (Lesson l : course.getLessons()) {
            for (Event e : events) {
                if (isConflict(l, e)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isConflict(Event event1, Event event2) {
        // Check if the date ranges overlap
        boolean dateOverlap = !event1.getStartDate().isAfter(event2.getEndDate())
                && !event1.getEndDate().isBefore(event2.getStartDate());

        // Check if the time ranges overlap
        boolean timeOverlap = !event1.getStartTime().isAfter(event2.getEndTime())
                && !event1.getEndTime().isBefore(event2.getStartTime());

        return dateOverlap && timeOverlap;
    }
}
