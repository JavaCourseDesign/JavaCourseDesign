package com.management.server.controllers;

import com.management.server.models.Course;
import com.management.server.models.Teacher;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.CourseRepository;
import com.management.server.repositories.LessonRepository;
import com.management.server.repositories.TeacherRepository;
import com.management.server.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class LessonController {
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private CourseRepository courseRepository;
    @PostMapping("/getAllLessonsByCourse")
    @PreAuthorize("hasRole('TEACHER')")
    public DataResponse getAllLessonsByTeacher(@RequestBody Map m)
    {
        Course c=courseRepository.findByCourseId((String) m.get("courseId"));
        return new DataResponse(0, c.getLessons(), null);
    }

}
