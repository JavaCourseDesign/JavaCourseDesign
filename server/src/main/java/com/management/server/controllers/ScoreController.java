package com.management.server.controllers;

import com.management.server.models.Course;
import com.management.server.models.Score;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.CourseRepository;
import com.management.server.repositories.ScoreRepository;
import com.management.server.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ScoreController {
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;

    @PostMapping("/addScore")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse addScore(@RequestBody Map m){
        Score score = new Score();
        Course course = courseRepository.findByCourseId((String) m.get("courseId"));
        score.setStudent(studentRepository.findByStudentId((String) m.get("studentId")));
        score.setCourse(course);
        score.setRegularMark((Double) m.get("regularMark"));//应该和作业、出勤情况联系起来
        score.setFinalMark((Double) m.get("finalMark"));
        score.setMark(score.getRegularMark()*course.getRegularWeight()+score.getFinalMark()*(1-course.getRegularWeight()));
        scoreRepository.save(score);
        return new DataResponse(0,null,"添加成功");
    }

    /*@PostMapping("/getScoresOfACourse")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse getScore(@RequestBody Map m){
        return new DataResponse(0,scoreRepository.findByCourseCourseId((String) m.get("courseId")),null);
    }

    @PostMapping("/getScoresOfAStudent")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse getScoreOfAStudent(@RequestBody Map m){
        return new DataResponse(0,scoreRepository.findByStudentStudentId((String) m.get("studentId")),null);
    }*///搜索逻辑写在前端

    @PostMapping("/getAllScore")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public DataResponse getAllScore(){
        return new DataResponse(0,scoreRepository.findAll(),null);
    }
}
