package com.management.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Entity

@Data
@Table(name="course")
/*@NamedEntityGraph(name = "Course",
        attributeNodes = {
                @NamedAttributeNode("persons")
        }
    )*/
/*@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")//在递归中第二次出现时用name属性替代本对象避免无限递归
@JsonIgnoreProperties(value = {"persons"})*/
@EqualsAndHashCode(exclude = {"lessons","persons","willingStudents","scores","homeworks"})
public class Course{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String courseId;//待修改

    private String name;

    private String reference;//教师有权限

    private Double capacity;//课容量

    private Double credit;//学分

    //private Double regularWeight=0.5;//平时成绩权重 教师有权限 不存入数据库

    private Double homeworkWeight;//作业成绩权重 教师有权限

    private Double absenceWeight;//期末成绩权重 教师有权限

    private String type;//课程类型 0 Required 1 Optional 2 Selective 代码代替似乎意义不大，后续考虑修改

    private boolean available;//是否可选

    @Transient//@Transient 注解表示该属性并非一个到数据库表的字段的映射，ORM框架将忽略该属性。
    private Boolean chosen;//是否被选中，由controller根据具体人进行判断

    //考虑添加一个学期属性，用于区分不同学期的课程，方便在简历中显示各学期均分
    //2023-2024学年第一学期
    //private String semester;


    @OneToMany(mappedBy = "course",fetch = FetchType.EAGER,cascade = {CascadeType.ALL},orphanRemoval = true)
    //@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "eventId")
    @JsonIgnore
    @ToString.Exclude
    private List<Lesson> lessons;

    /*@ManyToMany
    @JoinTable(name = "student_course")
    private List<Student> students;

    @ManyToMany
    @JoinTable(name = "teacher_course")
    private List<Teacher> teachers;*/

    /*@ManyToOne//一门课只有一门先修课？
    @JoinColumn(name="pre_course_id")
    private Course preCourse;*/

    /*@ManyToMany
    @JoinTable(name = "course_course")
    //@JsonIgnoreProperties(value = {"preCourses"})//非常重要，避免自身递归
    @ToString.Exclude//也非常重要，避免自身递归
    @JsonIgnoreProperties(value = {"preCourses","lessons","persons","willingStudents"})
    //@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
    private Set<Course> preCourses;*/

    private String preCourses;//用字符串存，不存对象了 存对象有一系列问题，包括但不限于无限递归、指向意义不明（前序课并不一定只有一个课序号，可能存在同名不同时的课程）

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "person_course")
    //@JsonIgnore
    //@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "personId")
    @JsonIgnoreProperties(value = {"courses","events"})
    @ToString.Exclude
    private Set<Person> persons;

    //希望选课的学生
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "willing_student_course")
    @JsonIgnoreProperties(value = {"courses","events"})
    @ToString.Exclude
    //@JsonIgnore
    private Set<Person> willingStudents;

    @OneToMany(mappedBy = "course",cascade = CascadeType.ALL,orphanRemoval = true)
    //@JoinColumn(name = "course_id")
    @JsonIgnore
    @ToString.Exclude
    private List<Score> scores;

    @OneToMany(mappedBy = "course",cascade = CascadeType.ALL,orphanRemoval = true)
    //@JoinColumn(name = "course_id")
    @JsonIgnore
    @ToString.Exclude
    private List<Homework> homeworks;
}
