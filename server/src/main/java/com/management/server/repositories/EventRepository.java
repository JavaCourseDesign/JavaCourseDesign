package com.management.server.repositories;

import com.management.server.models.Event;
import com.management.server.models.Person;
import com.management.server.models.Student;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface EventRepository extends JpaRepository<Event,String> {
    Event findEventByEventId(String eventId);
    @Query("SELECT e FROM Event e WHERE  :person MEMBER OF e.persons")
    List<Event> findEventsByPerson(@Param("person") Person person);
}
