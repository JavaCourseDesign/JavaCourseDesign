package com.management.server.repositories;

import com.management.server.models.Course;
import com.management.server.models.Homework;
import com.management.server.models.Person;
import com.management.server.models.Student;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface HomeworkRepository extends JpaRepository<Homework, String>{
    List<Homework> findHomeworkByStudent(Student student);
    List<Homework> findHomeworkByCourse(Course course);
    List<Homework> findHomeworkByCourseAndStudent(Course course, Student student);
    Homework findHomeworkByHomeworkId(String homeworkId);
    List<Homework> findHomeworkByCourseAndStudentIn(Course course, List<Person> students);

}
