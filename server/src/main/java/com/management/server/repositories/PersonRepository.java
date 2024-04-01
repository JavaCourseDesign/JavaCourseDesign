package com.management.server.repositories;

import com.management.server.models.Person;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional

public interface PersonRepository extends JpaRepository<Person,Integer> {
    Person findByPersonId(Integer personId);
    @Query("SELECT p FROM Person p JOIN p.courses c WHERE c.courseId = :courseId")
    List<Person> findPersonsByCourseId(@Param("courseId") String courseId);
}
