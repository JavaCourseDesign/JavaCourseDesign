package com.management.server.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Entity

@Data
@Table(name="course")
/*@NamedEntityGraph(name = "Course",
        attributeNodes = {
                @NamedAttributeNode("lessons"),
                //@NamedAttributeNode("persons")
        },
        subgraphs = {
                //@NamedSubgraph(name = "Event.persons", attributeNodes = @NamedAttributeNode("persons"))
        }
    )*/
/*@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")//在递归中第二次出现时用name属性替代本对象避免无限递归
@JsonIgnoreProperties(value = {"persons"})*/

public class Course{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String courseId;//待修改

    private String name;

    private String reference;//教师有权限

    private Double capacity;//课容量

    private Double credit;//学分

    private Double regularWeight=0.5;//平时成绩权重 教师有权限

    private String type;//课程类型 0 Required 1 Optional 2 Selective

    /*private Double beginWeek;//开始周次 教师有权限

    private Double endWeek;//结束周次 教师有权限*/ //改为lesson的属性

    private boolean available;//是否可选

    @Transient//@Transient 注解表示该属性并非一个到数据库表的字段的映射，ORM框架将忽略该属性。
    private Boolean chosen;//是否被选中，由controller根据具体人进行判断


    @OneToMany(fetch = FetchType.LAZY,cascade = {CascadeType.ALL},orphanRemoval = true)
    //@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "eventId")
    @JsonIgnore
    private List<Lesson> lessons;//可以通过get0和getSize得到开始结束周次

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
    //@JsonIgnoreProperties(value = {"courses"})
    private Set<Person> persons;

    //希望选课的学生
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "willing_student_course")
    //@JsonIgnore
    private Set<Person> willingStudents;

    //lesson should be subClass of event, lesson to course should be many to one
    //course should not be subClass of event

    /*public void setLessons(List<Lesson> lessons) {
        if(this.lessons!=null)
        {
            this.lessons.clear();
            if (lessons != null) {
                this.lessons.addAll(lessons);
            }
        }
    }*/
}
