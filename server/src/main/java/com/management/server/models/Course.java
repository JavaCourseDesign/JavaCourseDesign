package com.management.server.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name="course")
public class Course{
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private String courseId;

    private String reference;

    private Integer capacity;//课容量

    private Integer credit;//学分

    @OneToMany(mappedBy = "course")
    private List<Lesson> lessons;
   // private String beginWeek;
   // private String endWeek;

    /*@ManyToMany
    @JoinTable(name = "student_course")
    private List<Student> students;

    @ManyToMany
    @JoinTable(name = "teacher_course")
    private List<Teacher> teachers;*/


    @ManyToMany
    @JoinTable(name = "person_course")
    private List<Person> persons;


    //lesson should be subClass of event, lesson to course should be many to one
    //course should not be subClass of event
}
