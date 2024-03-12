package com.management.server.repositories;

import com.management.server.models.Person;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional

public interface PersonRepository extends JpaRepository<Person,Integer> {

}
