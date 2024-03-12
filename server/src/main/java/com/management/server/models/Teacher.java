package com.management.server.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="teacher",uniqueConstraints = {})
public class Teacher extends Person{
}
