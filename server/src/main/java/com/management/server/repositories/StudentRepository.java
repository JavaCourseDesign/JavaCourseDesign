package com.management.server.repositories;

import com.management.server.models.Student;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@Transactional
public interface StudentRepository extends JpaRepository<Student,String> {
    //查询完整的学生信息，包括懒加载的内容
    /*@EntityGraph(attributePaths = {"families"})
    @Query("select s from Student s where s.studentId = :studentId")
    Student findFullStudentByStudentId(@Param("studentId") String studentId);*/

    Student findByStudentId(String studentId);
    Student findByPersonId(String personId);
    List<Student> findAll();
    boolean existsByStudentId(String studentId);
    void deleteAllByStudentId(String studentId);
    Integer deleteAllByPersonId(String personId);

    /*Optional<Student> findByPersonPersonId(Integer personId);
    Optional<Student> findByPersonNum(String num);
    List<Student> findByPersonName(String name);*/
    /*Optional<Student> findByPersonPersonId(Integer personId);
    Optional<Student> findByPersonNum(String num);
    @Query(value = "from Student where ?1='' or Person.num like %?1% or Person.name like %?1% ")
    List<Student> findStudentListByNumName(String numName);*/
    /*@Query("select s from Student s where s.studentId = ?1")
    Student findByStudentId(Integer personId);*/
}
