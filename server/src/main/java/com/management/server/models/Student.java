package com.management.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@EqualsAndHashCode(callSuper = true)//设为true时equals和hashcode方法将考虑/包含父类的属性
@Entity
@Data
@Table(name="student",uniqueConstraints = {@UniqueConstraint(columnNames = {"student_id"})})


public class Student extends Person{
    /*@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer studentId;*/

    @Size(max = 20)
    private String studentId;//学号 是否可以通过setPersonId 来代替？ 不能，工号与学号可重复

    @Size(max = 20)
    private String major;

    @Size(max=20)
    private String homeTown;
    @Size(max=20)
    private String highSchool;

    @OneToMany(fetch = FetchType.LAZY,cascade = {CascadeType.ALL},orphanRemoval = true)
    @JsonIgnore
    private List<Family> families;

    /*@Size(max = 50)
    private String className;*/

    /*@ManyToOne
    @JoinColumn(name = "administrative_class_id")
    private AdministrativeClass administrativeClass;*/

    /*@ManyToMany(mappedBy = "students")
    private List<Event> events;

    @ManyToMany(mappedBy = "students")
    private List<Course> courses;*/

    /*public void addEvent(Event event){
        events.add(event);
    }*/

}
