package com.management.server.repositories;

import com.management.server.models.Teacher;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface TeacherRepository {
    /*@Query(value = "select max(personId) from Teacher  ")
    Integer getMaxId();
    Optional<Teacher> findByPersonPersonId(Integer personId);
    Optional<Teacher> findByPersonNum(String num);
    List<Teacher> findByPersonName(String name);
    @Query(value = "from Teacher where ?1='' or Person.num like %?1% or Person.name like %?1% ")
    List<Teacher> findTeacherListByNumName(String numName);*/
}
