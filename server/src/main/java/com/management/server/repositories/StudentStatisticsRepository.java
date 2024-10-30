package com.management.server.repositories;


import com.management.server.models.StudentStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentStatisticsRepository extends JpaRepository<StudentStatistics,Integer> {
}
