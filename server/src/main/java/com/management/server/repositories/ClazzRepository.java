package com.management.server.repositories;

import com.management.server.models.Clazz;
import com.management.server.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ClazzRepository extends JpaRepository<Clazz,String> {
    Clazz findByClazzId(String clazzId);
    boolean existsByClazzId(String clazzId);
    void deleteAllByClazzId(String clazzId);
    @Query("SELECT a.clazzId FROM Clazz a WHERE?1 MEMBER OF a.students")
    String findClazzByStudent(Student s);
}
