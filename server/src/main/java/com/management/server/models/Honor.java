package com.management.server.models;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(	name = "honor")
@Data
@EqualsAndHashCode(exclude = {"persons"})
public class Honor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String honorId;
    private LocalDate awardDate;//颁奖时间
    private String name;
    @Size(max=1000)
    private String department;//颁奖部门

    @ManyToMany
    @JoinTable(name = "person_honor")
    @JsonIgnore
    private List<Person> persons;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
}