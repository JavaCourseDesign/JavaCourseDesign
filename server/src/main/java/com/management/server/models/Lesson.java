package com.management.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name="lesson")

public class Lesson extends Event{

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    @JsonIgnore
    protected Course course;

    private String ppt;
    @Override
    @Transient
    public Set<Person> getPersons() { //lesson不再重复存储persons，提升课程管理性能
        return course.getPersons();
    }
}
