package com.management.server.repositories;

import com.management.server.models.AdministrativeClass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministrativeClassRepository extends JpaRepository<AdministrativeClass,String> {
    AdministrativeClass findByAdministrativeClassId(String administrativeClassId);
    boolean existsByAdministrativeClassId(String administrativeClassId);
    void deleteAllByAdministrativeClassId(String administrativeClassId);
}
