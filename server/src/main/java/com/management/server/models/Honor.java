package com.management.server.models;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(	name = "honor",
        uniqueConstraints = {
        })
@Data
public class Honor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer honorId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    private String time;
    private String honor;
    @Size(max=1000)
    private String message;
    @Size(max=1000)
    private String department;

}