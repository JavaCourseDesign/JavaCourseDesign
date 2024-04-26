package com.management.server.controllers;

import com.management.server.models.*;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @PostMapping("/addCourseScores")
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
            Course course = courseRepository.findByCourseId((String) m.get("courseId"));
            course.setRegularWeight(Double.parseDouble("0"+m.get("regularWeight")));
            course.getScores().clear();
            for(Person student:course.getPersons()){
                if(student instanceof Student) {
                    Score score = new Score();

                    //double disapprovedAbsence=0;
                    score.setAbsence(0.0);
                    for(Event event:course.getLessons()){
                        Absence absence = absenceRepository.findAbsenceByEventAndPerson(event,student);
                        if(absence!=null&&(absence.getIsApproved()==null||!absence.getIsApproved()))
                            //disapprovedAbsence++;
                            score.setAbsence(score.getAbsence()+1);
                    }

                    double homeworkA=0,homeworkB=0,homeworkC=0,homeworkD=0,homeworkE=0,unMarked=0,unHanded=0;
                    for(Homework homework:homeworkRepository.findHomeworkByCourseAndStudent(course,(Student) student)){
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

                    score.setStudent((Student) student);
                    course.getScores().add(score);
                    scoreRepository.save(score);
                }
            }
        }
        return new DataResponse(0,scoreRepository.findByCourseCourseId((String) m.get("courseId")),null);
    }

    @PostMapping("/getStudentScores")
    //@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse getStudentScores(@RequestBody Map m){
        return new DataResponse(0,scoreRepository.findByStudentStudentId((String) m.get("studentId")),null);
    }

    @PostMapping("/getAllScore")
    //@PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse getAllScore(){
        return new DataResponse(0,scoreRepository.findAll(),null);
    }
}
