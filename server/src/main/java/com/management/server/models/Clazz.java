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
    private String name;

    /*@OneToOne
    private Teacher headTeacher;*/ //暂时不考虑班主任

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "clazz_id")
    //@JsonIgnore
    private List<Student> students;

}
