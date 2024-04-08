package com.management.server.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity

//@Data
@Getter
@Setter

@Table(name="course")

//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")//在递归中第二次出现时用name属性替代本对象避免无限递归
//@JsonIgnoreProperties(value = {"persons"})

public class Course{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String courseId;//待修改

    private String name;

    private String reference;

    private Double capacity;//课容量

    private Double credit;//学分

    private Double beginWeek;//开始周次

    private Double endWeek;//结束周次

    @OneToMany(mappedBy = "course")
    private List<Lesson> lessons;

    /*@ManyToMany
    @JoinTable(name = "student_course")
    private List<Student> students;

    @ManyToMany
    @JoinTable(name = "teacher_course")
    private List<Teacher> teachers;*/

    //在老师的示例中有preCourse（前序课程）！！！待实现  关系为@manytoone

    @ManyToOne
    @JoinColumn(name="pre_course_id")
    private Course preCourse;

    @ManyToMany
    @JoinTable(name = "person_course")
    @JsonIgnoreProperties(value = {"courses"})
    private List<Person> persons;

    //lesson should be subClass of event, lesson to course should be many to one
    //course should not be subClass of event

}
