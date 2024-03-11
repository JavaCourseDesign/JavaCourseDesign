package com.management.server.models;

import jakarta.persistence.*;
import lombok.Data;



@Entity
@Data
@Table(name="dormitory")
public class Dormitory {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(name="name")
    String name;
    @Column(name = "num")
    String num;
}
