package com.management.server.repositories;

import com.management.server.models.Course;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface CourseRepository extends JpaRepository<Course,Integer> {
    boolean existsByCourseId(String courseId);
    void deleteAllByCourseId(String courseId);
}
