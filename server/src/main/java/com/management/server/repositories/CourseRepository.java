package com.management.server.repositories;

import com.management.server.models.Course;
import com.management.server.models.Person;
import com.management.server.models.Teacher;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface CourseRepository extends JpaRepository<Course,String> {
    //@EntityGraph(value = "Course", type = EntityGraph.EntityGraphType.LOAD)
    List<Course> findAll();
    Course findByCourseId(String courseId);
    @Query("SELECT c FROM Course c JOIN c.persons p WHERE p.personId = :personId")
    List<Course> findCoursesByPersonId(@Param("personId") String personId);

    @Query("SELECT c FROM Course c JOIN c.willingStudents p WHERE p.personId = :personId")
    List<Course> findWantedCoursesByPersonId(@Param("personId") String personId);
    /*@Modifying
    @Query("DELETE FROM Lesson l WHERE l IN (SELECT c.lessons FROM Course c WHERE c.courseId = :courseId)")
    void deleteLessonsByCourseId(@Param("courseId") String courseId);*/
    boolean existsByCourseId(String courseId);
    void deleteAllByCourseId(String courseId);
}
