package com.management.server.repositories;

import com.management.server.models.AdministrativeClass;
import com.management.server.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AdministrativeClassRepository extends JpaRepository<AdministrativeClass,String> {
    AdministrativeClass findByAdministrativeClassId(String administrativeClassId);
    boolean existsByAdministrativeClassId(String administrativeClassId);
    void deleteAllByAdministrativeClassId(String administrativeClassId);
    @Query("SELECT a.administrativeClassId FROM AdministrativeClass a WHERE?1 MEMBER OF a.students")
    String findAdministrativeClassByStudent(Student s);
}
