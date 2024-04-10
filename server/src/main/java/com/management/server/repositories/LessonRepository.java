package com.management.server.repositories;

import com.management.server.models.Course;
import com.management.server.models.Lesson;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@Transactional
public interface LessonRepository extends JpaRepository<Lesson, String> {

}
