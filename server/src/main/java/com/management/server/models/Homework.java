package com.management.server.models;

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
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
    private String homeworkContent;
    private String deadline;
    private String submitTime;
    private String grade;

}
