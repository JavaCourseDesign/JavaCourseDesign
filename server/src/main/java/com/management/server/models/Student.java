package com.management.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@EqualsAndHashCode(callSuper = true)//idea建议，用途未知
@Entity
@Data
@Table(name="student",uniqueConstraints = {@UniqueConstraint(columnNames = {"student_id"})})

public class Student extends Person{
    /*@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer studentId;*/

    @Size(max = 20)
    private String studentId;//学号 是否可以通过setPersonId 来代替？

    @Size(max = 20)
    private String major;

    @Size(max = 50)
    private String className;

    @ManyToMany(mappedBy = "students")
    private List<Event> events;

    @ManyToMany(mappedBy = "students")
    private List<Course> courses;

    /*public void addEvent(Event event){
        events.add(event);
    }*/



}
