package com.management.server.repositories;

import com.management.server.models.Score;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScoreRepository extends JpaRepository<Score, String> {
    List<Score> findByCourseCourseId(String courseId);
    List<Score> findByStudentStudentId(String studentId);
}
