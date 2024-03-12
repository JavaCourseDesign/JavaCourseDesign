package com.management.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name="teacher",uniqueConstraints = {})
public class Teacher extends Person{
    /*@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer teacherId;*/
    @Size(max = 20)
    private String degree;

    @Size(max = 50)
    private String title;

    @ManyToMany
    @JoinTable(name = "event", joinColumns = @JoinColumn(name = "personId"), inverseJoinColumns = @JoinColumn(name = "eventId"))
    private List<Event> events;
}
