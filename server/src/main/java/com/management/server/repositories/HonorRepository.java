package com.management.server.repositories;

import com.management.server.models.Honor;
import com.management.server.models.Innovation;
import com.management.server.models.Person;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface HonorRepository extends JpaRepository<Honor,String> {
    Honor findByEventId(String honorId);

    @Query("SELECT i FROM Honor i WHERE :person MEMBER OF i.persons")
    List<Honor> findByPersons(@Param("person") Person person);

    Integer deleteByEventId(String eventId);
}