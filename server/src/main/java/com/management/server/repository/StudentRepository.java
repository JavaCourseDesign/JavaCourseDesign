package com.management.server.repository;

import com.management.server.models.Student;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
}
