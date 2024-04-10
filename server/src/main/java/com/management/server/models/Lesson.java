package com.management.server.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name="lesson")
public class Lesson extends Event{

    //private String beginTime;

    /*@ManyToOne
    @JoinColumn(name="course_id")
    private Course course;*/

    @OneToMany(mappedBy = "lesson")
    private List<Absence> absences;

    //homework?
}
