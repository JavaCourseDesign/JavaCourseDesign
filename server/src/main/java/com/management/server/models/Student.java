package com.management.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;


@EqualsAndHashCode(callSuper = true, exclude = {"families","scores","clazz"})//设为true时equals和hashcode方法将考虑/包含父类的属性
@Entity
@Data
@Table(name="student",uniqueConstraints = {@UniqueConstraint(columnNames = {"student_id"})})


public class Student extends Person{

    @Size(max = 20)
    private String studentId;//学号 是否可以通过setPersonId 来代替？ 不能，工号与学号可重复

    @Size(max = 20)
    private String major;


    @Size(max=20)
    private String highSchool;

    @OneToMany(fetch = FetchType.EAGER,cascade = {CascadeType.ALL},orphanRemoval = true)
    @JoinColumn(name = "student_id")
    @JsonIgnore
    @ToString.Exclude
    private List<Family> families;

    @OneToMany(mappedBy = "student",fetch = FetchType.EAGER,cascade = {CascadeType.ALL},orphanRemoval = true)
    //@JoinColumn(name = "student_id")
    @JsonIgnore
    @ToString.Exclude
    private List<Score> scores;

    @ManyToOne
    @JoinColumn(name = "clazz_id")
    @JsonIgnore
    @ToString.Exclude
    private Clazz clazz;

}
