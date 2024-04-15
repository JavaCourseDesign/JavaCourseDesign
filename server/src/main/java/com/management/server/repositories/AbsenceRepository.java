package com.management.server.repositories;

import com.management.server.models.Absence;
import com.management.server.models.Event;
import com.management.server.models.Person;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface AbsenceRepository extends JpaRepository<Absence, String> {
    boolean existsByEvent(Event event);
    List<Absence> findAbsencesByPerson(Person person);
    Absence findAbsenceByAbsenceId(String absenceId);
}
