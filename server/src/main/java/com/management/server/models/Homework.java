package com.management.server.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name="homework")
public class Homework {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String homeworkId;

    private String homeworkContent;
    private LocalDate deadline;
    private LocalDate submitTime;
    private String grade;
    private String homeworkFile;

    @ManyToOne
    @JoinColumn
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonIgnoreProperties(value = {"persons","lessons","willingStudents"})
    private Course course;



}
