package com.management.server.controllers;

import cn.hutool.core.bean.BeanUtil;
import com.management.server.models.*;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.*;
import com.management.server.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ScoreController {
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private AbsenceRepository absenceRepository;
    @Autowired
    private HomeworkRepository homeworkRepository;

    /*@PostMapping("/addCourseScores")
    //@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse addCourseScores(@RequestBody Map m){

        Course course = courseRepository.findByCourseId((String) m.get("courseId"));
        course.setRegularWeight(Double.parseDouble((String) m.get("regularWeight")));
        course.getScores().clear();
        //得到这门课的所有学生
        for(Person student:course.getPersons()){
            if(student instanceof Student) {
                Score score = new Score();
                //System.out.println(student);
                score.setStudent((Student) student);
                course.getScores().add(score);
                scoreRepository.save(score);
            }
        }
        return new DataResponse(0,null,"添加成功");
    }

    @PostMapping("/getCourseScores")
    //@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse getCourseScores(@RequestBody Map m){

        List<Score> scores = scoreRepository.findByCourseCourseId((String) m.get("courseId"));
        if(true||scores.isEmpty()){//如果没有成绩，就初始化成绩
            scores.clear();
            Course course = courseRepository.findByCourseId((String) m.get("courseId"));

            List<Event> lessons = new ArrayList<>(course.getLessons());
            List<Person> students=new ArrayList<>();
            for(Person person:course.getPersons()){
                if(person instanceof Student) students.add(person);
            }

            // 一次性获取所有的缺席记录
            List<Absence> allAbsences = absenceRepository.findAbsencesByEventInAndPersonIn(lessons, students);

            // 一次性获取所有的作业记录
            List<Homework> allHomeworks = homeworkRepository.findHomeworkByCourseAndStudentIn(course, students);

            // 创建一个映射，用于快速查找学生的缺席记录和作业记录
            Map<Person, List<Absence>> absenceMap = allAbsences.stream().collect(Collectors.groupingBy(Absence::getPerson));
            Map<Person, List<Homework>> homeworkMap = allHomeworks.stream().collect(Collectors.groupingBy(Homework::getStudent));


            course.setRegularWeight(Double.parseDouble("0"+m.get("regularWeight")));
            course.getScores().clear();
            for(Person student:course.getPersons()){
                if(student instanceof Student) {
                    Score score = new Score();

                    // 使用映射来查找学生的缺席记录
                    List<Absence> absences = absenceMap.getOrDefault(student, Collections.emptyList());
                    score.setAbsence(0.0);
                    for (Absence absence : absences) {
                        if (absence.getIsApproved() == null || !absence.getIsApproved()) {
                            score.setAbsence(score.getAbsence() + 1);
                        }
                    }

                    List<Homework> homeworks = homeworkMap.getOrDefault(student, Collections.emptyList());
                    double homeworkA=0,homeworkB=0,homeworkC=0,homeworkD=0,homeworkE=0,unMarked=0,unHanded=0;
                    for(Homework homework:homeworks){
                        if(homework.getHomeworkFile()==null) unHanded++;
                        else if(homework.getGrade()!=null) switch (homework.getGrade()){
                            case "A":
                                homeworkA++;
                                break;
                            case "B":
                                homeworkB++;
                                break;
                            case "C":
                                homeworkC++;
                                break;
                            case "D":
                                homeworkD++;
                                break;
                            case "E":
                                homeworkE++;
                                break;
                        }
                        else unMarked++;
                    }
                    //score.setInfo("缺勤次数："+disapprovedAbsence+" 作业A次数："+homeworkA+" 作业B次数："+homeworkB+" 作业C次数："+homeworkC+" 作业D次数："+homeworkD+" 作业E次数："+homeworkE);
                    score.setHomework("A:"+homeworkA+" B:"+homeworkB+" C:"+homeworkC+" D:"+homeworkD+" E:"+homeworkE+" 未批改:"+unMarked+" 未交:"+unHanded);

                    System.out.println("score:"+score);

                    score.setStudent((Student) student);
                    course.getScores().add(score);
                    scores.add(score);
                }
            }
            scoreRepository.saveAll(scores);
        }
        return new DataResponse(0,scoreRepository.findByCourseCourseId((String) m.get("courseId")),null);
    }*/

    @PostMapping("/getCourseScores")
    //@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse getCourseScores(@RequestBody Map m){
        //m应该包含课程号
        List<Score> scores = scoreRepository.findByCourseCourseId((String) m.get("courseId"));
        String msg = "获取成功";
        //如果为空，则初始化成绩
        if(scores.isEmpty()){
            Course course = courseRepository.findByCourseId((String) m.get("courseId"));
            course.getScores().clear();
            for(Person student:course.getPersons()){
                if(student instanceof Student) {
                    Score score = new Score();
                    score.setStudent((Student) student);
                    course.getScores().add(score);
                    scores.add(score);
                }
            }
            scoreRepository.saveAll(scores);
            msg = "初始化成功";
        }
        return new DataResponse(0,scores,msg);
    }

    //填充平时成绩（作业、缺勤）
    @PostMapping("/fillCourseScores")
    //@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse fillCourseScores(@RequestBody Map m){
        //m应该包含作业权重（homeworkWeight）、缺勤权重（absenceWeight）、课程号
        List<Score> scores = scoreRepository.findByCourseCourseId((String) m.get("courseId"));
        Course course = courseRepository.findByCourseId((String) m.get("courseId"));

        course.setHomeworkWeight((Double) m.get("homeworkWeight"));
        course.setAbsenceWeight((Double) m.get("absenceWeight"));

        List<Event> lessons = new ArrayList<>(course.getLessons());
        List<Person> students=new ArrayList<>();
        for(Person person:course.getPersons()){
            if(person instanceof Student) students.add(person);
        }

        // 一次性获取所有的缺席记录
        List<Absence> allAbsences = absenceRepository.findAbsencesByEventInAndPersonIn(lessons, students);

        // 一次性获取所有的作业记录
        List<Homework> allHomeworks = homeworkRepository.findHomeworkByCourseAndStudentIn(course, students);

        // 创建一个映射，用于快速查找学生的缺席记录和作业记录
        Map<Person, List<Absence>> absenceMap = allAbsences.stream().collect(Collectors.groupingBy(Absence::getPerson));
        Map<Person, List<Homework>> homeworkMap = allHomeworks.stream().collect(Collectors.groupingBy(Homework::getStudent));

        for(Score score:scores){
            Student student = score.getStudent();
            List<Absence> absences = absenceMap.getOrDefault(student, Collections.emptyList());
            double absence = 0.0;
            for (Absence absenceRecord : absences) {
                if (absenceRecord.getIsApproved() == null || !absenceRecord.getIsApproved()) {
                    absence += 1;
                }
            }
            score.setAbsence(absence);
            if(!lessons.isEmpty()) score.setAbsenceMark(100*(1-absence/lessons.size()));
            else score.setAbsenceMark(100.0);

            List<Homework> homeworks = homeworkMap.getOrDefault(student, Collections.emptyList());
            double homeworkA=0,homeworkB=0,homeworkC=0,homeworkD=0,homeworkE=0,unMarked=0,unHanded=0;
            double homeworkMark = 0;
            for(Homework homework:homeworks){
                if(homework.getHomeworkFile()==null) {
                    unHanded++;//未交不加分
                }
                else if(homework.getGrade()!=null) switch (homework.getGrade()){
                    case "A":
                        homeworkA++;
                        homeworkMark+=5;
                        break;
                    case "B":
                        homeworkB++;
                        homeworkMark+=4;
                        break;
                    case "C":
                        homeworkC++;
                        homeworkMark+=3;
                        break;
                    case "D":
                        homeworkD++;
                        homeworkMark+=2;
                        break;
                    case "E":
                        homeworkE++;
                        homeworkMark+=1;
                        break;
                }
                else unMarked++;
            }
            score.setHomework("A:"+homeworkA+" B:"+homeworkB+" C:"+homeworkC+" D:"+homeworkD+" E:"+homeworkE+" 未批改:"+unMarked+" 未交:"+unHanded);
            if(!homeworks.isEmpty()) score.setHomeworkMark(100*homeworkMark/(5*homeworks.size()));
            else score.setHomeworkMark(100.0);
        }

        scoreRepository.saveAll(scores);
        return new DataResponse(0,null,"填充成功");
    }

    @PostMapping("/uploadFinalScore")
    //@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse uploadFinalScore(@RequestBody Map m){
        Score score =scoreRepository.findByScoreId((String) m.get("scoreId"));
        score.setFinalMark(Double.parseDouble((String) m.get("finalMark")));
        score.setMark(score.getFinalMark()*(1-score.getCourse().getAbsenceWeight()-score.getCourse().getHomeworkWeight()) +
                      score.getAbsenceMark()*score.getCourse().getAbsenceWeight() +
                      score.getHomeworkMark()*score.getCourse().getHomeworkWeight());
        scoreRepository.save(score);

        Student s= studentRepository.findByPersonId("2");
        System.out.println("markcount"+s.getScores().size());

        return new DataResponse(0,null,"更新成功");
    }

    @PostMapping("/getStudentScores")
    //@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse getStudentScores(){
       List<Score> scoreList= scoreRepository.findByStudentStudentId(CommonMethod.getUsername());
       List<Map> scoreMapList = new ArrayList<>();
       for(Score score:scoreList)
       {
           Map scoreMap = BeanUtil.beanToMap(score);
           scoreMap.put("course",score.getCourse());
           scoreMap.put("hours",score.getCourse().getLessons().size()*2);
           scoreMapList.add(scoreMap);
       }
        return new DataResponse(0,scoreMapList,null);
    }

    @PostMapping("/getAllScore")
    //@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse getAllScore(){
        return new DataResponse(0,scoreRepository.findAll(),null);
    }
}
