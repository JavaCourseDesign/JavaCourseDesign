package com.management.server.repositories;

import com.management.server.models.Student;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface StudentRepository extends JpaRepository<Student,Integer> {
    /*Optional<Student> findByPersonPersonId(Integer personId);
    Optional<Student> findByPersonNum(String num);
    List<Student> findByPersonName(String name);*/
    /*Optional<Student> findByPersonPersonId(Integer personId);
    Optional<Student> findByPersonNum(String num);
    @Query(value = "from Student where ?1='' or Person.num like %?1% or Person.name like %?1% ")
    List<Student> findStudentListByNumName(String numName);*/
    /*@Query("select s from Student s where s.studentId = ?1")
    Student findByStudentId(Integer personId);*/
    Student findByStudentId(String studentId);

    List<Student> findAll();
    boolean existsByStudentId(String studentId);
    Integer deleteAllByStudentId(String studentId);
}
