package com.management.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@Entity
@Data
@Table(name = "person")
abstract public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer personId;
    // 字段非空
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
    @Size(max = 2)
    private String gender;
    private String social;
    private String birthday;
    private String schoolTime;
    @Size(max = 60)
    private String email;
    @Size(max = 20)
    private String phone;
    @Size(max = 20)
    private String address;
}
