package com.management.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name="teacher")
public class Teacher extends Person{
    /*@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer teacherId;*/
    private String teacherId;

    @Size(max = 20)
    private String degree;

    @Size(max = 50)
    private String title;

    private String department;

    /*@ManyToMany(mappedBy = "teachers")
    private List<Event> events;

    @ManyToMany(mappedBy = "teachers")
    private List<Course> courses;*/
}
