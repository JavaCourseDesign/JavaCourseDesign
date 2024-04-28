package com.management.server.repositories;

import com.management.server.models.Fee;
import com.management.server.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeRepository extends JpaRepository<Fee,String > {
    List<Fee> findByPerson(Person person);
}
