package com.management.server.repository;

import com.management.server.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;



@Repository

public interface StudentRepository extends JpaRepository<Student,Integer> {
}
