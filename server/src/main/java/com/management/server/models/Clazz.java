package com.management.server.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "clazz")
public class Clazz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String clazzId;

    private String major;
    private String grade;
    private String classNumber;
    private String name;//这样赋值是被允许的吗？

    /*@OneToOne
    private Teacher headTeacher;*/ //暂时不考虑班主任

    @OneToMany
    private List<Student> students;

}
