package com.management.server.controllers;

import com.management.server.models.Course;
import com.management.server.models.Person;
import com.management.server.models.Score;
import com.management.server.models.Student;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.CourseRepository;
import com.management.server.repositories.ScoreRepository;
import com.management.server.repositories.StudentRepository;
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
