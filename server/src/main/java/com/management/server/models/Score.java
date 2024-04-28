package com.management.server.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
public class Score {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private String scoreId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "studentId")
    @JsonIdentityReference(alwaysAsId = true) // 这行是新添加的
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "courseId")
    @JsonIdentityReference(alwaysAsId = true)
    private Course course;

    //@Transient
    private String homework;

    //@Transient
    private Double absence;

    private Double regularMark;

    private Double finalMark;

    private Double mark;

    //private Integer ranking;
}
