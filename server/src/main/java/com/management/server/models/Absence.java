package com.management.server.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Absence {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Integer absenceId;

    private String reason;

    @OneToOne
    @JoinColumn(name="person_id")
    private Person person;

    @OneToOne
    @JoinColumn(name="related_event_id")
    private Event relatedEvent;
}
