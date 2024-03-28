/*
package com.management.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
@Table(	name = "user",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "user_name"),
        })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @ManyToOne()
    @JoinColumn(name = "user_type_id")
    private UserType userType;

    @OneToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @NotBlank
    @Size(max = 20)
    private String userName;


    @NotBlank
    @Size(max = 60)
    private String password;

    private Integer loginCount;
    private String lastLoginTime;

    public User() {
    }

    public User(String username, String password) {
        this.userName = username;
        this.password = password;
    }
}
*/
package com.management.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String username;//////在老师的案例中是userName
    private String password;


}
