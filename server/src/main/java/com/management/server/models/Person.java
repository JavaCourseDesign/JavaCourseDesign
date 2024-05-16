package com.management.server.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Entity
@Table(name="person")

@Data

//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")//在递归中第二次出现时用name属性替代本对象避免无限递归
//@JsonIgnoreProperties(value = {"courses"})
@EqualsAndHashCode(exclude = {"events","honors","courses","absences","dormitory"})//极为重要！跟json ignore搭配才能解决循环引用问题

@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String personId;

    @Size(max = 50)
    private String name;

    @Size(max = 50)
    private String dept;

    @Size(max = 20)
    private String idCardNum;
    @Size(max = 20)
    private String gender;

    private String birthday;
    

    @Size(max = 60)
    @Email
    private String email;

    @Size(max = 20)
    private String phone;

    @Size(max = 20)
    private String address;

    @Size(max=20)
    private String homeTown;

    private String social;
    private String photo;

    @OneToMany(mappedBy = "person")
    @JsonIgnore
    private List<Absence> absences;

    @ManyToOne
    @JoinColumn(name = "dormitory_id")
    @JsonIgnore
    private Dormitory dormitory;

    @ManyToMany(fetch = FetchType.EAGER,mappedBy = "persons")
    @JsonIgnore
    private Set<Event> events;

    @ManyToMany(fetch = FetchType.EAGER,mappedBy = "persons")
    @JsonIgnore
    private List<Honor> honors;

    @ManyToMany(fetch = FetchType.EAGER,mappedBy = "persons")
    @JsonIgnore
    private List<Course> courses;


    /*public String getGenderName() {
        return  ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender);
    }*/ //ComDataUtil相关，暂时（或永久）删除

    public Set<Event> getEvents() { //lesson不再重复存储persons，提升课程管理性能
        Set<Event> events = this.events;
        for(Course course : this.courses) {
            events.addAll(course.getLessons());
        }
        return events;
    }

    public List<Innovation> getInnovations() {
        List<Innovation> innovations = new ArrayList<>();
        for (Event event : events) {
            if(event instanceof Innovation)
                innovations.add((Innovation) event);
        }
        return innovations;
    }

    @JsonIgnore
    public Integer getAge() {
        if(this.birthday == null)
            return -1;
        //获取当前年份
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        //获取出生年份
        String birthYearStr = this.birthday.substring(0,4);
        int birthYear = Integer.parseInt(birthYearStr);
        return year - birthYear;
    }

}
