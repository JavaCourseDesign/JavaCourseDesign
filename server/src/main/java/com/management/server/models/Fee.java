package com.management.server.models;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(	name = "fee")
@Data
public class Fee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String feeId;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    private String time;
    private Double money;
    private String goods;
    private String place;
}
