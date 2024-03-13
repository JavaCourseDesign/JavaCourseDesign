package com.management.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@Table(name="student",uniqueConstraints = {})

public class Student{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer studentId;

    @Size(max = 20)
    private String major;

    @Size(max = 50)
    private String className;

    @ManyToMany(mappedBy = "students")
    private List<Event> events;

    /*public void addEvent(Event event){
        events.add(event);
    }*/


}
