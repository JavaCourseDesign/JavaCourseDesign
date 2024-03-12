package com.management.server.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

//should be the super class of course,activities,etc
@Entity
@Table(name="event",uniqueConstraints = {})
@Data
//@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer eventId;

    private String name;

    private String date;//2024-05-15

    private String beginTime;//08:31

    private String endTime;//14:05

    @ManyToMany(mappedBy = "events")
    private List<Person> persons;
}
