package com.management.server.repositories;

import com.management.server.models.Course;
import com.management.server.models.Score;
import com.management.server.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScoreRepository extends JpaRepository<Score, String> {
    List<Score> findByStudentStudentId(String studentId);

    List<Score> findByCourseCourseId(String courseId);
    List<Score> findByCourse(Course course);
    List<Score> findByStudent(Student student);

    Score findByScoreId(String scoreId);
}
