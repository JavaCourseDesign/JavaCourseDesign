package com.management.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="homework")
public class Homework {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String homeworkId;
    @ManyToOne
    @JoinColumn
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonIgnoreProperties(value = {"persons","lessons","willingStudents"})
    private Course course;
    private String homeworkContent;
    private String deadline;
    private String submitTime;
    private String grade;

}
