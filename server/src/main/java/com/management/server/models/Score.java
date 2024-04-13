package com.management.server.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Score {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private String scoreId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private Double regularMark;

    private Double finalMark;

    private Double mark;

    //private Integer ranking;
}
