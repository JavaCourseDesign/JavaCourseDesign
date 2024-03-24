package com.management.server.repositories;

import com.management.server.models.Event;
import com.management.server.models.Student;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface EventRepository extends JpaRepository<Event,Integer> {
}