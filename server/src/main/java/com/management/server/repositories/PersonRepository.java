package com.management.server.repositories;

import com.management.server.models.Person;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Transactional

public interface PersonRepository extends JpaRepository<Person,Integer> {
    Person findByPersonId(Integer personId);
}
