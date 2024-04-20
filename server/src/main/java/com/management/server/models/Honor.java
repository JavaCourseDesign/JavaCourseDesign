package com.management.server.models;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(	name = "honor")
@Data
public class Honor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String honorId;

    private String time;

    private String name;
    @Size(max=1000)
    private String message;
    @Size(max=1000)
    private String department;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
}