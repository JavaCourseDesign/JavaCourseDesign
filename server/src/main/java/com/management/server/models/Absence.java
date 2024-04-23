package com.management.server.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Absence {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private String absenceId;

    private Boolean isApproved;
    private String offReason;
    private String destination;
    //private String time;

    @ManyToOne
    @JoinColumn(name="person_id")
    private Person person;

    @ManyToOne
    @JoinColumn(name="event_id")
    private Event event;
}
