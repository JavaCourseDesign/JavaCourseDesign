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
    /*@Query(value = "select max(personId) from Student ")
    Integer getMaxId();
    Optional<Student> findByPersonPersonId(Integer personId);
    Optional<Student> findByPersonNum(String num);
    @Query(value = "from Student where ?1='' or Person.num like %?1% or Person.name like %?1% ")
    List<Student> findStudentListByNumName(String numName);*/
    /*@Query("select p from Person p where p.personId = ?1")
    Student findByPersonId(Integer personId);*/
}
