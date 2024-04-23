package com.management.server.repositories;

import com.management.server.models.Course;
import com.management.server.models.Person;
import com.management.server.models.Student;
import com.management.server.models.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional

public interface PersonRepository extends JpaRepository<Person,String> {
    Person findByPersonId(String personId);
    Person findByName(String username);

    @Query("select p from Course c join c.persons p where c.courseId = :courseId")
    List<Person> findPersonsByCourseId(@Param("courseId") String courseId);

    boolean existsByPersonId(String personId);
}
