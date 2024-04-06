package com.management.server.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "administrative_class")
public class AdministrativeClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String administrativeClassId;

    /*@OneToOne
    private Teacher headTeacher;*/ //暂时不考虑班主任

    @OneToMany(mappedBy = "administrativeClass")
    private List<Student> students;

}
