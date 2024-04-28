package com.management.server.repositories;

import com.management.server.models.Absence;
import com.management.server.models.Event;
import com.management.server.models.Lesson;
import com.management.server.models.Person;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface AbsenceRepository extends JpaRepository<Absence, String> {
    List<Absence> findAbsenceByEvent(Event event);
    boolean existsByEventAndPerson(Event event, Person person);
    List<Absence> findAbsencesByPerson(Person person);
    Absence findAbsenceByAbsenceId(String absenceId);

    Absence findAbsenceByEventAndPerson(Event event, Person person);

    List<Absence> findAbsencesByEventInAndPersonIn(List<Event> lessons, List<Person> students);

    @Query("SELECT a FROM Absence a WHERE a.event.eventId IN :eventIds AND TYPE(a.person) = :personType")
    List<Absence> findAbsencesByEventIdsAndPersonType(@Param("eventIds") List<String> eventIds, @Param("personType") Class<?> personType);
}
