package com.management.server.repositories;

import com.management.server.models.DailyActivity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface DailyActivityRepository extends JpaRepository<DailyActivity, String>{
    Integer deleteByEventId(String eventId);
    DailyActivity findByEventId(String eventId);
}
