package com.management.server.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name="course")

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")//在递归中第二次出现时用name属性替代本对象避免无限递归

public class Course{
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private String courseId;//待修改

    private String name;

    private String reference;

    private Integer capacity;//课容量

    private Integer credit;//学分

    @OneToMany(mappedBy = "course")
    private List<Lesson> lessons;

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
