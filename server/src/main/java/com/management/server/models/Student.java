package com.management.server.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;


@Entity
@Data
@Table(name="student",uniqueConstraints = {})
public class Student{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer studentId;
    @Column(name="score")
    Integer score;
    @Column(name="name")
    String name;
}
