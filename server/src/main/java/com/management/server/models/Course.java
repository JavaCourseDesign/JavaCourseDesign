package com.management.server.models;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class Course extends Event{
    private String courseCode;
    private String reference;
    private Integer capacity;//课容量
    private Integer credit;//学分
}
