package com.management.server.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="lesson")
public class Lesson extends Event{
    @ManyToOne
    @JoinColumn(name="course_id")
    private Course course;
}
