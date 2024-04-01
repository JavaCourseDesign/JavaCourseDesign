package com.management.server.models;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.security.DomainLoadStoreParameter;
import java.util.List;


/*@Entity
@Table(	name = "person",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "num"),   //人员表中的编号 唯一
        })*/
@Data
@Entity
@Table(name="person")

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")//在递归中第二次出现时用name属性替代本对象避免无限递归

@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer personId;

    //@NotBlank    // 字段非空
    @Size(max = 20)   //字段长度最长为20
    private String num;

    @Size(max = 50)
    private String name;

    @Size(max = 2)
    private String type;

    @Size(max = 50)
    private String dept;

    @Size(max = 20)
    private String card;
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

    @Size(max = 1000)
    private String introduce;

    @OneToMany(mappedBy = "person")
    private List<Absence> absences;

    @OneToMany(mappedBy = "person")
    private List<Honor> honors;

    @ManyToOne
    @JoinColumn(name = "dormitory_id")
    private Dormitory dormitory;

    @ManyToMany(mappedBy = "persons")
    private List<Event> events;

    @ManyToMany(mappedBy = "persons")
    private List<Course> courses;


    /*@ManyToMany
    @JoinTable(name = "event", joinColumns = @JoinColumn(name = "personId"), inverseJoinColumns = @JoinColumn(name = "eventId"))
    private List<Event> events;*/

    public Person() {
    }

    /*public String getGenderName() {
        return  ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender);
    }*/ //ComDataUtil相关，暂时（或永久）删除

    public void setGenderName(String genderName) {
    }

}
