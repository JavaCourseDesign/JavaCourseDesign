package com.management.server.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


@Entity
@Data
@Table(name="dormitory",uniqueConstraints = {})
public class Dormitory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String dormitoryId;

    @OneToMany
    private List<Person> persons;

    private Double number;
}
