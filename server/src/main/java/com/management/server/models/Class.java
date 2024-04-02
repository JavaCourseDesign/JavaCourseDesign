package com.management.server.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "class")
public class Class {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Integer classId;

    @OneToOne
    private Teacher headTeacher;

    @OneToMany(mappedBy = "class")
    private List<Student> students;

}
