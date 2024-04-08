package com.management.server.models;


import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.security.DomainLoadStoreParameter;
import java.util.List;


@Entity
@Table(name="person")

@Data

//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")//在递归中第二次出现时用name属性替代本对象避免无限递归
//@JsonIgnoreProperties(value = {"courses"})

@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String personId;

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

    @Size(max = 1000)
    private String introduce;

    /*@OneToMany(mappedBy = "person")
    private List<Absence> absences;*/

    /*@OneToMany(mappedBy = "person")
    private List<Honor> honors;*/

    /*@ManyToOne
    @JoinColumn(name = "dormitory_id")
    private Dormitory dormitory;*/

    /*@ManyToMany(mappedBy = "persons")
    private List<Event> events;*/

    /*@ManyToMany(mappedBy = "persons")
    @JsonIgnoreProperties(value = {"persons"})//在单个属性中添加@JsonIgnoreProperties注解，可以忽略 该属性中 某个属性 的序列化和反序列化
    private List<Course> courses;*/ //双向关系是不必要的，可以在course仓库查询实现相同效果


    /*public String getGenderName() {
        return  ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender);
    }*/ //ComDataUtil相关，暂时（或永久）删除

}
