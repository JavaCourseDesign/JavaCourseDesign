package com.management.server.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Entity
@Data
@Table(name = "clazz")
@EqualsAndHashCode(exclude = {"students"})
public class Clazz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String clazzId;

    private String major;
    private String grade;
    private String clazzNumber;
    private String name;

    /*@OneToOne
    private Teacher headTeacher;*/ //暂时不考虑班主任

    @OneToMany(mappedBy = "clazz",fetch = FetchType.EAGER)
    //@JoinColumn(name = "clazz_id")
    //@JsonIgnore
    private List<Student> students;

}
