package com.management.server.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "family")
public class Family extends Person{//家人
    private String relationship;
}
