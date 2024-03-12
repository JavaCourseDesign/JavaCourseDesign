package com.management.server.models;

import jakarta.persistence.*;
import lombok.Data;

//should be the super class of course,activities,etc

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
}
